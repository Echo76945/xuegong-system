package com.xuegong.controller;

import com.xuegong.common.Result;
import com.xuegong.entity.Activity;
import com.xuegong.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService service;

    @GetMapping
    public Result<List<Activity>> list(@RequestParam(required = false) String keyword,
                                       @RequestParam(required = false) String status) {
        List<Activity> list;
        if (status != null && !status.isEmpty()) {
            list = service.findByStatus(status);
        } else if (keyword != null && !keyword.isEmpty()) {
            list = service.search(keyword);
        } else {
            list = service.findAll();
        }
        return Result.ok(list);
    }

    @GetMapping("/{id}")
    public Result<Activity> getById(@PathVariable Long id) {
        Activity a = service.findById(id);
        return a != null ? Result.ok(a) : Result.error("活动不存在");
    }

    @PostMapping
    public Result<Activity> create(@Valid @RequestBody Activity activity) {
        return Result.ok(service.save(activity));
    }

    @PutMapping("/{id}")
    public Result<Activity> update(@PathVariable Long id, @Valid @RequestBody Activity activity) {
        Activity updated = service.update(id, activity);
        return updated != null ? Result.ok(updated) : Result.error("活动不存在");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return service.delete(id) ? Result.ok() : Result.error("活动不存在");
    }

    @PostMapping("/{id}/enroll")
    public Result<Activity> enroll(@PathVariable Long id) {
        Activity enrolled = service.enroll(id);
        return enrolled != null ? Result.ok(enrolled) : Result.error("报名失败，名额已满或活动不存在");
    }
}
