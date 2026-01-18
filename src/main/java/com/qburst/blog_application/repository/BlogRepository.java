package com.qburst.blog_application.repository;

import com.qburst.blog_application.entity.BlogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<BlogEntity, Integer> {

    // Find a blog by its SEO-friendly slug
    Optional<BlogEntity> findBySlug(String slug);

    // Find all published blogs, ordered by newest first
    List<BlogEntity> findByIsPublishedTrueOrderByCreatedAtDesc();

    // Find blogs by a specific category
    List<BlogEntity> findByCategoryId(Long categoryId);

    // Search blogs by title (case-insensitive)
    List<BlogEntity> findByTitleContainingIgnoreCase(String keyword);
}