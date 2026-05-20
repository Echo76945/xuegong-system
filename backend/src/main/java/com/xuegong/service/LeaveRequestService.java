package com.xuegong.service;

import com.xuegong.entity.LeaveRequest;
import com.xuegong.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {

    private final LeaveRequestRepository repository;

    public List<LeaveRequest> findAll() {
        return repository.findAll();
    }

    public LeaveRequest findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<LeaveRequest> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll();
        }
        return repository.findByStudentNameContaining(keyword);
    }

    public List<LeaveRequest> findByStatus(String status) {
        return repository.findByStatus(status);
    }

    public LeaveRequest save(LeaveRequest request) {
        return repository.save(request);
    }

    public LeaveRequest update(Long id, LeaveRequest request) {
        LeaveRequest existing = repository.findById(id).orElse(null);
        if (existing == null) return null;
        existing.setStudentName(request.getStudentName());
        existing.setClassName(request.getClassName());
        existing.setDestination(request.getDestination());
        existing.setStartTime(request.getStartTime());
        existing.setEndTime(request.getEndTime());
        existing.setReason(request.getReason());
        existing.setStatus(request.getStatus());
        return repository.save(existing);
    }

    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }

    public LeaveRequest approve(Long id, String status) {
        LeaveRequest existing = repository.findById(id).orElse(null);
        if (existing != null) {
            existing.setStatus(status);
            return repository.save(existing);
        }
        return null;
    }
}
