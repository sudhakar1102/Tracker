package com.lifecycle.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private final SecretKey key;
    public JwtUtil(@Value("${app.jwt.secret}") String secret) { this.key = Keys.hmacShaKeyFor(secret.getBytes()); }

    public String generate(Long userId) {
        return Jwts.builder().subject(userId.toString())
                .expiration(new Date(System.currentTimeMillis() + 24L * 3600 * 1000))
                .signWith(key).compact();
    }
    public Long parse(String token) {
        return Long.valueOf(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject());
    }
}
