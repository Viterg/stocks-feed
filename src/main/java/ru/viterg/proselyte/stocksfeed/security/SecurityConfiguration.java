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
import ru.viterg.proselyte.stocksfeed.user.RegisteredUser;

import java.util.Objects;

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

    private static final String API_KEY_HEADER = "API-KEY";
    private final String[] publicPaths =
            {"/api/v1/auth/register", "/api/v1/auth/confirm", "/api/v1/auth/login", "/api/docs/**", "/swagger-ui.html"};

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http, WebFilter jwtAuthenticationWebFilter,
            WebFilter apikeyWebFilter) {
        http.authorizeExchange(exchange -> exchange
                        .pathMatchers(publicPaths)
                        .permitAll()
                        .anyExchange()
                        .authenticated())
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // enable with PROD profile and using client-side
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .addFilterAt(jwtAuthenticationWebFilter, AUTHENTICATION)
                .addFilterAfter(apikeyWebFilter, AUTHENTICATION)
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
    public WebFilter jwtAuthenticationWebFilter(ReactiveAuthenticationManager jwtAuthenticationManager,
            ServerAuthenticationConverter jwtServerAuthenticationConverter) {
        var filter = new AuthenticationWebFilter(jwtAuthenticationManager);
        filter.setServerAuthenticationConverter(jwtServerAuthenticationConverter);
        return filter;
    }

    @Bean
    public WebFilter apikeyWebFilter(PathPattern pathPattern) {
        return (exchange, chain) -> {
            if (pathPattern.matches(exchange.getRequest().getPath().pathWithinApplication())) {
                String apikey = exchange.getRequest().getHeaders().getFirst(API_KEY_HEADER);
                return exchange.getPrincipal()
                        .map(ud -> (RegisteredUser) ud)
                        .flatMap(ud -> {
                            if (Objects.equals(apikey, ud.getApiKey())) {
                                return chain.filter(exchange);
                            } else {
                                exchange.getResponse().setStatusCode(FORBIDDEN);
                                return exchange.getResponse().setComplete();
                            }
                        });
            } else {
                return chain.filter(exchange);
            }
        };
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
        return new PathPatternParser().parse("/api/v1");
    }
}
