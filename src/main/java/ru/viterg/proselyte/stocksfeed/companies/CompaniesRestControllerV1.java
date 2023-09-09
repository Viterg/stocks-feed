package ru.viterg.proselyte.stocksfeed.companies;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sec/companies")
@RequiredArgsConstructor
public class CompaniesRestControllerV1 {

    private final CompaniesRepository companiesRepository;

    @GetMapping
    @Operation(summary = "Gets background information about available trading companies")
    public Mono<List<String>> getInformation() {
        return companiesRepository.findAll()
                .map(Company::getTicker)
                .collectList();
    }

}
