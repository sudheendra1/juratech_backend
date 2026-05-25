package com.juratech.backend.dto;
import lombok.Data;

@Data
public class CreateUserRequest {
    private String email;
    private String role;
    private String name;
}
