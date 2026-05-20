package com.xuegong.repository;

import com.xuegong.entity.ScholarshipApply;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScholarshipApplyRepository extends JpaRepository<ScholarshipApply, Long> {

    List<ScholarshipApply> findByStatus(String status);

    List<ScholarshipApply> findByStudentNameContaining(String studentName);
}
