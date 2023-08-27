package ru.viterg.proselyte.stocksfeed.config;

import org.redisson.Redisson;
import org.redisson.api.RRateLimiterReactive;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ru.viterg.proselyte.stocksfeed.stocks.Stock;

@Configuration
public class RedisConfiguration {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public ReactiveRedisConnectionFactory lettuceConnectionFactory() {
        return new LettuceConnectionFactory(redisHost, redisPort);
    }

    @Bean
    public ReactiveRedisOperations<String, Stock> reactiveRedisStockTemplate(
            ReactiveRedisConnectionFactory lettuceConnectionFactory) {
        RedisSerializer<Stock> serializer = new Jackson2JsonRedisSerializer<>(Stock.class);
        RedisSerializationContext<String, Stock> context = RedisSerializationContext
                .<String, Stock>newSerializationContext(new StringRedisSerializer())
                .value(serializer)
                .hashValue(serializer)
                .hashKey(serializer)
                .build();
        return new ReactiveRedisTemplate<>(lettuceConnectionFactory, context);
    }


    @Bean(destroyMethod = "shutdown")
    public RedissonReactiveClient redisson() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://%s:%s".formatted(redisHost, redisPort));
        return Redisson.create(config).reactive();
    }

    @Bean
    public RRateLimiterReactive rateLimiter(RedissonReactiveClient redisson) {
        RRateLimiterReactive limiter = redisson.getRateLimiter("myLimiter");
        limiter.trySetRate(RateType.OVERALL, 1, 1, RateIntervalUnit.SECONDS); // 1 RPS
        return limiter;
    }

}

