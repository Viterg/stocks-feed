package ru.viterg.proselyte.stocksfeed.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.viterg.proselyte.stocksfeed.user.RegisteredUser;
import ru.viterg.proselyte.stocksfeed.user.RegisteredUserRepository;

import java.util.UUID;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.springframework.util.StringUtils.hasText;
import static ru.viterg.proselyte.stocksfeed.user.Role.AUTHORIZED_NEW;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RegisteredUserService implements ReactiveUserDetailsService {

    private final RegisteredUserRepository repository;
    private final PasswordEncoder encoder;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    public Mono<RegisteredUser> activateRegistration(String key) {
        return repository.findByActivationKey(key)
                .doOnNext(ud -> {
                    ud.setActive(true);
                    ud.setActivationKey(null);
                    log.debug("Activated user: {}", ud.getUsername());
                });
    }

    @Transactional
    public Mono<RegisteredUser> saveNew(String username, String password, String role) {
        var newUser = new RegisteredUser();
        newUser.setUsername(username.toLowerCase());
        newUser.setPassword(hasText(password) ? encoder.encode(password) : password);
        newUser.setRole(defaultIfNull(role, AUTHORIZED_NEW.name()));
        newUser.setActivationKey(UUID.randomUUID().toString());
        return repository.save(newUser);
    }
}