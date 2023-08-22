package ru.viterg.proselyte.stocksfeed.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.viterg.proselyte.stocksfeed.client.StocksClient;
import ru.viterg.proselyte.stocksfeed.client.StocksInformationMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class StocksService {

    private final StocksClient stocksClient;
    private final StocksInformationMapper stocksInformationMapper;

    @Cacheable(cacheNames = "stocks")
    public String getStockInformation(String stockCode, String apiKey) {
        return null;
    }
}
