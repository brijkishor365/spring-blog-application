package com.qburst.blog_application.repository;

import com.qburst.blog_application.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {}
