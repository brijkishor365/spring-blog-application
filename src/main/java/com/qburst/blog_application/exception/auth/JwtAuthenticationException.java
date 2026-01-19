package com.qburst.blog_application.exception.auth;

import com.qburst.blog_application.exception.base.JwtErrorType;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class JwtAuthenticationException extends AuthenticationException {
    private final JwtErrorType errorType;

    public JwtAuthenticationException(
            JwtErrorType errorType,
            String message,
            Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }

}
