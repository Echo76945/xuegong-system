package com.xuegong.controller;

import com.xuegong.common.Result;
import com.xuegong.entity.ScholarshipApply;
import com.xuegong.service.ScholarshipApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scholarships")
@RequiredArgsConstructor
public class ScholarshipApplyController {

    private final ScholarshipApplyService service;

    @GetMapping
    public Result<List<ScholarshipApply>> list(@RequestParam(required = false) String status) {
        List<ScholarshipApply> list;
        if (status != null && !status.isEmpty() && !"all".equals(status)) {
            list = service.findByStatus(status);
        } else {
            list = service.findAll();
        }
        return Result.ok(list);
    }

    @PostMapping
    public Result<ScholarshipApply> create(@Valid @RequestBody ScholarshipApply apply) {
        return Result.ok(service.save(apply));
    }

    @PutMapping("/{id}/approve")
    public Result<ScholarshipApply> approve(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        String remark = body.getOrDefault("remark", "");
        String role = body.getOrDefault("role", "管理员");
        ScholarshipApply updated = service.approve(id, status, remark, role);
        if (updated == null) return Result.error("记录不存在或无权操作");
        return Result.ok(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return service.delete(id) ? Result.ok() : Result.error("记录不存在");
    }
}
