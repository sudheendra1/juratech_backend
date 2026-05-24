package com.juratech.backend.controller;
import com.juratech.backend.model.LoanDocumentModel;
import com.juratech.backend.model.ReviewModel;
import com.juratech.backend.repository.DocumentRepository;
import com.juratech.backend.repository.ReviewRepository;
import com.juratech.backend.service.ActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/vetting")
//@CrossOrigin(origins = "http://localhost:3000")
public class DocumentController {


        private final DocumentRepository submissionRepo;
        private final ReviewRepository reviewRepo;
        private final ActivityService activityService;

        public DocumentController(DocumentRepository submissionRepo, ReviewRepository reviewRepo, ActivityService activityService) {
            this.submissionRepo = submissionRepo;
            this.reviewRepo = reviewRepo;
            this.activityService = activityService;
        }

        // --- FOR USERS ---

        @PostMapping("/submissions")
        public ResponseEntity<LoanDocumentModel> createSubmission(@RequestBody LoanDocumentModel submission) {
            submission.setStatus("pending");
            submission.setSubmittedAt(new Date());
            LoanDocumentModel saved = submissionRepo.save(submission);

            activityService.logActivity(
                    "upload",
                    "Uploaded a new document: " + (submission.getName() != null ? submission.getName() : "Vetting Submission"),
                    submission.getSubmittedByUid(),
                    submission.getSubmittedBy()
            );

            return ResponseEntity.ok(saved);
        }

        @GetMapping("/submissions/user/{userId}")
        public ResponseEntity<List<LoanDocumentModel>> getUserSubmissions(@PathVariable String userId) {
            return ResponseEntity.ok(submissionRepo.findByUserIdOrderBySubmittedAtDesc(userId));
        }

        // --- FOR REVIEWERS / ADMINS ---

        @GetMapping("/submissions/queue")
        public ResponseEntity<List<LoanDocumentModel>> getPendingQueue() {
            List<String> activeStatuses = Arrays.asList("pending", "under-review");
            return ResponseEntity.ok(submissionRepo.findByStatusInOrderBySubmittedAtAsc(activeStatuses));
        }

    @GetMapping("/submissions/{id}")
    public ResponseEntity<LoanDocumentModel> getSubmissionById(@PathVariable String id) {
        return submissionRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

//        @PostMapping("/reviews")
//        public ResponseEntity<ReviewModel> submitReview(@RequestBody ReviewModel reviewPayload) {
//            ReviewModel savedReview = reviewRepo.save(reviewPayload);
//
//            Optional<LoanDocumentModel> optSub = submissionRepo.findById(reviewPayload.getSubmissionId());
//            if (optSub.isPresent()) {
//                LoanDocumentModel sub = optSub.get();
//                sub.setStatus("under-review");
//                sub.setReviewer(reviewPayload.getReviewerUid());
//                sub.setReviewedAt(new Date().toString());
//                submissionRepo.save(sub);
//
//                activityService.logActivity(
//                        "approval",
//                        "Updated vetting status for submission ID: " + sub.getId(),
//                        reviewPayload.getReviewerUid(),
//                        reviewPayload.getReviewerName()
//                );
//            }
//
//            return ResponseEntity.ok(savedReview);
//        }
//
//        @GetMapping("/reviews/{submissionId}")
//        public ResponseEntity<ReviewModel> getReviewForSubmission(@PathVariable String submissionId) {
//            return reviewRepo.findBySubmissionId(submissionId)
//                    .map(ResponseEntity::ok)
//                    .orElse(ResponseEntity.notFound().build());
//        }


}
