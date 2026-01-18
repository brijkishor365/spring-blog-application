package com.qburst.blog_application.Document;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "blacklisted_tokens")
public class BlacklistedToken {

    @Id
    private String id;

    @Indexed(unique = true)
    private String token;

    /**
     * TTL index – Mongo will auto delete after expiry
     */
    @Indexed(expireAfter = "0s")
    private Instant expiry;

    public BlacklistedToken(String token, Instant expiry) {
        this.token = token;
        this.expiry = expiry;
    }

    protected BlacklistedToken() {

    }
}
