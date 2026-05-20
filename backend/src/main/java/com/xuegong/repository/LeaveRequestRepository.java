package com.xuegong.repository;

import com.xuegong.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByStudentNameContaining(String name);

    List<LeaveRequest> findByStatus(String status);
}
