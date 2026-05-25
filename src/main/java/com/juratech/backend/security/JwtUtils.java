package com.juratech.backend.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
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
                    .subject(email)
                    .claim("role", role)
                    .issuedAt(new Date())
                    .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                    .signWith(getSigningKey())
                    .compact();
        }

        // 2. EXTRACT THE EMAIL
        public String getEmailFromJwtToken(String token) {
            return Jwts.parser()
                    .verifyWith((SecretKey) getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        }

        // 3. VALIDATE THE TOKEN
        // Ensures the token hasn't been tampered with, hasn't expired, and is formatted correctly
        public boolean validateJwtToken(String authToken) {
            try {
                Jwts.parser().verifyWith((SecretKey) getSigningKey()).build().parseSignedClaims(authToken);
                return true;
            } catch (SignatureException e) {
                System.err.println("Invalid JWT signature: " + e.getMessage());
            } catch (MalformedJwtException e) {
                System.err.println("Invalid JWT token: " + e.getMessage());
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



