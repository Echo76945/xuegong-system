package com.xuegong.service;

import com.xuegong.entity.Score;
import com.xuegong.repository.ScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScoreService {

    private final ScoreRepository repository;

    public List<Score> findAll() {
        return repository.findAll();
    }

    public List<Score> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return repository.findAll();
        return repository.findByStudentCodeOrStudentNameContaining(keyword, keyword);
    }

    public List<Score> findByCourseName(String courseName) {
        return repository.findByCourseName(courseName);
    }

    public List<Score> findByStudentCode(String studentCode) {
        return repository.findByStudentCode(studentCode);
    }

    public Score save(Score score) {
        return repository.save(score);
    }

    public Score update(Long id, Score score) {
        Score existing = repository.findById(id).orElse(null);
        if (existing == null) return null;
        existing.setStudentCode(score.getStudentCode());
        existing.setStudentName(score.getStudentName());
        existing.setCourseName(score.getCourseName());
        existing.setScore(score.getScore());
        existing.setSemester(score.getSemester());
        return repository.save(existing);
    }

    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }
}
