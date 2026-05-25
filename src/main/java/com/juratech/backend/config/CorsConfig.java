package com.juratech.backend.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
        @Bean
        public WebMvcConfigurer corsConfigurer() {
            return new WebMvcConfigurer() {
                @Override
                public void addCorsMappings(CorsRegistry registry) {
                    registry.addMapping("/**") // Apply to all API endpoints
                            .allowedOrigins("http://localhost:3000","https://juratech.in","https://dev.juratech.in","https://staging.juratech.in") // Explicitly allow your React app
                            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // OPTIONS is required for preflight
                            .allowedHeaders("*") // Allow all headers (like Content-Type)
                            .allowCredentials(true);
                }
            };
        }


}
