package ru.viterg.proselyte.stocksfeed.stocks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class StocksRedisAccessor {

    private final ReactiveRedisOperations<String, Stock> template;
    private final Duration stocksKeyExpiration;

    /**
     * Get value for given hashKey from hash at key.
     *
     * @param key key value - must not be null.
     * @return Stock.
     */
    public Mono<Stock> get(String key) {
        return template.opsForValue().get(key).onErrorStop();
    }

    /**
     * Set key and value into a hash key.
     *
     * @param key key value - must not be null.
     * @param val Stock value.
     * @return Mono of object.
     */
    public Mono<Stock> set(String key, Stock val) {
        return template.opsForValue().set(key, val, stocksKeyExpiration).map(b -> val);
    }

    /**
     * Delete a key that contained in a hash key.
     *
     * @param key key value - must not be null.
     * @return Mono of true if success or false otherwise.
     */
    public Mono<Boolean> remove(String key) {
        return template.opsForValue().delete(key);
    }
}
