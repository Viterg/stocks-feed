package ru.viterg.proselyte.stocksfeed.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

    CAN_READ_STOCKS("can_read_stocks"),
    ;

    @Getter
    private final String permission;
}
