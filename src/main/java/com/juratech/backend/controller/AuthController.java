package com.juratech.backend.controller;
import com.juratech.backend.dto.CreateUserRequest;
import com.juratech.backend.dto.LoginRequest;
import com.juratech.backend.dto.ResetPasswordRequest;
import com.juratech.backend.model.PasswordResetToken;
import com.juratech.backend.model.UserModel;
import com.juratech.backend.repository.PasswordResetTokenRepository;
import com.juratech.backend.repository.UserRepository; // You'll need to create this MongoRepository!
import com.juratech.backend.security.JwtUtils;
import com.juratech.backend.service.EmailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for managing user authentication")
public class AuthController {

        private final AuthenticationManager authenticationManager;
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtils jwtUtils;
        private final EmailService emailService; // We will wire this up next!
        private final PasswordResetTokenRepository tokenRepository;

    @PostMapping("/login")
        public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        System.out.println("-----> Attempting to log in user: " + loginRequest.getEmail());
        System.out.println("-----> Attempting to log in user: " + loginRequest.getPassword());
            // 1. Verify credentials with Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 2. Fetch the user to get their role
            UserModel user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("Error: User is not found."));

            // 3. Generate the JWT token
            String jwt = jwtUtils.generateJwtToken(user.getEmail(), user.getRole());

            // 4. Send the token and user details back to React
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("email", user.getEmail());
            response.put("role", user.getRole());
            response.put("requiresPasswordChange", user.isRequiresPasswordChange());
            response.put("name", user.getName());
            response.put("uid", user.getId());

            return ResponseEntity.ok(response);
        }

        @PostMapping("/create-user")
        public ResponseEntity<?> registerUser(@RequestBody CreateUserRequest request) {
            // NOTE: In production, you'd add @PreAuthorize("hasRole('ADMIN')") here!

            if (userRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body("Error: Email is already in use!");
            }

            // 1. Generate a random 10-character password
            String randomPassword = UUID.randomUUID().toString().substring(0, 10);

            // 2. Create the new user and hash the password
            UserModel user = new UserModel();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setRole(request.getRole().toUpperCase());
            user.setPassword(passwordEncoder.encode(randomPassword));
            user.setRequiresPasswordChange(true);

            userRepository.save(user);

            // 3. Generate a Password Reset Token (we will build this table next)
            String tokenString = UUID.randomUUID().toString();

            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(tokenString);
            resetToken.setEmail(user.getEmail());
            // Set token to expire in 24 hours (86,400,000 milliseconds)
            resetToken.setExpiryDate(new Date(System.currentTimeMillis() + 86400000));

            tokenRepository.save(resetToken);

            // 4. Fire off the email
             emailService.sendWelcomeEmail(user.getEmail(), tokenString);

            Map<String, String> response = new HashMap<>();
            response.put("message", "User created successfully! Welcome email sent.");

            return ResponseEntity.ok(response);
        }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {

        // Find the token in MongoDB
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(request.getToken());

        if (tokenOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Invalid token.");
        }

        PasswordResetToken resetToken = tokenOpt.get();

        // Check if it's expired
        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken); // Clean up dead token
            return ResponseEntity.badRequest().body("Error: Token has expired. Please request a new one.");
        }

        // Find the user and update their password
        Optional<UserModel> userOpt = userRepository.findByEmail(resetToken.getEmail());
        if (userOpt.isPresent()) {
            UserModel user = userOpt.get();
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            user.setRequiresPasswordChange(false); // They've now set it themselves!
            userRepository.save(user);

            // Delete the token so it can't be used again
            tokenRepository.delete(resetToken);

            return ResponseEntity.ok("Password successfully updated. You can now log in.");
        }

        return ResponseEntity.badRequest().body("Error: User not found.");
    }


    @GetMapping("/users")
    public ResponseEntity<?> getUsersByRole(@RequestParam(required = false) String role) {
        try {
            if (role != null) {
                // If a role is provided (like "?role=REVIEWER"), fetch only those
                return ResponseEntity.ok(userRepository.findByRole(role.toUpperCase()));
            } else {
                // Otherwise, return everyone
                return ResponseEntity.ok(userRepository.findAll());
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching users: " + e.getMessage());
        }
    }

    }


