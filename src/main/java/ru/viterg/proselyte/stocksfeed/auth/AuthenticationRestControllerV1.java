package ru.viterg.proselyte.stocksfeed.auth;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.viterg.proselyte.stocksfeed.security.JwtService;
import ru.viterg.proselyte.stocksfeed.service.MailService;
import ru.viterg.proselyte.stocksfeed.user.RegisteredUserService;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationRestControllerV1 {

    private final RegisteredUserService userService;
    private final JwtService jwtService;
    private final MailService mailService;

    @PostMapping("/register")
    @ResponseStatus(CREATED)
    @Operation(summary = "Registers a new user in the system")
    public Mono<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {
        String username = request.getUsername();
        return userService.findByUsername(username)
                .hasElement()
                .flatMap(hasElement -> {
                    if (hasElement) {
                        return Mono.error(
                                new ResponseStatusException(CONFLICT, "User " + username + " already exists!"));
                    } else {
                        return userService.saveNew(username, request.getEmail(), request.getPassword())
                                .doOnNext(ud -> mailService.sendActivationMail(ud.getEmail(), ud.getActivationKey()))
                                .map(ud -> new RegisterResponse(ud.getEmail(), ud.getRole()));
                    }
                });
    }

    @PatchMapping("/confirm")
    @Operation(summary = "Confirms and activates new registered user in the system")
    public Mono<Void> activateAccount(@RequestParam("key") String key) {
        return userService.activateRegistration(key)
                .switchIfEmpty(Mono.error(new ResponseStatusException(NOT_FOUND, "Activation key not found!")))
                .then();
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticates user in the system")
    public Mono<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        return userService.getValidatedUser(request.getUsername(), request.getPassword())
                .switchIfEmpty(Mono.error(new ResponseStatusException(UNAUTHORIZED)))
                .map(ud -> new AuthenticationResponse(jwtService.generateToken(ud).getToken()));
    }

    @PostMapping("/get-api-key")
    @PreAuthorize("hasAuthority('CAN_GENERATE_TOKEN')")
    @Operation(summary = "Gets a unique API key for a registered user")
    public Mono<String> getApiKey(Mono<Authentication> authentication) {
        return authentication.flatMap(auth -> userService.generateApiToken(auth.getName()));
    }
}
