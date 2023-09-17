package ru.viterg.proselyte.stocksfeed.stocks;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.BASIC_ISO_DATE;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
class StocksService {

    private final StocksRedisAccessor stocksRedisAccessor;

    @PostConstruct
    // this is for test purpose
    public void init() {
        String ticker = "AAPL";
        Stock stock = new Stock();
        stock.setStock(BigDecimal.valueOf(147.92));
        stock.setTicker(ticker);
        stock.setActualOn(Instant.parse("2023-08-25T10:15:30.00Z"));
        stocksRedisAccessor.set(formatKey(ticker), stock).block();

        ticker = "MSFT";
        stock = new Stock();
        stock.setStock(BigDecimal.valueOf(216.83));
        stock.setTicker(ticker);
        stock.setActualOn(Instant.parse("2023-08-25T10:15:30.00Z"));
        stocksRedisAccessor.set(formatKey(ticker), stock).block();

        ticker = "GOGL";
        stock = new Stock();
        stock.setStock(BigDecimal.valueOf(194.47));
        stock.setTicker(ticker);
        stock.setActualOn(Instant.parse("2023-08-25T10:15:30.00Z"));
        stocksRedisAccessor.set(formatKey(ticker), stock).block();
    }

    public Mono<BigDecimal> getStockInformation(String stockCode) {
        return stocksRedisAccessor.get(formatKey(stockCode))
                .switchIfEmpty(Mono.error(new ResponseStatusException(NOT_FOUND)))
                .map(Stock::getStock);
    }

    private static String formatKey(String stockCode) {
        return String.format("stocks:%s@%s", stockCode, LocalDate.of(2023, 8, 25).format(BASIC_ISO_DATE));
    }
}
