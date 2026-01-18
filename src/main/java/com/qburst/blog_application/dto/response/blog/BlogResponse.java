package com.qburst.blog_application.dto.response.blog;

import java.time.LocalDateTime;
import java.util.Set;

public record BlogResponse(
        Long id,
        String title,
        String slug,
        String content,
        String summary,
        String imageUrl,

        // Nested DTOs or simple strings for related data
        String categoryName,
        String authorFullName,
        Set<String> tags,

        // Metadata
        long viewCount,
        boolean isPublished,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}