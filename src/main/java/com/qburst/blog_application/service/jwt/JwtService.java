package com.qburst.blog_application.service.jwt;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class JwtService {

    private static final long ACCESS_TOKEN_EXPIRY = 30 * 60 * 1000; // 15 minutes

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public JwtService(PrivateKey privateKey, PublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    /* ===================== TOKEN GENERATION ===================== */

    public String generateAccessToken(UserDetails userDetails) {

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claims(Map.of("role", userDetails.getAuthorities()))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    /* ===================== TOKEN VALIDATION ===================== */

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /* ===================== CLAIM EXTRACTION ===================== */

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Instant extractExpiry(String token) {
        return extractExpiration(token).toInstant();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
