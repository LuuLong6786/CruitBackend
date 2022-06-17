package com.tma.recruit.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
public class CustomizedResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = ResponseStatusException.class)
    public ResponseEntity<?> handleIllegalArgumentException(ResponseStatusException ex) {
        return new ResponseEntity(ex.getReason(), ex.getStatus());
    }
}