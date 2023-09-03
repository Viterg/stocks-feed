package ru.viterg.proselyte.stocksfeed.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthenticationResponse(@JsonProperty("access_token") String accessToken) {
}
