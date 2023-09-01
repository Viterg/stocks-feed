package ru.viterg.proselyte.stocksfeed.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtService jwtService;
    private final ReactiveUserDetailsService userDetailsService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .filter(auth -> auth instanceof BearerToken)
                .map(auth -> (BearerToken) auth)
                .flatMap(this::validate);
    }

    private Mono<Authentication> validate(BearerToken bearerToken) {
        String jwt = bearerToken.getToken();
        return userDetailsService.findByUsername(jwtService.extractUsername(jwt))
                .filter(ud -> jwtService.isTokenValid(jwt, ud))
                .switchIfEmpty(Mono.error(new AuthenticationServiceException("Invalid token")))
                .map(ud -> new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities()));
    }
}
