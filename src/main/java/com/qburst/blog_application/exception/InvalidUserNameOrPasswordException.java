package com.qburst.blog_application.exception;

public class InvalidUserNameOrPasswordException extends RuntimeException {
    public InvalidUserNameOrPasswordException(String message) {
        super(message);
    }
}
