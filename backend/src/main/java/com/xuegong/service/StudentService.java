package com.xuegong.service;

import com.xuegong.entity.Student;
import com.xuegong.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository repository;

    @Cacheable(value = "students", key = "'all'")
    public List<Student> findAll() {
        return repository.findAll();
    }

    @Cacheable(value = "students", key = "#id")
    public Student findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Student> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll();
        }
        return repository.findByNameContainingOrCodeContaining(keyword, keyword);
    }

    @Cacheable(value = "students", key = "'class:' + #className")
    public List<Student> findByClassName(String className) {
        return repository.findByClassName(className);
    }

    @Cacheable(value = "students", key = "'classNames'")
    public List<String> findAllClassNames() {
        return repository.findAllClassNames();
    }

    public long countByStatus(String status) {
        return repository.countByStatus(status);
    }

    @CacheEvict(value = "students", allEntries = true)
    public Student save(Student student) {
        return repository.save(student);
    }

    @CacheEvict(value = "students", allEntries = true)
    public Student update(Long id, Student student) {
        Student existing = repository.findById(id).orElse(null);
        if (existing == null) return null;
        existing.setCode(student.getCode());
        existing.setName(student.getName());
        existing.setGender(student.getGender());
        existing.setClassName(student.getClassName());
        existing.setPhone(student.getPhone());
        existing.setStatus(student.getStatus());
        return repository.save(existing);
    }

    @CacheEvict(value = "students", allEntries = true)
    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }

    @Transactional
    @CacheEvict(value = "students", allEntries = true)
    public int importAll(List<Student> students) {
        int count = 0;
        for (Student s : students) {
            if (s.getCode() == null || s.getCode().isEmpty()) continue;
            if (s.getName() == null || s.getName().isEmpty()) continue;
            repository.save(s);
            count++;
        }
        return count;
    }
}
