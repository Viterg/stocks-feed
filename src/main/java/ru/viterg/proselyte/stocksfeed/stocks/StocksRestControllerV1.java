package ru.viterg.proselyte.stocksfeed.stocks;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
public class StocksRestControllerV1 {

    private final StocksService stocksService;

    @GetMapping("/{stock_code}/quote")
    // TODO Access with api-key through filter
    @Operation(summary = "Gets current information about the stock price for the specified company.",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "500")
            })
    public String getInformation(@PathVariable("stock_code") @NotBlank String stockCode) {
        return stocksService.getStockInformation(stockCode);
    }

}
