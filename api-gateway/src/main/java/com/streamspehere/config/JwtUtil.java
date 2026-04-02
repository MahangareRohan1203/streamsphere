package com.streamspehere.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    
    private final String SECRET = "thisismysecuretkfjsdlkfjsdkfjsdklfjsd;fkljfklsdasdfksdjfklsdfsdkl;fjksdfj;sdfaslfdskdfj";
    
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .compact();
    }
    
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
    public String extractRole(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }
    
    public boolean isTokenValid(String token, String username) {
        return extractUsername(token).equals(username);
    }
}
