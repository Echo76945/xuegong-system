package com.xuegong.service;

import com.xuegong.entity.Extracurricular;
import com.xuegong.repository.ExtracurricularRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExtracurricularService {

    private final ExtracurricularRepository repository;

    public List<Extracurricular> findAll() {
        return repository.findAll();
    }

    public Extracurricular findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Extracurricular> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll();
        }
        return repository.findByStudentNameContaining(keyword);
    }

    public List<Extracurricular> findByCategory(String category) {
        return repository.findByCategory(category);
    }

    public List<Extracurricular> findByStatus(String status) {
        return repository.findByStatus(status);
    }

    public Extracurricular save(Extracurricular record) {
        return repository.save(record);
    }

    public Extracurricular update(Long id, Extracurricular record) {
        Extracurricular existing = repository.findById(id).orElse(null);
        if (existing == null) return null;
        existing.setStudentName(record.getStudentName());
        existing.setActivityName(record.getActivityName());
        existing.setCategory(record.getCategory());
        existing.setPoints(record.getPoints());
        existing.setDuration(record.getDuration());
        existing.setDate(record.getDate());
        existing.setStatus(record.getStatus());
        return repository.save(existing);
    }

    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }
}
