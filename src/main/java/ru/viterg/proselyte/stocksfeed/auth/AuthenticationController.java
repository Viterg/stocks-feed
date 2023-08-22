package ru.viterg.proselyte.stocksfeed.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.viterg.proselyte.stocksfeed.security.JwtService;
import ru.viterg.proselyte.stocksfeed.service.RegisteredUserService;
import ru.viterg.proselyte.stocksfeed.user.Role;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final JwtService jwtService;
    private final PasswordEncoder encoder;
    private final RegisteredUserService userService;

    @PostMapping("/register")
    @Operation(summary = "Registers a new user in the system.",
            responses = {
                    @ApiResponse(responseCode = "201"),
                    @ApiResponse(responseCode = "409"),
                    @ApiResponse(responseCode = "500")
            })
    public Mono<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {
        String username = request.getEmail();
        return userService.findByUsername(username)
                .hasElement()
                .flatMap(hasElement -> {
                    if (hasElement) {
                        return Mono.error(
                                new ResponseStatusException(CONFLICT, "User " + username + " already exists!"));
                    } else {
                        return userService.saveNew(username, request.getPassword(), request.getRole())
                                .map(ud -> RegisterResponse.builder()
                                        .email(ud.getUsername())
                                        .role(Role.valueOf(ud.getRole()))
                                        .build());
                    }
                });
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticates user in the system.",
            responses = {
                    @ApiResponse(responseCode = "201"),
                    @ApiResponse(responseCode = "401"),
                    @ApiResponse(responseCode = "500")
            })
    public Mono<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        var username = request.getEmail();
        var password = request.getPassword();
        return userService.findByUsername(username)
                .filter(ud -> encoder.matches(password, ud.getPassword()))
                .switchIfEmpty(Mono.error(new ResponseStatusException(UNAUTHORIZED)))
                .map(ud -> new AuthenticationResponse(jwtService.generateToken(ud).getToken()));
    }

    @PostMapping("/get-api-key")
    @Operation(summary = "Gets a unique API key for a registered user.",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "500")
            })
    public Mono<String> getApiKey(@RequestBody @Valid AuthenticationRequest request) {
        return Mono.empty();
    }

}
