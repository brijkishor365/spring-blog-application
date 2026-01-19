package com.qburst.blog_application.exception.base;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ApplicationException extends RuntimeException {
    private final HttpStatus status;
    private final String error;

    protected ApplicationException(String message, String error, HttpStatus status) {
        super(message);
        this.status = status;
        this.error = error;
    }
}
