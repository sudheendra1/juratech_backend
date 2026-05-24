package com.juratech.backend.repository;
import com.juratech.backend.model.ReviewModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;


public interface ReviewRepository extends MongoRepository<ReviewModel, String>  {

        Optional<ReviewModel> findBySubmissionId(String submissionId);


}
