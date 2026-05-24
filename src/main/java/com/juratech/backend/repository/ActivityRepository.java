package com.juratech.backend.repository;

import com.juratech.backend.model.ActivityModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends MongoRepository<ActivityModel, String>{

    List<ActivityModel> findByUserIdOrderByCreatedAtDesc(String userId);

    List<ActivityModel> findAllByOrderByCreatedAtDesc();
}
