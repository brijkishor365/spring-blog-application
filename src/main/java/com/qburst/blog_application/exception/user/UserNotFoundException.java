package com.qburst.blog_application.exception.user;

import com.qburst.blog_application.exception.base.ApplicationException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApplicationException {

    public UserNotFoundException(String message) {
        super(message, "User Not Found", HttpStatus.NOT_FOUND);
    }
}
