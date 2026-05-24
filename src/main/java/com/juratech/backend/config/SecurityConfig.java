package com.juratech.backend.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    // 1. Tell Spring Security to use the CorsFilter we created in CorsConfig.java
                    .cors(Customizer.withDefaults())

                    // 2. Disable CSRF. This is absolutely required for stateless REST APIs
                    // receiving POST requests from React frameworks.
                    .csrf(AbstractHttpConfigurer::disable)

                    // 3. Route Authorization
                    .authorizeHttpRequests(auth -> auth
                            // For right now, permit all traffic to your API endpoints so we can test the upload
                            .requestMatchers("/api/**").permitAll()
                            // Anything else requires authentication
                            .anyRequest().authenticated()
                    );

            return http.build();
        }
    }
