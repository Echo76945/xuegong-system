package com.xuegong.controller;

import com.xuegong.common.Result;
import com.xuegong.entity.Student;
import com.xuegong.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService service;

    @GetMapping
    public Result<List<Student>> list(@RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) String className) {
        List<Student> list;
        if (className != null && !className.isEmpty()) {
            list = service.findByClassName(className);
        } else if (keyword != null && !keyword.isEmpty()) {
            list = service.search(keyword);
        } else {
            list = service.findAll();
        }
        return Result.ok(list);
    }

    @GetMapping("/{id}")
    public Result<Student> getById(@PathVariable Long id) {
        Student s = service.findById(id);
        return s != null ? Result.ok(s) : Result.error("学生不存在");
    }

    @GetMapping("/classes")
    public Result<List<String>> getClassNames() {
        return Result.ok(service.findAllClassNames());
    }

    @GetMapping("/count")
    public Result<Long> count(@RequestParam(defaultValue = "在校") String status) {
        return Result.ok(service.countByStatus(status));
    }

    @PostMapping
    public Result<Student> create(@Valid @RequestBody Student student) {
        return Result.ok(service.save(student));
    }

    @PutMapping("/{id}")
    public Result<Student> update(@PathVariable Long id, @Valid @RequestBody Student student) {
        Student updated = service.update(id, student);
        return updated != null ? Result.ok(updated) : Result.error("学生不存在");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return service.delete(id) ? Result.ok() : Result.error("学生不存在");
    }

    @PostMapping("/import")
    public Result<Integer> importStudents(@RequestBody List<Student> students) {
        if (students == null || students.isEmpty()) {
            return Result.error("导入数据为空");
        }
        int count = service.importAll(students);
        return Result.ok(count);
    }
}
