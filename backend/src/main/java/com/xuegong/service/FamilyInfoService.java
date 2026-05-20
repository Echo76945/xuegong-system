package com.xuegong.service;

import com.xuegong.entity.FamilyInfo;
import com.xuegong.repository.FamilyInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FamilyInfoService {

    private final FamilyInfoRepository repository;

    public List<FamilyInfo> findAll() {
        return repository.findAll();
    }

    public FamilyInfo findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<FamilyInfo> findByStudentCode(String studentCode) {
        return repository.findByStudentCode(studentCode);
    }

    public List<FamilyInfo> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll();
        }
        return repository.findByStudentNameContainingOrStudentCodeContaining(keyword, keyword);
    }

    public List<FamilyInfo> findByDifficulty(String difficulty) {
        return repository.findByDifficulty(difficulty);
    }

    public FamilyInfo save(FamilyInfo familyInfo) {
        return repository.save(familyInfo);
    }

    public FamilyInfo update(Long id, FamilyInfo familyInfo) {
        FamilyInfo existing = repository.findById(id).orElse(null);
        if (existing == null) return null;
        existing.setStudentCode(familyInfo.getStudentCode());
        existing.setStudentName(familyInfo.getStudentName());
        existing.setParentName(familyInfo.getParentName());
        existing.setRelation(familyInfo.getRelation());
        existing.setParentPhone(familyInfo.getParentPhone());
        existing.setAddress(familyInfo.getAddress());
        existing.setIncome(familyInfo.getIncome());
        existing.setDifficulty(familyInfo.getDifficulty());
        existing.setRemark(familyInfo.getRemark());
        return repository.save(existing);
    }

    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }
}
