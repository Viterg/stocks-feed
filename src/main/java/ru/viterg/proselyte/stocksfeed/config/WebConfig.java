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
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.LocalTime;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

@Configuration
@EnableWebFlux
@RequiredArgsConstructor
public class WebConfig {

    private static final String API_KEY_HEADER = "API-KEY";
    @Value("${application.web.max-requests-per-minute}")
    private Long maxRequestsPerMinute = 60L;
    private final ReactiveRedisTemplate<String, Long> reactiveRedisLongTemplate;

    @Bean
    public WebFilter rateLimiterWebFilter() {
        return (request, chain) -> {
            int currentMinute = LocalTime.now().getMinute();
            String key = String.format("rl_%s:%s", extractApikey(request), currentMinute);
            return reactiveRedisLongTemplate.opsForValue().get(key)
                    .flatMap(value -> value >= maxRequestsPerMinute
                                      ? Mono.error(new ResponseStatusException(TOO_MANY_REQUESTS))
                                      : incrAndExpireKey(key, request, chain))
                    .switchIfEmpty(incrAndExpireKey(key, request, chain));
        };
    }

    private static String extractApikey(ServerWebExchange exchange) {
        return exchange.getRequest().getHeaders().getFirst(API_KEY_HEADER);
    }

    private Mono<Void> incrAndExpireKey(String key, ServerWebExchange exchange, WebFilterChain chain) {
        return reactiveRedisLongTemplate.execute(connection -> {
                    ByteBuffer bbKey = ByteBuffer.wrap(key.getBytes(UTF_8));
                    return Mono.zip(connection.numberCommands().incr(bbKey),
                                    connection.keyCommands().expire(bbKey, Duration.ofSeconds(59L)));
                })
                .then(chain.filter(exchange));
    }
}
