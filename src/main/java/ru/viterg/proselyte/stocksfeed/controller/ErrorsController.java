package ru.viterg.proselyte.stocksfeed.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
public class ErrorsController {

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(RuntimeException.class)
    public String handleResourceNotFoundException(RuntimeException ex) {
        return ex.getMessage();
    }
}
