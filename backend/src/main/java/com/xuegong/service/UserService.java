package com.xuegong.service;

import com.xuegong.entity.User;
import com.xuegong.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public List<User> findAll() {
        return repository.findAll();
    }

    public User findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    public List<User> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll();
        }
        return repository.findByNameContainingOrUsernameContaining(keyword, keyword);
    }

    public List<User> findByRole(String role) {
        return repository.findByRole(role);
    }

    public List<User> findByStatus(String status) {
        return repository.findByStatus(status);
    }

    public User save(User user) {
        return repository.save(user);
    }

    public User update(Long id, User user) {
        User existing = repository.findById(id).orElse(null);
        if (existing == null) return null;
        existing.setName(user.getName());
        existing.setRole(user.getRole());
        existing.setPhone(user.getPhone());
        existing.setEmail(user.getEmail());
        existing.setStatus(user.getStatus());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existing.setPassword(user.getPassword());
        }
        return repository.save(existing);
    }

    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }

    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    public void updateLastLogin(String username) {
        Optional<User> userOpt = repository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setLastLogin(LocalDateTime.now());
            repository.save(user);
        }
    }
}
