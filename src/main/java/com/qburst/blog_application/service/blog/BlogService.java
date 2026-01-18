package com.qburst.blog_application.service.blog;

import com.qburst.blog_application.dto.request.blog.BlogAddRequest;
import com.qburst.blog_application.dto.response.blog.BlogResponse;
import com.qburst.blog_application.entity.BlogEntity;
import com.qburst.blog_application.entity.CategoryEntity;
import com.qburst.blog_application.entity.UserEntity;
import com.qburst.blog_application.repository.BlogRepository;
import com.qburst.blog_application.repository.CategoryRepository;
import com.qburst.blog_application.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public BlogResponse createBlog(BlogAddRequest request) {
        // 1. Fetch existing User (Author)
        UserEntity author = userRepository.findById(request.authorId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Fetch existing Category
        CategoryEntity category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // 3. Map Record DTO to Entity
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

        // 4. Return the Response Record
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
}