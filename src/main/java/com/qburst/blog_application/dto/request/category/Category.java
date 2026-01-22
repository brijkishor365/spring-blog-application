package com.qburst.blog_application.dto.request.category;

import jakarta.validation.constraints.NotBlank;

public record Category(
        Long id,

        @NotBlank(message = "Category name is required")
        String name,
        String description
) {
}
