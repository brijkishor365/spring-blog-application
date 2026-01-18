package com.qburst.blog_application.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qburst.blog_application.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    @Query("SELECT ue from UserEntity ue WHERE ue.username=:username")
    public List<UserEntity> findUserByUsername(@Param("username") String username);

    Optional<UserEntity> findByEmail(String username);
}
