package com.xuegong.controller;

import com.xuegong.common.Result;
import com.xuegong.entity.Award;
import com.xuegong.service.AwardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/awards")
@RequiredArgsConstructor
public class AwardController {

    private final AwardService service;

    @GetMapping
    public Result<List<Award>> list(@RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) String type,
                                    @RequestParam(required = false) String level) {
        List<Award> list;
        if (type != null && !type.isEmpty()) {
            list = service.findByType(type);
        } else if (level != null && !level.isEmpty()) {
            list = service.findByLevel(level);
        } else if (keyword != null && !keyword.isEmpty()) {
            list = service.search(keyword);
        } else {
            list = service.findAll();
        }
        return Result.ok(list);
    }

    @GetMapping("/{id}")
    public Result<Award> getById(@PathVariable Long id) {
        Award a = service.findById(id);
        return a != null ? Result.ok(a) : Result.error("奖项不存在");
    }

    @PostMapping
    public Result<Award> create(@Valid @RequestBody Award award) {
        return Result.ok(service.save(award));
    }

    @PutMapping("/{id}")
    public Result<Award> update(@PathVariable Long id, @Valid @RequestBody Award award) {
        Award updated = service.update(id, award);
        return updated != null ? Result.ok(updated) : Result.error("奖项不存在");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return service.delete(id) ? Result.ok() : Result.error("奖项不存在");
    }
}
