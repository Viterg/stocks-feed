package ru.viterg.proselyte.stocksfeed.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
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
    private final ReactiveRedisTemplate<String, Long> redisTemplate;

    @Bean
    public HandlerFilterFunction<ServerResponse, ServerResponse> rateLimiterHandlerFilterFunction() {
        return (request, next) -> {
            int currentMinute = LocalTime.now().getMinute();
            String key = String.format("rl_%s:%s", extractApikey(request), currentMinute);
            return redisTemplate.opsForValue().get(key)
                    .flatMap(value -> value >= maxRequestsPerMinute
                                      ? ServerResponse.status(TOO_MANY_REQUESTS).build()
                                      : incrAndExpireKey(key, request, next))
                    .switchIfEmpty(incrAndExpireKey(key, request, next));
        };
    }

    private static String extractApikey(ServerRequest request) {
        return request.headers().header(API_KEY_HEADER).get(0);
    }

    private Mono<ServerResponse> incrAndExpireKey(String key, ServerRequest request,
            HandlerFunction<ServerResponse> next) {
        return redisTemplate.execute(connection -> {
                    ByteBuffer bbKey = ByteBuffer.wrap(key.getBytes(UTF_8));
                    return Mono.zip(connection.numberCommands().incr(bbKey),
                                    connection.keyCommands().expire(bbKey, Duration.ofSeconds(59L)))
                            .then(Mono.empty());
                })
                .then(next.handle(request));
    }
}
