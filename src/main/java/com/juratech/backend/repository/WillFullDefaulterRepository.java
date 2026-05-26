package com.juratech.backend.repository;
import com.juratech.backend.model.WillFullDefaulterModel;
import org.jspecify.annotations.Nullable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WillFullDefaulterRepository extends MongoRepository<WillFullDefaulterModel, String>{


        // For the "My Submissions" view on your dashboard
        List<WillFullDefaulterModel> findBySubmittedByUidOrderBySubmittedAtDesc(String submittedByUid);

        // For the "Status" filter on your dashboard
        List<WillFullDefaulterModel> findByStatusOrderBySubmittedAtDesc(String status);

        // Default dashboard view (all submissions)
        List<WillFullDefaulterModel> findAllByOrderBySubmittedAtDesc();

}
