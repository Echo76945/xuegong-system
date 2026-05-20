package com.xuegong.repository;

import com.xuegong.entity.LateRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface LateRecordRepository extends JpaRepository<LateRecord, Long> {

    List<LateRecord> findByStudentCode(String studentCode);

    List<LateRecord> findByStudentNameContainingOrStudentCodeContaining(String name, String code);

    List<LateRecord> findByDate(LocalDate date);

    List<LateRecord> findByAbnormalTrue();
}
