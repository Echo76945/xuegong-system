package com.xuegong.service;

import com.xuegong.entity.Award;
import com.xuegong.repository.AwardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AwardService {

    private final AwardRepository repository;

    public List<Award> findAll() {
        return repository.findAll();
    }

    public Award findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Award> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll();
        }
        return repository.findByStudentNameContaining(keyword);
    }

    public List<Award> findByType(String type) {
        return repository.findByType(type);
    }

    public List<Award> findByLevel(String level) {
        return repository.findByLevel(level);
    }

    public Award save(Award award) {
        return repository.save(award);
    }

    public Award update(Long id, Award award) {
        Award existing = repository.findById(id).orElse(null);
        if (existing == null) return null;
        existing.setStudentName(award.getStudentName());
        existing.setAwardName(award.getAwardName());
        existing.setType(award.getType());
        existing.setLevel(award.getLevel());
        existing.setGrantUnit(award.getGrantUnit());
        existing.setAwardDate(award.getAwardDate());
        existing.setBonus(award.getBonus());
        return repository.save(existing);
    }

    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }
}
