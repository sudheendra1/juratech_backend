package com.juratech.backend.service;
import com.juratech.backend.model.ActivityModel;
import com.juratech.backend.repository.ActivityRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Cacheable(value = "activities", key = "#p0.userId") // Caches returned array implicitly in server memory
    public Page<ActivityModel> getUserActivities(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("submittedAt").descending());
        return activityRepository.findByUserIdOrderByCreatedAtDesc(userId,pageable);
    }

    @CacheEvict(value = "activities", key = "#p0.userId")
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
