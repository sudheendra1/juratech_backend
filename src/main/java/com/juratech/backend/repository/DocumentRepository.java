package com.juratech.backend.repository;
import com.juratech.backend.model.LoanDocumentModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DocumentRepository extends MongoRepository<LoanDocumentModel, String> {

        List<LoanDocumentModel> findByStatusInOrderBySubmittedAtAsc(List<String> statuses);
        List<LoanDocumentModel> findByUserIdOrderBySubmittedAtDesc(String userId);
        List<LoanDocumentModel> findByReviewerOrderBySubmittedAtDesc(String reviewerUid);


}
