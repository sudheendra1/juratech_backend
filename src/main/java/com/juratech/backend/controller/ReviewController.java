package com.juratech.backend.controller;
import com.juratech.backend.model.ReviewModel;
import com.juratech.backend.repository.ReviewRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:3000")
public class ReviewController {


        private final ReviewRepository reviewRepository;

        public ReviewController(ReviewRepository reviewRepository) {
            this.reviewRepository = reviewRepository;
        }

        // 1. Submit or Update a Review
        @PostMapping
        public ResponseEntity<ReviewModel> submitReview(@RequestBody ReviewModel reviewPayload) {
            // If a review already exists for this submission, you might want to update it
            // instead of creating a duplicate.
            Optional<ReviewModel> existingReview = reviewRepository.findBySubmissionId(reviewPayload.getSubmissionId());

            if (existingReview.isPresent()) {
                ReviewModel current = existingReview.get();
                // Preserve the MongoDB ID so it updates the existing record
                reviewPayload.setId(current.getId());
            }

            ReviewModel savedReview = reviewRepository.save(reviewPayload);

            // Note: You can also inject your SubmissionRepository here
            // to update the status of the parent submission to "reviewed"

            return ResponseEntity.ok(savedReview);
        }

        // 2. Fetch a Review by its Submission ID
        @GetMapping("/submission/{submissionId}")
        public ResponseEntity<ReviewModel> getReviewBySubmission(@PathVariable String submissionId) {
            return reviewRepository.findBySubmissionId(submissionId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }


}
