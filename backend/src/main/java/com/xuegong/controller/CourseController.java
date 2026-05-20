package com.xuegong.controller;

import com.xuegong.common.Result;
import com.xuegong.entity.Course;
import com.xuegong.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService service;

    @GetMapping
    public Result<List<Course>> list(@RequestParam(required = false) String keyword,
                                     @RequestParam(required = false) String type) {
        List<Course> list;
        if (type != null && !type.isEmpty()) {
            list = service.findByType(type);
        } else if (keyword != null && !keyword.isEmpty()) {
            list = service.search(keyword);
        } else {
            list = service.findAll();
        }
        return Result.ok(list);
    }

    @GetMapping("/{id}")
    public Result<Course> getById(@PathVariable Long id) {
        Course c = service.findById(id);
        return c != null ? Result.ok(c) : Result.error("课程不存在");
    }

    @PostMapping
    public Result<Course> create(@Valid @RequestBody Course course) {
        return Result.ok(service.save(course));
    }

    @PutMapping("/{id}")
    public Result<Course> update(@PathVariable Long id, @Valid @RequestBody Course course) {
        Course updated = service.update(id, course);
        return updated != null ? Result.ok(updated) : Result.error("课程不存在");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return service.delete(id) ? Result.ok() : Result.error("课程不存在");
    }
}
