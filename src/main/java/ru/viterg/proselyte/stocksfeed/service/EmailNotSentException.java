package ru.viterg.proselyte.stocksfeed.service;

import java.io.Serial;

class EmailNotSentException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    EmailNotSentException(Throwable cause) {
        super("Unable to send email!", cause);
    }
}
