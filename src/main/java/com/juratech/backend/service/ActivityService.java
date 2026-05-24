package com.juratech.backend.service;
import com.juratech.backend.model.ActivityModel;
import com.juratech.backend.repository.ActivityRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;


@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public void logActivity(String type, String description, String userId, String userName) {
        ActivityModel log = new ActivityModel();
        log.setType(type);
        log.setDescription(description);
        log.setUserId(userId);
        log.setUserName(userName);
        log.setCreatedAt(LocalDateTime.now());
        activityRepository.save(log);
    }


}
