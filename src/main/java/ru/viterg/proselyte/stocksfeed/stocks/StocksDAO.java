package ru.viterg.proselyte.stocksfeed.stocks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@RequiredArgsConstructor
public class StocksDAO {

    private static final String KEY = "*";
    private final StocksRedisAccessor stocksRedisAccessor;

    public Mono<Stock> save(Stock stock) {
        return stocksRedisAccessor.set(KEY, stock.getId(), stock).map(b -> stock);
    }

    public Mono<Stock> get(String key) {
        return Mono.empty();
//        return stocksRedisAccessor.get(KEY, key)
//                .flatMap(obj -> Mono.just(ObjectMapperUtils.objectMapper(obj, Stock.class)));
    }

    public Flux<Stock> getAll() {
        return Flux.empty();
//        return stocksRedisAccessor.get(KEY).map(b -> ObjectMapperUtils.objectMapper(b, Stock.class))
//                .collectList().flatMapMany(Flux::fromIterable);
    }

    public Mono<Long> delete(String id) {
        return stocksRedisAccessor.remove(KEY, id);
    }
}