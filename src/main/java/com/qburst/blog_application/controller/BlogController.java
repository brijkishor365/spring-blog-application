package com.qburst.blog_application.controller;

import com.qburst.blog_application.dto.request.blog.BlogAddRequest;
import com.qburst.blog_application.dto.response.blog.BlogResponse;
import com.qburst.blog_application.service.blog.BlogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @PostMapping
    public ResponseEntity<BlogResponse> createBlog(@Valid @RequestBody BlogAddRequest request) {
        BlogResponse response = blogService.createBlog(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<BlogResponse> getBlogBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(blogService.getBlogBySlug(slug));
    }

    @GetMapping
    public ResponseEntity<List<BlogResponse>> getAllPublishedBlogs() {
        return ResponseEntity.ok(blogService.getAllPublishedBlogs());
    }
}