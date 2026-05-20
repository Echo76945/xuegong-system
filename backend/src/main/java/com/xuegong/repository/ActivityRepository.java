package com.xuegong.repository;

import com.xuegong.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByNameContaining(String name);

    List<Activity> findByStatus(String status);
}
