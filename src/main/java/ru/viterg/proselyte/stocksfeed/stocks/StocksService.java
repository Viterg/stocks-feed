package ru.viterg.proselyte.stocksfeed.stocks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StocksService {

    public String getStockInformation(String stockCode) {
        return stockCode;
    }
}
