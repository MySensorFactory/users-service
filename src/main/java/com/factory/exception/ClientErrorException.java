package com.factory.exception;

import lombok.Getter;
import com.factory.openapi.model.Error;

@Getter
public class ClientErrorException extends RuntimeException {
    private final String code;

    public ClientErrorException(final Error.CodeEnum code, final String message) {
        super(message);
        this.code = code.toString();
    }
}
