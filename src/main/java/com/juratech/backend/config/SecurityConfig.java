package com.juratech.backend.config;
import com.juratech.backend.security.AuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final AuthFilter authFilter;

    // Inject our custom bouncer
    public SecurityConfig(AuthFilter authFilter) {
        this.authFilter = authFilter;
    }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    // 1. Tell Spring Security to use the CorsFilter we created in CorsConfig.java
                    .cors(Customizer.withDefaults())

                    // 2. Disable CSRF. This is absolutely required for stateless REST APIs
                    // receiving POST requests from React frameworks.
                    .csrf(AbstractHttpConfigurer::disable)

                    // 3. Make the session STATELESS (crucial for JWTs)
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                    // 4. Define route permissions
                    .authorizeHttpRequests(auth -> auth
                            // PUBLIC ENDPOINTS: Anyone can access these
                            .requestMatchers("/api/auth/**").permitAll()

                            // PRIVATE ENDPOINTS: Everything else requires a valid JWT
                            .anyRequest().authenticated()
                    );

            // 5. Place our AuthFilter in front of the default Spring Security filter
            http.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

//                    // 3. Route Authorization
//                    .authorizeHttpRequests(auth -> auth
//                            // For right now, permit all traffic to your API endpoints so we can test the upload
//                            .requestMatchers("/api/**").permitAll()
//                            // Anything else requires authentication
//                            .anyRequest().authenticated()
//                    );

            return http.build();
        }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    }
