package ru.viterg.proselyte.stocksfeed.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.pattern.PathPattern;
import reactor.core.publisher.Mono;
import ru.viterg.proselyte.stocksfeed.user.RegisteredUserRepository;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

@Configuration
@EnableWebFlux
@RequiredArgsConstructor
public class WebConfig {

    private static final String API_KEY_HEADER = "API-KEY";
    private final ReactiveRedisTemplate<String, Long> reactiveRedisLongTemplate;
    private final RegisteredUserRepository repository;
    @Value("${application.web.max-requests-per-minute}")
    private Long maxRequestsPerMinute = 60L;

    @Bean
    public WebFilter apikeyLimiterWebFilter(PathPattern apikeyPathPattern, Duration apiKeyExpiration) {
        return (exchange, chain) -> {
            if (apikeyPathPattern.matches(exchange.getRequest().getPath())) {
                String apikey = extractApikey(exchange);
                return checkApiKeyExistence(apikey, apiKeyExpiration)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(FORBIDDEN)))
                        .flatMap(udCount -> {
                            String key = String.format("rl_%s:%s", apikey, LocalTime.now().getMinute());
                            return checkLimit(key, exchange, chain);
                        });
            }
            return chain.filter(exchange);
        };
    }

    private Mono<Long> checkApiKeyExistence(String apikey, Duration apiKeyExpiration) {
        return reactiveRedisLongTemplate.opsForValue().get(apikey)
                .switchIfEmpty(repository.findByApiKey(apikey)
                                       .flatMap(ud -> Objects.isNull(ud) ? Mono.empty() : Mono.just(1L))
                                       .flatMap(count -> saveApiKeyCount(apikey, apiKeyExpiration, count)));
    }

    private Mono<Long> saveApiKeyCount(String apikey, Duration apiKeyExpiration, Long count) {
        return reactiveRedisLongTemplate.opsForValue().set(apikey, count, apiKeyExpiration).map(b -> count);
    }

    private Mono<Void> checkLimit(String key, ServerWebExchange exchange, WebFilterChain chain) {
        return reactiveRedisLongTemplate.opsForValue().get(key)
                .flatMap(value -> value >= maxRequestsPerMinute
                                  ? Mono.error(new ResponseStatusException(TOO_MANY_REQUESTS))
                                  : incrAndExpireKey(key, exchange, chain))
                .switchIfEmpty(incrAndExpireKey(key, exchange, chain));
    }

    private Mono<Void> incrAndExpireKey(String key, ServerWebExchange exchange, WebFilterChain chain) {
        return reactiveRedisLongTemplate.execute(connection -> {
                    ByteBuffer bbKey = ByteBuffer.wrap(key.getBytes(UTF_8));
                    return Mono.zip(connection.numberCommands().incr(bbKey),
                                    connection.keyCommands().expire(bbKey, Duration.ofSeconds(59L)));
                })
                .then(chain.filter(exchange));
    }

    private static String extractApikey(ServerWebExchange exchange) {
        return exchange.getRequest().getHeaders().getFirst(API_KEY_HEADER);
    }

    @Bean
    public Duration apiKeyExpiration(@Value("${application.redis.api-key.expiration}") String expiration) {
        return Duration.parse(expiration);
    }
}
