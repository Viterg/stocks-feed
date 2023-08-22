package ru.viterg.proselyte.stocksfeed.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.viterg.proselyte.stocksfeed.user.RegisteredUser;
import ru.viterg.proselyte.stocksfeed.user.RegisteredUserRepository;
import ru.viterg.proselyte.stocksfeed.user.Role;

import static org.springframework.util.StringUtils.hasText;

@Service
@RequiredArgsConstructor
public class RegisteredUserService implements ReactiveUserDetailsService {

    private final RegisteredUserRepository repository;
    private final PasswordEncoder encoder;

    @Override
    @Transactional(readOnly = true)
    public Mono<UserDetails> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Transactional
    public Mono<RegisteredUser> saveNew(String username, String password, Role role) {
        var newUser = new RegisteredUser();
        newUser.setUsername(username.toLowerCase());
        newUser.setPassword(hasText(password) ? encoder.encode(password) : password);
        newUser.setRole(role.name());
        return repository.save(newUser);
    }
}