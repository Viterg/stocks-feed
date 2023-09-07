package ru.viterg.proselyte.stocksfeed.companies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CompaniesRestControllerV1Test {

    private final CompaniesRepository companiesRepository = mock(CompaniesRepository.class);
    private WebTestClient testClient;

    @BeforeEach
    void setUp() {
        testClient = WebTestClient.bindToController(new CompaniesRestControllerV1(companiesRepository)).build();
    }

    @Test
    @DisplayName("should return companies tickers")
    void getInformation() {
        when(companiesRepository.findAll()).thenReturn(Flux.just(new Company(1, "TICKER1"), new Company(2, "TICKER2")));

        testClient.get()
                .uri("/api/v1/sec/companies")
                .exchange()
                .expectStatus().isOk()
                .expectBody(List.class)
                .value(list -> {
                    assertEquals("TICKER1", list.get(0));
                    assertEquals("TICKER2", list.get(1));
                });
    }
}