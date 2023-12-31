package ru.viterg.proselyte.stocksfeed.stocks;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/sec/stocks")
@RequiredArgsConstructor
public class StocksRestControllerV1 {

    private final StocksService stocksService;

    @GetMapping("/{stock_code}/quote")
    @Operation(summary = "Gets current information about the stock price for the specified company")
    public Mono<BigDecimal> getInformation(@PathVariable("stock_code") @NotBlank String stockCode) {
        return stocksService.getStockInformation(stockCode);
    }

}
