package com.xuegong.service;

import com.xuegong.entity.ScholarshipApply;
import com.xuegong.repository.ScholarshipApplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScholarshipApplyService {

    private final ScholarshipApplyRepository repository;

    public List<ScholarshipApply> findAll() {
        return repository.findAll();
    }

    public List<ScholarshipApply> findByStatus(String status) {
        return repository.findByStatus(status);
    }

    public ScholarshipApply save(ScholarshipApply apply) {
        if (apply.getStatus() == null) apply.setStatus("待班主任审批");
        if (apply.getApprovalStage() == null) apply.setApprovalStage("teacher");
        if (apply.getApplyTime() == null) apply.setApplyTime(LocalDate.now());
        return repository.save(apply);
    }

    public ScholarshipApply approve(Long id, String status, String remark, String role) {
        ScholarshipApply existing = repository.findById(id).orElse(null);
        if (existing == null) return null;

        if ("已驳回".equals(status)) {
            existing.setStatus("已驳回");
            existing.setApproverRemark(remark);
            existing.setApprovalStage(null);
        } else if ("教师".equals(role) && "待班主任审批".equals(existing.getStatus())) {
            existing.setStatus("待学校审批");
            existing.setApprovalStage("school");
            existing.setApproverRemark(remark);
        } else if ("管理员".equals(role) && "待学校审批".equals(existing.getStatus())) {
            existing.setStatus("已通过");
            existing.setApprovalStage(null);
            existing.setApproverRemark(remark);
        } else {
            return null;
        }

        return repository.save(existing);
    }

    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }
}
