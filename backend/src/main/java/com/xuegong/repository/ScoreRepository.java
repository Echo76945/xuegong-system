package com.xuegong.repository;

import com.xuegong.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScoreRepository extends JpaRepository<Score, Long> {

    List<Score> findByStudentCodeOrStudentNameContaining(String code, String name);

    List<Score> findByCourseName(String courseName);

    List<Score> findByStudentCode(String studentCode);
}
