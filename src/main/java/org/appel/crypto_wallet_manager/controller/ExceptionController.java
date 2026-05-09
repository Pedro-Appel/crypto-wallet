package org.appel.crypto_wallet_manager.controller;

import org.appel.crypto_wallet_manager.exception.NotFoundException;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<ErrorMessage> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.status(404).body(new ErrorMessage(e.getMessage()));
    }
}
