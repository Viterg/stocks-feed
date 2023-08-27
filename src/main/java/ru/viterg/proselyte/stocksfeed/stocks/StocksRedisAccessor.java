package ru.viterg.proselyte.stocksfeed.stocks;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class StocksRedisAccessor {

    private final ReactiveRedisOperations<String, Stock> template;

    /**
     * Set key and value into a hash key
     *
     * @param key     key value - must not be null.
     * @param hashKey hash key value -  must not be null.
     * @param val     Stock value
     * @return Mono of object
     */
    public Mono<Stock> set(String key, String hashKey, Stock val) {
        return template.opsForHash().put(key, hashKey, val).map(b -> val);
    }

    /**
     * @param key key value - must not be null.
     * @return Flux of Stock
     */
    public Flux<Object> get(@NotNull String key) {
        return template.opsForHash().values(key);
    }

    /**
     * Get value for given hashKey from hash at key.
     *
     * @param key     key value - must not be null.
     * @param hashKey hash key value -  must not be null.
     * @return Stock
     */
    public Mono<Object> get(String key, Stock hashKey) {
        return template.opsForHash().get(key, hashKey);
    }

    /**
     * Delete a key that contained in a hash key.
     *
     * @param key     key value - must not be null.
     * @param hashKey hash key value -  must not be null.
     * @return 1 Success or 0 Error
     */
    public Mono<Long> remove(String key, String hashKey) {
        return template.opsForHash().remove(key, hashKey);
    }
}