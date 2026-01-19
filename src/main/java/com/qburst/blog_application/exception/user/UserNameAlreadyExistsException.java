package com.qburst.blog_application.exception.user;

import com.qburst.blog_application.exception.base.ApplicationException;
import org.springframework.http.HttpStatus;

public class UserNameAlreadyExistsException extends ApplicationException {
    public UserNameAlreadyExistsException(String message) {
        super(message, "Username Conflict", HttpStatus.CONFLICT);
    }
}
