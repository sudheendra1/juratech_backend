package com.juratech.backend.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;


@Component
public class JwtUtils {

        @Value("${app.jwt.secret}")
        private String jwtSecret;

        @Value("${app.jwt.expiration}")
        private int jwtExpirationMs;

        // Creates the cryptographic key from your secret string
        private Key getSigningKey() {
            return Keys.hmacShaKeyFor(jwtSecret.getBytes());
        }

        // 1. GENERATE THE TOKEN
        // We include the user's email and their role directly inside the token payload
        public String generateJwtToken(String email, String role) {
            return Jwts.builder()
                    .setSubject(email)
                    .claim("role", role)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
        }

        // 2. EXTRACT THE EMAIL
        public String getEmailFromJwtToken(String token) {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        }

        // 3. VALIDATE THE TOKEN
        // Ensures the token hasn't been tampered with, hasn't expired, and is formatted correctly
        public boolean validateJwtToken(String authToken) {
            try {
                Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
                return true;
            } catch (SecurityException | MalformedJwtException e) {
                System.err.println("Invalid JWT signature: " + e.getMessage());
            } catch (ExpiredJwtException e) {
                System.err.println("JWT token is expired: " + e.getMessage());
            } catch (UnsupportedJwtException e) {
                System.err.println("JWT token is unsupported: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.err.println("JWT claims string is empty: " + e.getMessage());
            }
            return false;
        }
    }



