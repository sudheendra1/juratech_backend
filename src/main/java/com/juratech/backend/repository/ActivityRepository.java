package com.juratech.backend.repository;

import com.juratech.backend.model.ActivityModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends MongoRepository<ActivityModel, String>{

    Page<ActivityModel> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    Page<ActivityModel> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
