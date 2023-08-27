package ru.viterg.proselyte.stocksfeed.stocks;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface StocksRepository extends ReactiveCrudRepository<Stock, String> {
}
