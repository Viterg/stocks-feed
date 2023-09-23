package ru.viterg.proselyte.stocksfeed.stocks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StocksRestControllerV1Test {

    @InjectMocks
    private StocksRestControllerV1 stocksRestControllerV1;
    @Mock
    private StocksService stocksService;
    private WebTestClient testClient;

    @BeforeEach
    void setUp() {
        testClient = WebTestClient.bindToController(stocksRestControllerV1).build();
    }

    @Test
    @DisplayName("should return current stock value")
    void getInformation() {
        BigDecimal expected = BigDecimal.valueOf(105L);
        when(stocksService.getStockInformation("AAPL")).thenReturn(Mono.just(expected));

        testClient.get()
                .uri("/api/v1/sec/stocks/AAPL/quote")
                .exchange()
                .expectStatus().isOk()
                .expectBody(BigDecimal.class)
                .value(value -> assertEquals(expected, value));
    }
}