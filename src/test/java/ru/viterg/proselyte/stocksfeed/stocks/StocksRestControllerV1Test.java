package ru.viterg.proselyte.stocksfeed.stocks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StocksRestControllerV1Test {

    private final StocksService stocksService = mock(StocksService.class);
    private WebTestClient testClient;

    @BeforeEach
    void setUp() {
        testClient = WebTestClient.bindToController(new StocksRestControllerV1(stocksService)).build();
    }

    @Test
    @DisplayName("should return current stock value")
    void getInformation() {
        BigDecimal expected = BigDecimal.valueOf(105L);
        when(stocksService.getStockInformation("AAPL")).thenReturn(Mono.just(expected));

        testClient.get()
                .uri("/api/v1/stocks/AAPL/quote")
                .exchange()
                .expectStatus().isOk()
                .expectBody(BigDecimal.class)
                .value(value -> assertEquals(expected, value));
    }
}