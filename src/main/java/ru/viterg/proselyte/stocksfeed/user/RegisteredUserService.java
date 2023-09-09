package ru.viterg.proselyte.stocksfeed.user;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static ru.viterg.proselyte.stocksfeed.user.Role.AUTHORIZED_NEW;
import static ru.viterg.proselyte.stocksfeed.user.Role.AUTHORIZED_REGULAR;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RegisteredUserService implements ReactiveUserDetailsService {

    private final RegisteredUserRepository repository;
    private final PasswordEncoder encoder;

    @Override
    @Transactional(readOnly = true)
    public Mono<UserDetails> findByUsername(@NotBlank String username) {
        return repository.findByUsername(username.toLowerCase());
    }

    /**
     * Activates the registration of a user using the specified activation key.
     *
     * @param key the activation key for the user registration
     * @return the activated user
     */
    public Mono<RegisteredUser> activateRegistration(@NotBlank String key) {
        return repository.findByActivationKey(key)
                .doOnNext(ud -> ud.setActive(true))
                .doOnNext(ud -> ud.setActivationKey(""))
                .doOnNext(ud -> ud.setRole(AUTHORIZED_REGULAR))
                .flatMap(repository::save)
                .doOnSuccess(ud -> log.debug("Activated user: {}", ud.getUsername()));
    }

    /**
     * Saves a new registered user with the given username, email, and password.
     *
     * @param username the username of the new user
     * @param email    the email of the new user
     * @param password the password of the new user
     * @return saved user
     */
    public Mono<RegisteredUser> saveNew(@NotBlank String username, String email, @NotBlank String password) {
        var newUser = new RegisteredUser();
        newUser.setUsername(username.toLowerCase());
        newUser.setEmail(email);
        newUser.setPassword(encoder.encode(password));
        newUser.setRole(AUTHORIZED_NEW);
        newUser.setActivationKey(UUID.randomUUID().toString());
        return repository.save(newUser);
    }

    /**
     * Retrieves the validated user details for the given username and password.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return the validated user details if found, otherwise empty
     */
    @Transactional(readOnly = true)
    public Mono<UserDetails> getValidatedUser(@NotBlank String username, @NotBlank String password) {
        return repository.findByUsername(username.toLowerCase())
                .filter(ud -> encoder.matches(password, ud.getPassword()))
                .filter(UserDetails::isEnabled);
    }

    /**
     * Generate an API token for the specified username.
     *
     * @param username the username for which to generate the API token
     * @return the generated API token
     */
    public Mono<String> generateApiToken(@NotBlank String username) {
        return repository.findByUsername(username.toLowerCase())
                .map(ud -> (RegisteredUser) ud)
                .doOnNext(ud -> ud.setApiKey(UUID.randomUUID().toString()))
                .flatMap(repository::save)
                .map(RegisteredUser::getApiKey);
    }
}