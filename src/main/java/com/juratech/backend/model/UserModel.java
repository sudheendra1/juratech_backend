package com.juratech.backend.model;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class UserModel {

        @Id
        private String id;

        private String email;
        private String password;
        private String role; // "ADMIN", "REVIEWER", "USER"
        private String name;
        private boolean isEnabled = true;
        private boolean requiresPasswordChange = true;
        private int assignedCount = 0;
        private int approved = 0;
        private int rejected = 0;


}
