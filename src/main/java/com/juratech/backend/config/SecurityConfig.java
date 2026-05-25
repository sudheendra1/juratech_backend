package com.juratech.backend.config;
import com.juratech.backend.security.AuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import com.juratech.backend.model.UserModel;
import com.juratech.backend.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthFilter authFilter;
    private final UserRepository userRepository;
    // 2. Tells Spring Security HOW to verify the password using BCrypt
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
//        authProvider.setUserDetailsService();
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    // 1. Tell Spring Security to use the CorsFilter we created in CorsConfig.java
//                    .cors(Customizer.withDefaults())
                    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                    // 2. Disable CSRF. This is absolutely required for stateless REST APIs
                    // receiving POST requests from React frameworks.
                    .csrf(AbstractHttpConfigurer::disable)

                    // 3. Make the session STATELESS (crucial for JWTs)
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                    // 4. Define route permissions
                    .authorizeHttpRequests(auth -> auth
                            // PUBLIC ENDPOINTS: Anyone can access these
                            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                            .requestMatchers("/api/auth/login", "/api/auth/reset-password").permitAll()

                            // PRIVATE ENDPOINTS: Everything else requires a valid JWT
                            .anyRequest().authenticated()
                    );

            http.authenticationProvider(authenticationProvider(userDetailsService(null), passwordEncoder()));

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

    // 1. Tells Spring Security HOW to find a user in your MongoDB database
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            // We use 'username' here because that is Spring's default terminology, but we pass it your email.
            UserModel user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

            // Convert your MongoDB user into a Spring Security user
            return User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .roles(user.getRole()) // Assumes roles are like "ADMIN", "REVIEWER"
                    .build();
        };
    }



    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 1. Explicitly allow your React frontend URL
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));

        // 2. Allow the standard HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 3. Allow our custom Authorization headers for the JWT
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Apply these rules to every endpoint in our API
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    }
