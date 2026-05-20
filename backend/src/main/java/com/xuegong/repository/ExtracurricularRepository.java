package com.xuegong.repository;

import com.xuegong.entity.Extracurricular;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExtracurricularRepository extends JpaRepository<Extracurricular, Long> {

    List<Extracurricular> findByStudentNameContaining(String name);

    List<Extracurricular> findByCategory(String category);

    List<Extracurricular> findByStatus(String status);
}
