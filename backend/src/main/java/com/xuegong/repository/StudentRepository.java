package com.xuegong.repository;

import com.xuegong.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByNameContainingOrCodeContaining(String name, String code);

    List<Student> findByClassName(String className);

    @Query("SELECT DISTINCT s.className FROM Student s")
    List<String> findAllClassNames();

    long countByStatus(String status);
}
