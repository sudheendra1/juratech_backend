package com.juratech.backend.controller;
import com.juratech.backend.model.ActivityModel;
import com.juratech.backend.repository.ActivityRepository;
import com.juratech.backend.service.ActivityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Activity Management", description = "Endpoints for managing activity across the system")
public class ActivityController {

        private final ActivityRepository activityRepo;
        private final ActivityService service;

    @GetMapping("/user/{userId}")
        public ResponseEntity<Page<ActivityModel>> getUserActivities(@PathVariable String userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

            return ResponseEntity.ok(service.getUserActivities(userId,page,size));
        }

        @GetMapping("/global")
        public ResponseEntity<Page<ActivityModel>> getGlobalActivities(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("submittedAt").descending());
            return ResponseEntity.ok(activityRepo.findAllByOrderByCreatedAtDesc(pageable));
        }
    }
