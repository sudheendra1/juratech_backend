package com.juratech.backend.controller;
import com.juratech.backend.model.ActivityModel;
import com.juratech.backend.repository.ActivityRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/activities")
//@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Activity Management", description = "Endpoints for managing activity across the system")
public class ActivityController {

        private final ActivityRepository activityRepo;

        public ActivityController(ActivityRepository activityRepo) {
            this.activityRepo = activityRepo;
        }

        @GetMapping("/user/{userId}")
        public ResponseEntity<List<ActivityModel>> getUserActivities(@PathVariable String userId) {
            return ResponseEntity.ok(activityRepo.findByUserIdOrderByCreatedAtDesc(userId));
        }

        @GetMapping("/global")
        public ResponseEntity<List<ActivityModel>> getGlobalActivities() {
            return ResponseEntity.ok(activityRepo.findAllByOrderByCreatedAtDesc());
        }
    }
