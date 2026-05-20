package com.xuegong.service;

import com.xuegong.entity.Incident;
import com.xuegong.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidentService {

    private final IncidentRepository repository;

    public List<Incident> findAll() {
        return repository.findAll();
    }

    public Incident findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Incident> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll();
        }
        return repository.findByTitleContainingOrReporterContaining(keyword, keyword);
    }

    public List<Incident> findBySeverity(String severity) {
        return repository.findBySeverity(severity);
    }

    public List<Incident> findByStatus(String status) {
        return repository.findByStatus(status);
    }

    public Incident save(Incident incident) {
        return repository.save(incident);
    }

    public Incident update(Long id, Incident incident) {
        Incident existing = repository.findById(id).orElse(null);
        if (existing == null) return null;
        existing.setTitle(incident.getTitle());
        existing.setReporter(incident.getReporter());
        existing.setCategory(incident.getCategory());
        existing.setSeverity(incident.getSeverity());
        existing.setDescription(incident.getDescription());
        existing.setStatus(incident.getStatus());
        existing.setHandler(incident.getHandler());
        return repository.save(existing);
    }

    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }

    public Incident process(Long id, String handler) {
        Incident existing = repository.findById(id).orElse(null);
        if (existing != null) {
            existing.setStatus("处理中");
            existing.setHandler(handler);
            return repository.save(existing);
        }
        return null;
    }

    public Incident resolve(Long id) {
        Incident existing = repository.findById(id).orElse(null);
        if (existing != null) {
            existing.setStatus("已处理");
            return repository.save(existing);
        }
        return null;
    }
}
