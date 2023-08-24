package ru.viterg.proselyte.stocksfeed.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.WWW_AUTHENTICATE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.security.config.web.server.SecurityWebFiltersOrder.AUTHENTICATION;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http, WebFilter jwtAuthenticationWebFilter) {
        http.authorizeExchange(exchange -> exchange
                        .pathMatchers("/api/v1/auth/**",
                                      "/api/docs/**",
                                      "/swagger-ui.html")
                        .permitAll()
                        .anyExchange()
                        .authenticated())
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // enable with PROD profile and using client-side
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .addFilterAt(jwtAuthenticationWebFilter, AUTHENTICATION)
                .exceptionHandling(eh -> eh.authenticationEntryPoint((exchange, e) -> Mono
                        .fromRunnable(() -> {
                            exchange.getResponse().setStatusCode(UNAUTHORIZED);
                            exchange.getResponse().getHeaders().set(WWW_AUTHENTICATE, "Bearer");
                        })));
        return http.build();
    }

    @Bean
    public WebFilter jwtAuthenticationWebFilter(ReactiveAuthenticationManager jwtAuthenticationManager,
            ServerAuthenticationConverter jwtServerAuthenticationConverter) {
        var filter = new AuthenticationWebFilter(jwtAuthenticationManager);
        filter.setServerAuthenticationConverter(jwtServerAuthenticationConverter);
        return filter;
    }

    @Bean
    public ServerAuthenticationConverter jwtServerAuthenticationConverter() {
        return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(AUTHORIZATION))
                .filter(value -> value.startsWith("Bearer "))
                .map(value -> value.substring(7))
                .map(value -> new BearerToken(value));
    }
}
