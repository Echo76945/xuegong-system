package com.xuegong.repository;

import com.xuegong.entity.FamilyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FamilyInfoRepository extends JpaRepository<FamilyInfo, Long> {

    List<FamilyInfo> findByStudentCode(String studentCode);

    List<FamilyInfo> findByStudentNameContainingOrStudentCodeContaining(String name, String code);

    List<FamilyInfo> findByDifficulty(String difficulty);
}
