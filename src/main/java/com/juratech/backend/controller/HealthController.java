package com.juratech.backend.controller;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health", description = "Endpoints for checking health for AWS")
public class HealthController {

    @GetMapping("/health")
        public String healthCheck() {
            return "OK";
        }
    }

