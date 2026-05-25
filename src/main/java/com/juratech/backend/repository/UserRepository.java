package com.juratech.backend.repository;

import com.juratech.backend.model.UserModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<UserModel, String> {

    // Used during login to fetch the user's details and hashed password
    Optional<UserModel> findByEmail(String email);

    // Used during registration to ensure the admin doesn't create duplicate accounts
    boolean existsByEmail(String email);

    List<UserModel> findByRole(String role);
}
