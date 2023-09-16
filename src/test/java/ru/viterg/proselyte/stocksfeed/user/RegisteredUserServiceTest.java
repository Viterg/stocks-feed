package ru.viterg.proselyte.stocksfeed.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.viterg.proselyte.stocksfeed.user.Role.AUTHORIZED_NEW;
import static ru.viterg.proselyte.stocksfeed.user.Role.AUTHORIZED_REGULAR;

@ExtendWith(SpringExtension.class)
class RegisteredUserServiceTest {

    @InjectMocks
    private RegisteredUserService registeredUserService;
    private final RegisteredUserRepository repository = mock(RegisteredUserRepository.class);
    private final PasswordEncoder encoder = mock(PasswordEncoder.class);

    private String username;
    private String password;
    private RegisteredUser registeredUser;

    @BeforeEach
    void setUp() {
        username = "user";
        password = "password";
        registeredUser = new RegisteredUser();
        registeredUser.setUsername(username);
        registeredUser.setPassword(password);
        registeredUser.setActive(true);
    }

    @Test
    @DisplayName("should return found user by username")
    void findByUsername() {
        when(repository.findByUsername(username)).thenReturn(Mono.just(registeredUser));

        StepVerifier.create(registeredUserService.findByUsername(username))
                .expectNextMatches(ud -> Objects.equals(username, ud.getUsername()))
                .verifyComplete();
    }

    @Test
    @DisplayName("should return activated user")
    void activateRegistration() {
        registeredUser.setRole(AUTHORIZED_NEW);
        registeredUser.setActive(false);

        var updatedUser = new RegisteredUser();
        updatedUser.setUsername(username);
        updatedUser.setActive(true);
        updatedUser.setRole(AUTHORIZED_REGULAR);

        String key = "key";
        when(repository.findByActivationKey(key)).thenReturn(Mono.just(registeredUser));
        when(repository.save(registeredUser)).thenReturn(Mono.just(updatedUser));

        StepVerifier.create(registeredUserService.activateRegistration(key))
                .expectNextMatches(ud -> Objects.equals(username, ud.getUsername())
                                         && ud.isActive()
                                         && ud.getRole() == AUTHORIZED_REGULAR)
                .verifyComplete();
    }

    @Test
    @DisplayName("should return created user")
    void saveNew() {
        String email = username + "@mail.com";
        registeredUser.setEmail(email);
        registeredUser.setRole(AUTHORIZED_NEW);

        when(encoder.encode(password)).thenReturn(password);
        when(repository.save(any())).thenReturn(Mono.just(registeredUser));

        StepVerifier.create(registeredUserService.saveNew(username, email, password))
                .expectNextMatches(ud -> Objects.equals(username, ud.getUsername())
                                         && Objects.equals(email, ud.getEmail())
                                         && Objects.equals(password, ud.getPassword())
                                         && AUTHORIZED_NEW == ud.getRole())
                .verifyComplete();
    }

    @Test
    @DisplayName("should return generated API key")
    void generateApiToken() {
        var updatedUser = new RegisteredUser();
        updatedUser.setApiKey(UUID.randomUUID().toString());

        when(repository.findByUsername(username)).thenReturn(Mono.just(registeredUser));
        when(repository.save(registeredUser)).thenReturn(Mono.just(updatedUser));

        StepVerifier.create(registeredUserService.generateApiToken(username))
                .expectNextMatches(token -> (UUID.fromString(token) != null))
                .verifyComplete();
    }

    @Test
    @DisplayName("should return validated user")
    void getValidatedUser() {
        when(repository.findByUsername(username)).thenReturn(Mono.just(registeredUser));
        when(encoder.matches(password, registeredUser.getPassword())).thenReturn(true);

        StepVerifier.create(registeredUserService.getValidatedUser(username, password))
                .expectNextMatches(ud -> Objects.equals(username, ud.getUsername()))
                .verifyComplete();
    }

    @Test
    @DisplayName("should return empty when user has invalid password")
    void getNonValidUser() {
        when(repository.findByUsername(username)).thenReturn(Mono.just(registeredUser));
        when(encoder.matches(password, registeredUser.getPassword())).thenReturn(false);

        StepVerifier.create(registeredUserService.getValidatedUser(username, password))
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    @DisplayName("should return empty when user is not active")
    void getNonActiveUser() {
        registeredUser.setActive(false);

        when(repository.findByUsername(username)).thenReturn(Mono.just(registeredUser));
        when(encoder.matches(password, registeredUser.getPassword())).thenReturn(true);

        StepVerifier.create(registeredUserService.getValidatedUser(username, password))
                .expectNextCount(0L)
                .verifyComplete();
    }
}