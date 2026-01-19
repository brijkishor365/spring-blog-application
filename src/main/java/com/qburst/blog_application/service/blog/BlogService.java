package com.qburst.blog_application.service.blog;

import com.qburst.blog_application.dto.request.blog.BlogAddRequest;
import com.qburst.blog_application.dto.response.blog.BlogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BlogService {

    BlogResponse createBlog(BlogAddRequest blogAddRequest);

    BlogService updateBlog(Long blogId, BlogAddRequest blogAddRequest);

    void deleteBlog(Long blogId);

    BlogService getBlogByID(Long blogId);

    Page<BlogService> getAllBlogs(Pageable pageable);

    List<BlogService> searchBlog(String keywords);

    List<BlogService> getBlogByCategory(Long categoryId);

    List<BlogService> getBlogByUser(Long userId);
}
