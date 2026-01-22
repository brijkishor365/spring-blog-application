package com.qburst.blog_application.exception.category;

import com.qburst.blog_application.exception.base.ApplicationException;
import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends ApplicationException {
    public CategoryNotFoundException(String message) {
        super(message, "Category not found", HttpStatus.NOT_FOUND);
    }
}
