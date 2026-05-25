package com.juratech.backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@Document(collection = "password_reset_tokens")
public class PasswordResetToken {
    @Id
    private String id;

    private String token;
    private String email; // The user this token belongs to
    private Date expiryDate; // When the token becomes invalid

    // Helper method to quickly check if the token is dead
    public boolean isExpired() {
        return new Date().after(this.expiryDate);
    }
}