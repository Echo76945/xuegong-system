package com.xuegong.controller;

import com.xuegong.common.Result;
import com.xuegong.entity.Extracurricular;
import com.xuegong.service.ExtracurricularService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/extracurricular")
@RequiredArgsConstructor
public class ExtracurricularController {

    private final ExtracurricularService service;

    @GetMapping
    public Result<List<Extracurricular>> list(@RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) String category,
                                              @RequestParam(required = false) String status) {
        List<Extracurricular> list;
        if (category != null && !category.isEmpty()) {
            list = service.findByCategory(category);
        } else if (status != null && !status.isEmpty()) {
            list = service.findByStatus(status);
        } else if (keyword != null && !keyword.isEmpty()) {
            list = service.search(keyword);
        } else {
            list = service.findAll();
        }
        return Result.ok(list);
    }

    @GetMapping("/{id}")
    public Result<Extracurricular> getById(@PathVariable Long id) {
        Extracurricular e = service.findById(id);
        return e != null ? Result.ok(e) : Result.error("记录不存在");
    }

    @PostMapping
    public Result<Extracurricular> create(@Valid @RequestBody Extracurricular record) {
        return Result.ok(service.save(record));
    }

    @PutMapping("/{id}")
    public Result<Extracurricular> update(@PathVariable Long id, @Valid @RequestBody Extracurricular record) {
        Extracurricular updated = service.update(id, record);
        return updated != null ? Result.ok(updated) : Result.error("记录不存在");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return service.delete(id) ? Result.ok() : Result.error("记录不存在");
    }
}
