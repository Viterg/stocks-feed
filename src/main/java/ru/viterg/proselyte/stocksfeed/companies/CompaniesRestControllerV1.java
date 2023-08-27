package ru.viterg.proselyte.stocksfeed.companies;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
@SecurityScheme(name = "basic-auth", scheme = "basic", type = HTTP, description = "Basic auth for all endpoints")
public class CompaniesRestControllerV1 {

    @GetMapping
    @PreAuthorize("hasAuthority('can_read_stocks')")
    @Operation(summary = "Gets background information about available trading companies.",
            security = @SecurityRequirement(name = "basic-auth"),
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "500")
            })
    public String getInformation() {
        return "Information";
    }

}
