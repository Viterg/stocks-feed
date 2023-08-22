package ru.viterg.proselyte.stocksfeed.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static ru.viterg.proselyte.stocksfeed.user.Permission.CAN_READ_STOCKS;

@RequiredArgsConstructor
public enum Role {

    AUTHORIZED_NEW(Collections.emptySet()),
    AUTHORIZED_REGULAR(Set.of(CAN_READ_STOCKS)),
    ;

    @Getter
    private final Set<Permission> permissions;

    Collection<SimpleGrantedAuthority> getAuthorities() {
        var authorities = permissions.stream()
                .map(p -> new SimpleGrantedAuthority(p.getPermission()))
                .collect(toSet());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + name()));
        return authorities;
    }
}
