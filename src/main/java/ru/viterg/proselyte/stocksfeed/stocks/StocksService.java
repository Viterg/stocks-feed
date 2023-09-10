package ru.viterg.proselyte.stocksfeed.stocks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.BASIC_ISO_DATE;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
class StocksService {

    private final StocksRedisAccessor stocksRedisAccessor;

    public Mono<BigDecimal> getStockInformation(String stockCode) {
        return stocksRedisAccessor.get(formatKey(stockCode))
                .switchIfEmpty(Mono.error(new ResponseStatusException(NOT_FOUND)))
                .map(Stock::getStock);
    }

    private static String formatKey(String stockCode) {
        return String.format("stocks:%s@%s", stockCode, LocalDate.of(2023, 8, 24).format(BASIC_ISO_DATE));
    }
}
