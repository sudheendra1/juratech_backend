package com.juratech.backend.repository;
import com.juratech.backend.model.WillFullDefaulterModel;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WillFullDefaulterRepository extends MongoRepository<WillFullDefaulterModel, String>{


        // For the "My Submissions" view on your dashboard
        Page<WillFullDefaulterModel> findBySubmittedByUidOrderBySubmittedAtDesc(String submittedByUid, Pageable pageable);

        // For the "Status" filter on your dashboard
        Page<WillFullDefaulterModel> findByStatusOrderBySubmittedAtDesc(String status,Pageable pageable);

        // Default dashboard view (all submissions)
        Page<WillFullDefaulterModel> findAllByOrderBySubmittedAtDesc(Pageable pageable);

}
