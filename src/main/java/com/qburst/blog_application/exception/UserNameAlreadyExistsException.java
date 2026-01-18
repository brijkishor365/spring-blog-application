package com.qburst.blog_application.exception;

public class UserNameAlreadyExistsException extends RuntimeException {
    public UserNameAlreadyExistsException(String message) {
        super(message); // Pass message to the parent class
    }
}
