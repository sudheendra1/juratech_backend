package com.juratech.backend.service;
import com.juratech.backend.model.WillFullDefaulterModel;
import com.juratech.backend.repository.WillFullDefaulterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
public class WillFullDefaulterService {

        @Autowired
        private WillFullDefaulterRepository repository;

        // 1. Create a new submission
        public WillFullDefaulterModel createSubmission(WillFullDefaulterModel submission) {
            // Auto-inject system metadata before saving
            submission.setSubmittedAt(new Date());
            submission.setStatus("pending");
            submission.setModule("willful-defaulter");

            return repository.save(submission);
        }

        // 2. Get all submissions (Admin Dashboard)
        public List<WillFullDefaulterModel> getAllSubmissions() {
            return repository.findAllByOrderBySubmittedAtDesc();
        }

        // 3. Get submissions for a specific user (My Submissions)
        public List<WillFullDefaulterModel> getUserSubmissions(String userId) {
            return repository.findBySubmittedByUidOrderBySubmittedAtDesc(userId);
        }

        // 4. Get a single submission (Details Page)
        public WillFullDefaulterModel getSubmissionById(String id) {
            Optional<WillFullDefaulterModel> submission = repository.findById(id);
            if (submission.isPresent()) {
                return submission.get();
            } else {
                throw new RuntimeException("Submission not found with id: " + id);
            }
        }
    }

