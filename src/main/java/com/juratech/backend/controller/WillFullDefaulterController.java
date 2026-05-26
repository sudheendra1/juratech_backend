package com.juratech.backend.controller;
import com.juratech.backend.model.WillFullDefaulterModel;
import com.juratech.backend.repository.WillFullDefaulterRepository;
import com.juratech.backend.service.WillFullDefaulterService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/willful-defaulter")
@Tag(name = "WillFullDefaulter management", description = "Endpoints for managing willFullDefaulter submissions")
public class WillFullDefaulterController {

    WillFullDefaulterRepository repository;

        @Autowired
        private WillFullDefaulterService service;

        // POST: Save new form data from React
        @PostMapping
        public ResponseEntity<WillFullDefaulterModel> submitForm(@RequestBody WillFullDefaulterModel submission) {
            WillFullDefaulterModel savedData = service.createSubmission(submission);
            return ResponseEntity.ok(savedData);
        }

        // GET: Fetch all submissions (Dashboard)
        @GetMapping
        public ResponseEntity<List<WillFullDefaulterModel>> getAll() {
            return ResponseEntity.ok(service.getAllSubmissions());
        }

        // GET: Fetch by User ID (Dashboard -> My Submissions)
        @GetMapping("/user/{userId}")
        public ResponseEntity<List<WillFullDefaulterModel>> getByUser(@PathVariable String userId) {
            return ResponseEntity.ok(service.getUserSubmissions(userId));
        }

        // GET: Fetch single record (Details Page)
        @GetMapping("/{id}")
        public ResponseEntity<WillFullDefaulterModel> getById(@PathVariable String id) {
            return ResponseEntity.ok(service.getSubmissionById(id));
        }

    @GetMapping("/search")
    public ResponseEntity<List<WillFullDefaulterModel>> searchSubmissions(
            @RequestParam(required = false) String branchId,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String submittedByUid) {

        // For maximum speed without configuring a complex MongoDB criteria builder,
        // you can lean on your existing tailored routes or basic repository filters.
        if (submittedByUid != null) {
            return ResponseEntity.ok(service.getUserSubmissions(submittedByUid));
        }
        if (status != null) {
            return ResponseEntity.ok(repository.findByStatusOrderBySubmittedAtDesc(status));
        }

        return ResponseEntity.ok(service.getAllSubmissions());
    }
    }

