package com.qburst.blog_application.exception;

public class OtpExpiredException extends RuntimeException {
    public OtpExpiredException(String message) {
        super(message);
    }
}
