package com.qburst.blog_application.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
@SoftDelete(strategy = SoftDeleteType.ACTIVE)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password; // Should be stored as a BCrypt hash

    @Column(nullable = false, unique = true)
    private String email;

    private String roles; // e.g., "ROLE_USER,ROLE_ADMIN"

    private String firstname;

    private String lastname;

    private String profileImageUrl;

    @Column(insertable=false, updatable=false)
    private boolean active = true;

    @Column(columnDefinition = "int default 0", nullable = false)
    private int failedAttempt = 0;

    private LocalDateTime lockTime;

    private String otp;

    private LocalDateTime otpExpiry;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int otpAttempts = 0;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", roles='" + roles + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}