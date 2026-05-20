package com.xuegong.repository;

import com.xuegong.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {

    List<Incident> findByTitleContainingOrReporterContaining(String title, String reporter);

    List<Incident> findBySeverity(String severity);

    List<Incident> findByStatus(String status);
}
