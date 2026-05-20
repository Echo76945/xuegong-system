package com.xuegong.controller;

import com.xuegong.common.Result;
import com.xuegong.entity.Score;
import com.xuegong.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/scores")
@RequiredArgsConstructor
public class ScoreController {

    private final ScoreService service;

    @GetMapping
    public Result<List<Score>> list(@RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) String courseName,
                                    @RequestParam(required = false) String studentCode) {
        List<Score> list;
        if (studentCode != null && !studentCode.isEmpty()) {
            list = service.findByStudentCode(studentCode);
        } else if (courseName != null && !courseName.isEmpty()) {
            list = service.findByCourseName(courseName);
        } else if (keyword != null && !keyword.isEmpty()) {
            list = service.search(keyword);
        } else {
            list = service.findAll();
        }
        return Result.ok(list);
    }

    @PostMapping
    public Result<Score> create(@Valid @RequestBody Score score) {
        return Result.ok(service.save(score));
    }

    @PutMapping("/{id}")
    public Result<Score> update(@PathVariable Long id, @Valid @RequestBody Score score) {
        Score updated = service.update(id, score);
        return updated != null ? Result.ok(updated) : Result.error("记录不存在");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return service.delete(id) ? Result.ok() : Result.error("记录不存在");
    }
}
