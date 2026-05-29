package com.juratech.backend.service;
import com.juratech.backend.model.WillFullDefaulterModel;
import com.juratech.backend.repository.WillFullDefaulterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        public Page<WillFullDefaulterModel> getAllSubmissions(int page, int size) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("submittedAt").descending());
            return repository.findAllByOrderBySubmittedAtDesc(pageable);
        }

        // 3. Get submissions for a specific user (My Submissions)
        public Page<WillFullDefaulterModel> getUserSubmissions(String userId,int page, int size) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("submittedAt").descending());
            return repository.findBySubmittedByUidOrderBySubmittedAtDesc(userId,pageable);
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

