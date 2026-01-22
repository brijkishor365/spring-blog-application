package com.qburst.blog_application.service.category.Impl;

import com.qburst.blog_application.dto.request.category.Category;
import com.qburst.blog_application.entity.CategoryEntity;
import com.qburst.blog_application.exception.category.CategoryNameAlreadyExistSException;
import com.qburst.blog_application.exception.category.CategoryNotFoundException;
import com.qburst.blog_application.mapper.CategoryMapper;
import com.qburst.blog_application.repository.CategoryRepository;
import com.qburst.blog_application.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public Category createCategory(Category category) {
        List<CategoryEntity> categoryEntityList = categoryRepository.findCategoryByName(category.name());

        if (!categoryEntityList.isEmpty()) {
            throw new CategoryNameAlreadyExistSException("Category '" + category.name() + "' is already taken");
        }

        CategoryEntity categoryEntity = categoryMapper.toEntity(category);

        CategoryEntity newCategory = categoryRepository.save(categoryEntity);

        return categoryMapper.toResponse(newCategory);
    }

    @Override
    public Category updateCategory(Long categoryId, Category category) {
        CategoryEntity existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category ID: '" + categoryId + "' does not exist"));

        // Only throw if the name is taken by a DIFFERENT ID
        categoryRepository.findCategoryByName(category.name())
                .stream()
                .filter(c -> !c.getId().equals(categoryId)) // Check for ID mismatch
                .findAny()
                .ifPresent(c -> {
                    throw new CategoryNameAlreadyExistSException("Category '" + category.name() + "' is already taken");
                });

        // Update the managed entity using the mapper
        categoryMapper.updateEntityFromDto(category, existingCategory);

        CategoryEntity updatedCategory = categoryRepository.save(existingCategory);
        return categoryMapper.toResponse(updatedCategory);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category ID: '" + categoryId + "' does not exist"));

        categoryRepository.delete(category);
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category ID: '" + categoryId + "' does not exist"));

        return categoryMapper.toResponse(category);
    }

    @Override
    public Page<Category> getAllCategories(Pageable pageable) {
        Page<CategoryEntity> categories = categoryRepository.findAll(pageable);

        return categories.map(categoryMapper::toResponse);
    }
}
