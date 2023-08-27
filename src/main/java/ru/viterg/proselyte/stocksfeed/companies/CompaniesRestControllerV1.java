package ru.viterg.proselyte.stocksfeed.companies;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompaniesRestControllerV1 {

    private final CompaniesRepository companiesRepository;

    @GetMapping
    @Operation(summary = "Gets background information about available trading companies.",
            security = @SecurityRequirement(name = "basic-auth"),
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "403"),
                    @ApiResponse(responseCode = "500")
            })
    public Mono<List<String>> getInformation() {
        return companiesRepository.findAll()
                .map(Company::getTicker)
                .collectList();
    }

}
