package com.xuegong.service;

import com.xuegong.entity.Announcement;
import com.xuegong.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository repository;

    @Cacheable(value = "announcements", key = "'all'")
    public List<Announcement> findAll() {
        return repository.findAll();
    }

    @Cacheable(value = "announcements", key = "#id")
    public Announcement findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Announcement> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll();
        }
        return repository.findByTitleContaining(keyword);
    }

    @Cacheable(value = "announcements", key = "'scope:' + #scope")
    public List<Announcement> findByScope(String scope) {
        return repository.findByScope(scope);
    }

    @CacheEvict(value = "announcements", allEntries = true)
    public Announcement save(Announcement announcement) {
        return repository.save(announcement);
    }

    @CacheEvict(value = "announcements", allEntries = true)
    public Announcement update(Long id, Announcement announcement) {
        Announcement existing = repository.findById(id).orElse(null);
        if (existing == null) return null;
        existing.setTitle(announcement.getTitle());
        existing.setContent(announcement.getContent());
        existing.setScope(announcement.getScope());
        existing.setPinned(announcement.getPinned());
        return repository.save(existing);
    }

    @CacheEvict(value = "announcements", allEntries = true)
    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }

    @CacheEvict(value = "announcements", key = "#id")
    public Announcement incrementViews(Long id) {
        Announcement existing = repository.findById(id).orElse(null);
        if (existing != null) {
            existing.setViews(existing.getViews() + 1);
            return repository.save(existing);
        }
        return null;
    }
}
