package com.juratech.backend.controller;
import com.juratech.backend.model.LoanDocumentModel;
import com.juratech.backend.repository.DocumentRepository;
import com.juratech.backend.repository.ReviewRepository;
import com.juratech.backend.service.ActivityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/vetting")
@lombok.RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Document Management", description = "Endpoints for managing document submissions,retrieval and status")
public class DocumentController {


        private final DocumentRepository submissionRepo;
        private final ReviewRepository reviewRepo;
        private final ActivityService activityService;

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
        public ResponseEntity<Page<LoanDocumentModel>> getUserSubmissions(@PathVariable String userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("submittedAt").descending());
            return ResponseEntity.ok(submissionRepo.findByUserIdOrderBySubmittedAtDesc(userId,pageable));
        }

        // --- FOR REVIEWERS / ADMINS ---

        @GetMapping("/submissions/queue")
        public ResponseEntity<Page<LoanDocumentModel>> getPendingQueue(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("submittedAt").descending());
            List<String> activeStatuses = Arrays.asList("pending", "under-review","pending-under-review","approved","rejected");
            return ResponseEntity.ok(submissionRepo.findByStatusInOrderBySubmittedAtAsc(activeStatuses,pageable));
        }

    @GetMapping("/submissions/{id}")
    public ResponseEntity<LoanDocumentModel> getSubmissionById(@PathVariable String id) {
        return submissionRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 1. Fetch all submissions assigned to a specific reviewer
    @GetMapping("/submissions/reviewer/{reviewerId}")
    public ResponseEntity<Page<LoanDocumentModel>> getReviewerSubmissions(@PathVariable String reviewerId,@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        // NOTE: If you want Reviewers to also see UNASSIGNED documents in their queue,
        // you would write logic here to return findByStatus("pending") as well!
        Pageable pageable = PageRequest.of(page, size, Sort.by("submittedAt").descending());
        return ResponseEntity.ok(submissionRepo.findByReviewerOrderBySubmittedAtDesc(reviewerId,pageable));
    }

    // 2. Update the status and assign a reviewer
    @PutMapping("/submissions/{id}/status")
    public ResponseEntity<?> updateSubmissionStatus(@PathVariable String id, @RequestBody java.util.Map<String, String> payload) {
        Optional<LoanDocumentModel> optSub = submissionRepo.findById(id);

        if (optSub.isPresent()) {
            LoanDocumentModel sub = optSub.get();

            if (payload.containsKey("status")) {
                sub.setStatus(payload.get("status"));
            }
            if (payload.containsKey("reviewer")) {
                sub.setReviewer(payload.get("reviewer"));
            }

            sub.setReviewedAt(new Date().toString());
            submissionRepo.save(sub);

            // Optional: Log this activity
            activityService.logActivity(
                    "approval",
                    "Updated vetting status for submission to: " + sub.getStatus(),
                    sub.getReviewer(),
                    "Reviewer"
            );

            return ResponseEntity.ok(sub);
        }
        return ResponseEntity.notFound().build();
    }

    // Update a full submission (e.g. when a user replaces a rejected document)
    @PutMapping("/submissions/{id}")
    public ResponseEntity<?> updateSubmissionFull(@PathVariable String id, @RequestBody LoanDocumentModel updatedSubmission) {
        Optional<LoanDocumentModel> optSub = submissionRepo.findById(id);

        if (optSub.isPresent()) {
            LoanDocumentModel existing = optSub.get();

            // Overwrite the dynamic document maps with the newly uploaded file URLs
            existing.setBorrowerDetails(updatedSubmission.getBorrowerDetails());
            existing.setGuarantors(updatedSubmission.getGuarantors());
            existing.setLoanFacilities(updatedSubmission.getLoanFacilities());
            existing.setRegistrationOfSecurity(updatedSubmission.getRegistrationOfSecurity());
            existing.setSanctionLetter(updatedSubmission.getSanctionLetter());
            existing.setSecurities(updatedSubmission.getSecurities());
            existing.setOtherDocuments(updatedSubmission.getOtherDocuments());

            // Update metadata to push it back to the Reviewer's queue
            existing.setStatus("pending-under-review");
            existing.setLastModified(new Date().toString());
            existing.setModifiedBy(updatedSubmission.getModifiedBy());

            submissionRepo.save(existing);
            return ResponseEntity.ok(existing);
        }
        return ResponseEntity.notFound().build();
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
