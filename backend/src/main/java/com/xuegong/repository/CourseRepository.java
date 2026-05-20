package com.xuegong.repository;

import com.xuegong.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByNameContainingOrCodeContaining(String name, String code);

    List<Course> findByType(String type);
}
