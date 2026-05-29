package com.juratech.backend.controller;
import com.juratech.backend.model.WillFullDefaulterModel;
import com.juratech.backend.repository.WillFullDefaulterRepository;
import com.juratech.backend.service.WillFullDefaulterService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/willful-defaulter")
@RequiredArgsConstructor
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
        public ResponseEntity<Page<WillFullDefaulterModel>> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
            return ResponseEntity.ok(service.getAllSubmissions(page,size));
        }

        // GET: Fetch by User ID (Dashboard -> My Submissions)
        @GetMapping("/user/{userId}")
        public ResponseEntity<Page<WillFullDefaulterModel>> getByUser(@PathVariable String userId,@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
            return ResponseEntity.ok(service.getUserSubmissions(userId,page,size));
        }

        // GET: Fetch single record (Details Page)
        @GetMapping("/{id}")
        public ResponseEntity<WillFullDefaulterModel> getById(@PathVariable String id) {
            return ResponseEntity.ok(service.getSubmissionById(id));
        }

    @GetMapping("/search")
    public ResponseEntity<Page<WillFullDefaulterModel>> searchSubmissions(
            @RequestParam(required = false) String branchId,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String submittedByUid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // For maximum speed without configuring a complex MongoDB criteria builder,
        // you can lean on your existing tailored routes or basic repository filters.
        if (submittedByUid != null) {
            return ResponseEntity.ok(service.getUserSubmissions(submittedByUid,page,size));
        }
        if (status != null) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("submittedAt").descending());
            return ResponseEntity.ok(repository.findByStatusOrderBySubmittedAtDesc(status,pageable));
        }

        return ResponseEntity.ok(service.getAllSubmissions(page, size));
    }
    }

