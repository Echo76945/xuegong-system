package com.xuegong.service;

import com.xuegong.entity.Activity;
import com.xuegong.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository repository;

    @Cacheable(value = "activities", key = "'all'")
    public List<Activity> findAll() {
        return repository.findAll();
    }

    @Cacheable(value = "activities", key = "#id")
    public Activity findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Activity> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll();
        }
        return repository.findByNameContaining(keyword);
    }

    @Cacheable(value = "activities", key = "'status:' + #status")
    public List<Activity> findByStatus(String status) {
        return repository.findByStatus(status);
    }

    @CacheEvict(value = "activities", allEntries = true)
    public Activity save(Activity activity) {
        return repository.save(activity);
    }

    @CacheEvict(value = "activities", allEntries = true)
    public Activity update(Long id, Activity activity) {
        Activity existing = repository.findById(id).orElse(null);
        if (existing == null) return null;
        existing.setName(activity.getName());
        existing.setDescription(activity.getDescription());
        existing.setDate(activity.getDate());
        existing.setLocation(activity.getLocation());
        existing.setCapacity(activity.getCapacity());
        existing.setStatus(activity.getStatus());
        return repository.save(existing);
    }

    @CacheEvict(value = "activities", allEntries = true)
    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }

    @CacheEvict(value = "activities", allEntries = true)
    public Activity enroll(Long id) {
        Activity existing = repository.findById(id).orElse(null);
        if (existing != null && existing.getEnrolled() < existing.getCapacity()) {
            existing.setEnrolled(existing.getEnrolled() + 1);
            return repository.save(existing);
        }
        return null;
    }
}
