package com.factory.controller;

import com.factory.exception.ClientErrorException;
import com.factory.exception.ServerErrorException;
import com.factory.openapi.model.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;

import static com.factory.openapi.model.Error.CodeEnum.INTERNAL_SERVER_ERROR;
import static com.factory.openapi.model.Error.CodeEnum.INVALID_INPUT;

@ControllerAdvice
public class ControllerErrorHandler {
    @ExceptionHandler(ClientErrorException.class)
    public ResponseEntity<Error> handleClientError(final ClientErrorException ex) {
        var error = getError(ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    private Error getError(final ClientErrorException ex) {
        return Error.builder()
                .code(Error.CodeEnum.fromValue(ex.getCode()))
                .description(ex.getMessage())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {
        var error = getError(ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    private Error getError(final MethodArgumentNotValidException ex) {
        return Error.builder()
                .code(INVALID_INPUT)
                .description(ex.getMessage())
                .build();
    }

    @ExceptionHandler(ServerErrorException.class)
    public ResponseEntity<Error> handleServerError(final ServerErrorException ex) {
        var error = getError(ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private Error getError(final ServerErrorException ex) {
        return Error.builder()
                .code(Error.CodeEnum.fromValue(ex.getCode()))
                .description(ex.getMessage())
                .build();
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Error> handleServerError(final Exception ex) {
        var error = getError(ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private Error getError(final Exception ex) {
        return Error.builder()
                .code(Error.CodeEnum.fromValue(INTERNAL_SERVER_ERROR.toString()))
                .description(ex.getMessage() + Arrays.toString(ex.getStackTrace()))
                .build();
    }
}
