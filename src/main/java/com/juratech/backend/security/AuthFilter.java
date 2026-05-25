package com.juratech.backend.security;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class AuthFilter extends OncePerRequestFilter {

        @Autowired
        private JwtUtils jwtUtils;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            try {
                // 1. Get the token from the incoming Axios request header
                String jwt = parseJwt(request);

                // 2. If the token is present and the signature is mathematically valid...
                if (jwt != null && jwtUtils.validateJwtToken(jwt)) {

                    // 3. Extract the email from the token payload
                    String email = jwtUtils.getEmailFromJwtToken(jwt);

                    // 4. Create a Spring Security authentication object.
                    // (We pass an empty ArrayList for roles right now, we will add strict roles later).
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 5. Save the authentication data to the Security Context.
                    // This tells Spring Boot: "This user is officially logged in for this specific request."
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                System.err.println("Cannot set user authentication: " + e.getMessage());
            }

            // 6. Pass the request further down the chain to your Controllers
            filterChain.doFilter(request, response);
        }

        // Helper method to extract the token string from the Authorization header
        private String parseJwt(HttpServletRequest request) {
            String headerAuth = request.getHeader("Authorization");

            // Standard industry format: "Authorization: Bearer <token_string>"
            if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
                return headerAuth.substring(7);
            }

            return null;
        }
    }


