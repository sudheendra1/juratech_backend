package com.juratech.backend.repository;

import com.juratech.backend.model.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, String> {

    // Find the token document when the user submits it from React
    Optional<PasswordResetToken> findByToken(String token);

    // Optional cleanup tool to delete old tokens for a specific user
    void deleteByEmail(String email);
}