package com.qburst.blog_application.exception.auth;

import com.qburst.blog_application.exception.base.ApplicationException;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ApplicationException {
    public UnauthorizedException(String message) {
        super(message, "Unauthorized", HttpStatus.UNAUTHORIZED);
    }
}
