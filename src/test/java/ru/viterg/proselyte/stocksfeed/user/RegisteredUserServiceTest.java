package ru.viterg.proselyte.stocksfeed.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
class RegisteredUserServiceTest {

    @InjectMocks
    private RegisteredUserService registeredUserService;
    @Mock
    private RegisteredUserRepository repository;
    @Mock
    private PasswordEncoder encoder;

    @Test
    @DisplayName("should find user by username")
    void findByUsername() {
        Mono<UserDetails> user = registeredUserService.findByUsername("user");
    }

    @Test
    void activateRegistration() {
    }

    @Test
    void saveNew() {
    }

    @Test
    void generateApiToken() {
    }
}