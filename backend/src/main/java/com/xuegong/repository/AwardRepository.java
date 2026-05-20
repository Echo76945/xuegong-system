package com.xuegong.repository;

import com.xuegong.entity.Award;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AwardRepository extends JpaRepository<Award, Long> {

    List<Award> findByStudentNameContaining(String name);

    List<Award> findByType(String type);

    List<Award> findByLevel(String level);
}
