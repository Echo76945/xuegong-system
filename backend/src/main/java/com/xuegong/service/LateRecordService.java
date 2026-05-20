package com.xuegong.service;

import com.xuegong.entity.LateRecord;
import com.xuegong.repository.LateRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LateRecordService {

    private final LateRecordRepository repository;

    public List<LateRecord> findAll() {
        return repository.findAll();
    }

    public LateRecord findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<LateRecord> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll();
        }
        return repository.findByStudentNameContainingOrStudentCodeContaining(keyword, keyword);
    }

    public List<LateRecord> findByDate(LocalDate date) {
        return repository.findByDate(date);
    }

    public List<LateRecord> findByAbnormal() {
        return repository.findByAbnormalTrue();
    }

    public LateRecord save(LateRecord record) {
        return repository.save(record);
    }

    public LateRecord update(Long id, LateRecord record) {
        LateRecord existing = repository.findById(id).orElse(null);
        if (existing == null) return null;
        existing.setStudentCode(record.getStudentCode());
        existing.setStudentName(record.getStudentName());
        existing.setClassName(record.getClassName());
        existing.setDate(record.getDate());
        existing.setReturnTime(record.getReturnTime());
        existing.setReason(record.getReason());
        existing.setAbnormal(record.getAbnormal());
        return repository.save(existing);
    }

    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }
}
