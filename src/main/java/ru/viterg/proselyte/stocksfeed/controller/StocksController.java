package ru.viterg.proselyte.stocksfeed.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.viterg.proselyte.stocksfeed.service.StocksService;

import static io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP;

@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
@SecurityScheme(name = "basic-auth", scheme = "basic", type = HTTP, description = "Basic auth for all endpoints")
public class StocksController {

    private final StocksService stocksService;

    @GetMapping("/{stock_code}/quote")
    @PreAuthorize("hasAuthority('can_read_stocks')")
    @Operation(summary = "Gets current information about the stock price for the specified company.",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "500")
            })
    public String getInformation(@PathVariable("stock_code") @NotBlank String stockCode,
            @RequestHeader("apikey") @NotBlank String apiKey) {
        return stocksService.getStockInformation(stockCode, apiKey);
    }

}
