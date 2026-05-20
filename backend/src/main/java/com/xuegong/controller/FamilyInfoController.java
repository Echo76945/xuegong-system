package com.xuegong.controller;

import com.xuegong.common.Result;
import com.xuegong.entity.FamilyInfo;
import com.xuegong.service.FamilyInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/families")
@RequiredArgsConstructor
public class FamilyInfoController {

    private final FamilyInfoService service;

    @GetMapping
    public Result<List<FamilyInfo>> list(@RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) String studentCode,
                                         @RequestParam(required = false) String difficulty) {
        List<FamilyInfo> list;
        if (studentCode != null && !studentCode.isEmpty()) {
            list = service.findByStudentCode(studentCode);
        } else if (difficulty != null && !difficulty.isEmpty()) {
            list = service.findByDifficulty(difficulty);
        } else if (keyword != null && !keyword.isEmpty()) {
            list = service.search(keyword);
        } else {
            list = service.findAll();
        }
        return Result.ok(list);
    }

    @GetMapping("/{id}")
    public Result<FamilyInfo> getById(@PathVariable Long id) {
        FamilyInfo f = service.findById(id);
        return f != null ? Result.ok(f) : Result.error("记录不存在");
    }

    @PostMapping
    public Result<FamilyInfo> create(@Valid @RequestBody FamilyInfo familyInfo) {
        return Result.ok(service.save(familyInfo));
    }

    @PutMapping("/{id}")
    public Result<FamilyInfo> update(@PathVariable Long id, @Valid @RequestBody FamilyInfo familyInfo) {
        FamilyInfo updated = service.update(id, familyInfo);
        return updated != null ? Result.ok(updated) : Result.error("记录不存在");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return service.delete(id) ? Result.ok() : Result.error("记录不存在");
    }
}
