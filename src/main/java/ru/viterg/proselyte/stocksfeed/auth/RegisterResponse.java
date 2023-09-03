package ru.viterg.proselyte.stocksfeed.auth;

import ru.viterg.proselyte.stocksfeed.user.Role;

public record RegisterResponse(String email, Role role) {
}
