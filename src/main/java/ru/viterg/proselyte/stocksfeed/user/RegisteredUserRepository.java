package ru.viterg.proselyte.stocksfeed.user;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface RegisteredUserRepository extends R2dbcRepository<RegisteredUser, String> {

    Mono<UserDetails> findByUsername(String username);

    Mono<RegisteredUser> findByActivationKey(String key);
}