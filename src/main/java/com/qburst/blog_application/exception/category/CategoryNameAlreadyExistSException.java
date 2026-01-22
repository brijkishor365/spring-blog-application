package com.qburst.blog_application.exception.category;

import com.qburst.blog_application.exception.base.ApplicationException;
import org.springframework.http.HttpStatus;

public class CategoryNameAlreadyExistSException extends ApplicationException {
    public CategoryNameAlreadyExistSException(String message) {
        super(message, "Category Conflict", HttpStatus.CONFLICT);
    }
}
