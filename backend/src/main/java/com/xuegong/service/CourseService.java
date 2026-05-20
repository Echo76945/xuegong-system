package com.xuegong.service;

import com.xuegong.entity.Course;
import com.xuegong.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository repository;

    @Cacheable(value = "courses", key = "'all'")
    public List<Course> findAll() {
        return repository.findAll();
    }

    @Cacheable(value = "courses", key = "#id")
    public Course findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Course> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll();
        }
        return repository.findByNameContainingOrCodeContaining(keyword, keyword);
    }

    @Cacheable(value = "courses", key = "'type:' + #type")
    public List<Course> findByType(String type) {
        return repository.findByType(type);
    }

    @CacheEvict(value = "courses", allEntries = true)
    public Course save(Course course) {
        if (course.getEnrolled() == null) course.setEnrolled(0);
        return repository.save(course);
    }

    @CacheEvict(value = "courses", allEntries = true)
    public Course update(Long id, Course course) {
        Course existing = repository.findById(id).orElse(null);
        if (existing == null) return null;
        existing.setCode(course.getCode());
        existing.setName(course.getName());
        existing.setType(course.getType());
        existing.setCredit(course.getCredit());
        existing.setTeacher(course.getTeacher());
        existing.setSchedule(course.getSchedule());
        existing.setCapacity(course.getCapacity());
        existing.setEnrolled(course.getEnrolled());
        return repository.save(existing);
    }

    @CacheEvict(value = "courses", allEntries = true)
    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }
}
