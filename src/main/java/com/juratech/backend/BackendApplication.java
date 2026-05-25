package com.juratech.backend;

import com.juratech.backend.model.UserModel;
import com.juratech.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			// Change this to your actual email!
			String adminEmail = "admin@juratech.com";
			if (!userRepository.existsByEmail(adminEmail)) {
				UserModel admin = new UserModel();
				admin.setEmail(adminEmail);
				admin.setPassword(passwordEncoder.encode("MasterAdmin123!")); // Initial password
				admin.setRole("ADMIN");
				admin.setRequiresPasswordChange(false);
				userRepository.save(admin);
				System.out.println("Master Admin created automatically.");
			}
		};
	}

}
