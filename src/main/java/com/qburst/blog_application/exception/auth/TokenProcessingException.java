package com.qburst.blog_application.exception.auth;

import com.qburst.blog_application.exception.base.ApplicationException;
import org.springframework.http.HttpStatus;

public class TokenProcessingException extends ApplicationException {
    public TokenProcessingException(String message) {
        super(message, "Token Error", HttpStatus.UNAUTHORIZED);
    }
}
