package com.qburst.blog_application.service.blog.Impl;

import com.qburst.blog_application.dto.request.blog.BlogAddRequest;
import com.qburst.blog_application.dto.response.blog.BlogResponse;
import com.qburst.blog_application.entity.BlogEntity;
import com.qburst.blog_application.entity.CategoryEntity;
import com.qburst.blog_application.entity.UserEntity;
import com.qburst.blog_application.repository.BlogRepository;
import com.qburst.blog_application.repository.CategoryRepository;
import com.qburst.blog_application.repository.UserRepository;
import com.qburst.blog_application.service.blog.BlogService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    @Override
    public BlogResponse createBlog(BlogAddRequest request) {
        // Fetch existing User (Author)
        UserEntity author = userRepository.findById(request.authorId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch existing Category
        CategoryEntity category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Map Record DTO to Entity
        BlogEntity blog = BlogEntity.builder()
                .title(request.title())
                .content(request.content())
                .summary(request.summary())
                .imageUrl(request.imageUrl())
                .isPublished(request.isPublished())
                .tags(request.tags())
                .slug(request.title().toLowerCase().replace(" ", "-"))
                .author(author)   // Link existing entity
                .category(category) // Link existing entity
                .build();

        BlogEntity savedBlog = blogRepository.save(blog);

        // Return the Response Record
        return mapToResponse(savedBlog);
    }

    private BlogResponse mapToResponse(BlogEntity blog) {
        return new BlogResponse(
                blog.getId(),
                blog.getTitle(),
                blog.getSlug(),
                blog.getContent(),
                blog.getSummary(),
                blog.getImageUrl(),
                blog.getCategory() != null ? blog.getCategory().getName() : "Uncategorized",
                blog.getAuthor().getFirstname() + " " + blog.getAuthor().getLastname(),
                blog.getTags(),
                blog.getViewCount(),
                blog.isPublished(),
                blog.getCreatedAt(),
                blog.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public BlogResponse getBlogBySlug(String slug) {
        return blogRepository.findBySlug(slug)
                .map(this::mapToResponse) // Convert Entity to Record DTO
                .orElseThrow(() -> new EntityNotFoundException("Blog not found with slug: " + slug));
    }

    @Transactional(readOnly = true)
    public List<BlogResponse> getAllPublishedBlogs() {
        return blogRepository.findByIsPublishedTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse) // Convert List of Entities to List of Record DTOs
                .collect(Collectors.toList());
    }

    @Override
    public BlogService updateBlog(Long blogId, BlogAddRequest blogAddRequest) {
        return null;
    }

    @Override
    public void deleteBlog(Long blogId) {

    }

    @Override
    public BlogService getBlogByID(Long blogId) {
        return null;
    }

    @Override
    public Page<BlogService> getAllBlogs(Pageable pageable) {
        return null;
    }

    @Override
    public List<BlogService> searchBlog(String keywords) {
        return List.of();
    }

    @Override
    public List<BlogService> getBlogByCategory(Long categoryId) {
        return List.of();
    }

    @Override
    public List<BlogService> getBlogByUser(Long userId) {
        return List.of();
    }
}