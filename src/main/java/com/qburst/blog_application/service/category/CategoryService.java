package com.qburst.blog_application.service.category;

import com.qburst.blog_application.dto.request.category.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    Category createCategory(Category category);

    Category updateCategory(Long categoryId, Category category);

    void deleteCategory(Long categoryId);

    Category getCategoryById(Long categoryId);

    Page<Category> getAllCategories(Pageable pageable);
}
