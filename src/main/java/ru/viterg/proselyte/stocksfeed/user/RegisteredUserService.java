package ru.viterg.proselyte.stocksfeed.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.springframework.util.StringUtils.hasText;
import static ru.viterg.proselyte.stocksfeed.user.Role.AUTHORIZED_NEW;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RegisteredUserService implements ReactiveUserDetailsService {

    private final RegisteredUserRepository repository;
    private final PasswordEncoder encoder;

    @Override
    @Transactional(readOnly = true)
    public Mono<UserDetails> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    public Mono<RegisteredUser> activateRegistration(String key) {
        return repository.findByActivationKey(key)
                .map(ud -> {
                    ud.setActive(true);
                    ud.setActivationKey("");
                    return ud;
                })
                .flatMap(repository::save)
                .doOnSuccess(ud -> log.debug("Activated user: {}", ud.getUsername()));
    }

    public Mono<RegisteredUser> saveNew(String username, String password, String email) {
        var newUser = new RegisteredUser();
        newUser.setUsername(username.toLowerCase());
        newUser.setEmail(email);
        newUser.setPassword(hasText(password) ? encoder.encode(password) : password);
        newUser.setRole(AUTHORIZED_NEW);
        newUser.setActivationKey(UUID.randomUUID().toString());
        return repository.save(newUser);
    }

    public Mono<String> generateApiToken(String username) {
        return repository.findByUsername(username)
                .map(ud -> (RegisteredUser) ud)
                .doOnNext(ud -> ud.setApiKey(UUID.randomUUID().toString()))
                .map(RegisteredUser::getApiKey);
    }
}