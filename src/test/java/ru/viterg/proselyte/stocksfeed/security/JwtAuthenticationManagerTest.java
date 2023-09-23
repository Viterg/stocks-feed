package ru.viterg.proselyte.stocksfeed.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.viterg.proselyte.stocksfeed.user.RegisteredUser;

import java.util.Objects;

import static org.mockito.Mockito.when;
import static ru.viterg.proselyte.stocksfeed.user.Role.AUTHORIZED_REGULAR;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationManagerTest {

    @InjectMocks
    private JwtAuthenticationManager jwtAuthenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private ReactiveUserDetailsService userDetailsService;

    private String jwt;
    private String username;
    private BearerToken authentication;
    private RegisteredUser registeredUser;

    @BeforeEach
    void setUp() {
        jwt = "token";
        username = "user";
        authentication = new BearerToken(jwt);
        registeredUser = new RegisteredUser();
        registeredUser.setUsername(username);
        registeredUser.setRole(AUTHORIZED_REGULAR);
    }

    @Test
    @DisplayName("should return authenticated found verified user")
    void authenticate() {
        when(jwtService.extractUsername(jwt)).thenReturn(username);
        when(userDetailsService.findByUsername(username)).thenReturn(Mono.just(registeredUser));
        when(jwtService.isTokenValid(jwt, registeredUser)).thenReturn(true);

        StepVerifier.create(jwtAuthenticationManager.authenticate(authentication))
                .expectNextMatches(auth -> (auth instanceof UsernamePasswordAuthenticationToken)
                                   && Objects.equals(registeredUser, auth.getPrincipal()))
                .verifyComplete();
    }

    @Test
    @DisplayName("should return error due to found user was not verified")
    void nonValidatedAuthentication() {
        when(jwtService.extractUsername(jwt)).thenReturn(username);
        when(userDetailsService.findByUsername(username)).thenReturn(Mono.just(registeredUser));
        when(jwtService.isTokenValid(jwt, registeredUser)).thenReturn(false);

        StepVerifier.create(jwtAuthenticationManager.authenticate(authentication))
                .expectErrorMatches(t -> (t instanceof AuthenticationServiceException))
                .verify();
    }

    @Test
    @DisplayName("should return error due to user was not found")
    void nonExistedAuthentication() {
        when(jwtService.extractUsername(jwt)).thenReturn(username);
        when(userDetailsService.findByUsername(username)).thenReturn(Mono.empty());

        StepVerifier.create(jwtAuthenticationManager.authenticate(authentication))
                .expectErrorMatches(t -> (t instanceof AuthenticationServiceException))
                .verify();
    }
}