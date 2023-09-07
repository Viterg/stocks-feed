package ru.viterg.proselyte.stocksfeed.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ru.viterg.proselyte.stocksfeed.stocks.Stock;

import java.time.Duration;

@Configuration
public class RedisConfiguration {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public ReactiveRedisOperations<String, Stock> reactiveRedisStockTemplate(ReactiveRedisConnectionFactory factory) {
        var context = RedisSerializationContext
                .<String, Stock>newSerializationContext(new StringRedisSerializer())
                .value(new Jackson2JsonRedisSerializer<>(Stock.class))
                .build();
        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    public ReactiveRedisTemplate<String, Long> reactiveRedisLongTemplate(ReactiveRedisConnectionFactory factory) {
        var context = RedisSerializationContext
                .<String, Long>newSerializationContext(new JdkSerializationRedisSerializer())
                .key(StringRedisSerializer.UTF_8)
                .value(new GenericToStringSerializer<>(Long.class))
                .build();
        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    public Duration stocksKeyExpiration(@Value("${application.redis.stocks.expiration}") String expiration) {
        return Duration.parse(expiration);
    }

    @Bean(destroyMethod = "shutdown")
    public RedissonReactiveClient redisson() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://%s:%s".formatted(redisHost, redisPort));
        return Redisson.create(config).reactive();
    }
}

