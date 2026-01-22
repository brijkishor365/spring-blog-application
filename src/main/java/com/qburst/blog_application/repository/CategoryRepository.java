package com.qburst.blog_application.repository;

import com.qburst.blog_application.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    @Query("SELECT ce from CategoryEntity ce WHERE ce.name = :name")
    public List<CategoryEntity> findCategoryByName(@Param("name") String name);
}
