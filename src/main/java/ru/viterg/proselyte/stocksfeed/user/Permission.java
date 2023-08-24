package ru.viterg.proselyte.stocksfeed.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

    CAN_GENERATE_TOKEN("CAN_GENERATE_TOKEN"),
    ;

    @Getter
    private final String permission;
}
