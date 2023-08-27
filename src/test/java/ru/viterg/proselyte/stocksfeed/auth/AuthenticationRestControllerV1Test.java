package ru.viterg.proselyte.stocksfeed.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.viterg.proselyte.stocksfeed.security.BearerToken;
import ru.viterg.proselyte.stocksfeed.security.JwtService;
import ru.viterg.proselyte.stocksfeed.service.MailService;
import ru.viterg.proselyte.stocksfeed.user.RegisteredUser;
import ru.viterg.proselyte.stocksfeed.user.RegisteredUserService;
import ru.viterg.proselyte.stocksfeed.user.Role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthenticationRestControllerV1Test {

    private final RegisteredUserService userService = mock(RegisteredUserService.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final PasswordEncoder encoder = mock(PasswordEncoder.class);
    private final MailService mailService = mock(MailService.class);
    private WebTestClient testClient;

    @BeforeEach
    void setUp() {
        testClient = WebTestClient.bindToController(
                        new AuthenticationRestControllerV1(userService, jwtService, encoder, mailService))
                .build();
    }

    @Test
    @DisplayName("should create new user and return its email and role")
    void register() {
        String username = "user";
        String password = "pass4";
        String email = "email@mail.com";

        RegisterRequest request = RegisterRequest.builder()
                .username(username)
                .password(password)
                .email(email)
                .build();

        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setUsername(username);
        registeredUser.setEmail(email);
        registeredUser.setRole(Role.AUTHORIZED_NEW.name());
        registeredUser.setActivationKey("key");

        when(userService.findByUsername(username)).thenReturn(Mono.empty());
        when(userService.saveNew(username, password, email)).thenReturn(Mono.just(registeredUser));
        doNothing().when(mailService).sendActivationMail(email, registeredUser.getActivationKey());

        testClient.post()
                .uri("/api/v1/auth/register")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(RegisterResponse.class)
                .value(response -> {
                    assertEquals(email, response.getEmail());
                    assertEquals(Role.AUTHORIZED_NEW, response.getRole());
                })
                .returnResult();
    }

    @Test
    @DisplayName("should return 409 if new user already exists")
    void registerConflict() {
        String username = "user";
        String password = "pass4";
        String email = "email@mail.com";

        RegisterRequest request = RegisterRequest.builder()
                .username(username)
                .password(password)
                .email(email)
                .build();

        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setUsername(username);
        registeredUser.setEmail(email);
        registeredUser.setRole(Role.AUTHORIZED_NEW.name());
        registeredUser.setActivationKey("key");

        when(userService.findByUsername(username)).thenReturn(Mono.just(registeredUser));

        testClient.post()
                .uri("/api/v1/auth/register")
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("should activate user and return nothing")
    void activateAccount() {
        String activationKey = "key";

        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setUsername("user");
        registeredUser.setActive(true);

        when(userService.activateRegistration(activationKey)).thenReturn(Mono.just(registeredUser));

        testClient.patch()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/auth/confirm")
                        .queryParam("key", activationKey)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class)
                .returnResult();
    }

    @Test
    @DisplayName("should authenticate user and return its JWT token")
    void authenticate() {
        String username = "user";
        String password = "pass4";

        AuthenticationRequest request = AuthenticationRequest.builder()
                .username(username)
                .password(password)
                .build();

        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setUsername(username);
        registeredUser.setPassword("encodedPass");
        registeredUser.setActive(true);

        when(userService.findByUsername(username)).thenReturn(Mono.just(registeredUser));
        when(encoder.matches(password, registeredUser.getPassword())).thenReturn(true);
        when(jwtService.generateToken(registeredUser)).thenReturn(new BearerToken("token"));

        testClient.post()
                .uri("/api/v1/auth/login")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthenticationResponse.class)
                .value(response -> {
                    assertEquals("token", response.getAccessToken());
                })
                .returnResult();
    }

    @Test
    @WithMockUser(authorities = "CAN_GENERATE_TOKEN")
    @DisplayName("should generate API key for current user")
    void getApiKey() {
        when(userService.generateApiToken(any())).thenReturn(Mono.just("api-key"));

        testClient.post()
                .uri("/api/v1/auth/get-api-key")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(response -> {
                    assertEquals("api-key", response);
                })
                .returnResult();
    }
}