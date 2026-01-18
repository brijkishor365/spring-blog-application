package com.qburst.blog_application.repository;

import com.qburst.blog_application.Document.BlacklistedToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlacklistedTokenRepository
        extends MongoRepository<BlacklistedToken, String> {

    boolean existsByToken(String token);
}
