package com.qburst.blog_application.dto.request.blog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record BlogAddRequest(
        @NotBlank(message = "Title is required")
        @Size(min = 5, max = 150, message = "Title should be between 5 and 150 characters")
        String title,

        @NotBlank(message = "Content cannot be empty")
        String content,

        @Size(max = 255)
        String summary, // A short blurb for the blog list view

        @NotNull(message = "Category ID is required")
        Long categoryId,

        // Using a Set for tags to ensure uniqueness
        Set<String> tags,

        @NotNull(message = "Author ID is required")
        Long authorId,

        boolean isPublished,

        String imageUrl
) {
    public BlogAddRequest {
        // Initialize tags to an empty set if null is passed
        if (tags == null) {
            tags = Set.of();
        }
    }
}