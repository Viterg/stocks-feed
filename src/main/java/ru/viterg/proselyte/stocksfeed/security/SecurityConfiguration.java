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
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.WWW_AUTHENTICATE;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.security.config.web.server.SecurityWebFiltersOrder.AUTHENTICATION;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final String[] publicPaths = {"/api/v1/auth/register", "/api/v1/auth/confirm",
            "/api/v1/auth/login", "/api/v1/sec/**", "/api/docs/**", "/swagger-ui.html", "/actuator/**"};

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http, WebFilter jwtAuthWebFilter,
            WebFilter apikeyLimiterWebFilter) {
        http.authorizeExchange(exchange -> exchange
                        .pathMatchers(publicPaths)
                        .permitAll()
                        .anyExchange()
                        .authenticated())
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // enable with PROD profile and using client-side
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .addFilterAt(jwtAuthWebFilter, AUTHENTICATION)
                .addFilterAfter(apikeyLimiterWebFilter, AUTHENTICATION)
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint((exchange, e) -> Mono.fromRunnable(() -> {
                            exchange.getResponse().setStatusCode(UNAUTHORIZED);
                            exchange.getResponse().getHeaders().set(WWW_AUTHENTICATE, "Bearer");
                        }))
                        .accessDeniedHandler((exchange, e) -> Mono.fromRunnable(() -> {
                            exchange.getResponse().setStatusCode(FORBIDDEN);
                        })));
        return http.build();
    }

    @Bean
    public WebFilter jwtAuthWebFilter(ReactiveAuthenticationManager jwtAuthManager,
            ServerAuthenticationConverter jwtServerAuthConverter) {
        var filter = new AuthenticationWebFilter(jwtAuthManager);
        filter.setServerAuthenticationConverter(jwtServerAuthConverter);
        return filter;
    }

    @Bean
    public ServerAuthenticationConverter jwtServerAuthenticationConverter() {
        return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(AUTHORIZATION))
                .filter(value -> value.startsWith("Bearer "))
                .map(value -> value.substring(7))
                .map(value -> new BearerToken(value));
    }

    @Bean
    public PathPattern apikeyPathPattern() {
        return new PathPatternParser().parse("/api/v1/sec/**");
    }
}
