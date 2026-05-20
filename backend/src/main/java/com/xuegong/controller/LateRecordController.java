package com.xuegong.controller;

import com.xuegong.common.Result;
import com.xuegong.entity.LateRecord;
import com.xuegong.service.LateRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/late-records")
@RequiredArgsConstructor
public class LateRecordController {

    private final LateRecordService service;

    @GetMapping
    public Result<List<LateRecord>> list(@RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                         @RequestParam(required = false) Boolean abnormal) {
        List<LateRecord> list;
        if (date != null) {
            list = service.findByDate(date);
        } else if (abnormal != null && abnormal) {
            list = service.findByAbnormal();
        } else if (keyword != null && !keyword.isEmpty()) {
            list = service.search(keyword);
        } else {
            list = service.findAll();
        }
        return Result.ok(list);
    }

    @GetMapping("/{id}")
    public Result<LateRecord> getById(@PathVariable Long id) {
        LateRecord r = service.findById(id);
        return r != null ? Result.ok(r) : Result.error("记录不存在");
    }

    @PostMapping
    public Result<LateRecord> create(@Valid @RequestBody LateRecord record) {
        return Result.ok(service.save(record));
    }

    @PutMapping("/{id}")
    public Result<LateRecord> update(@PathVariable Long id, @Valid @RequestBody LateRecord record) {
        LateRecord updated = service.update(id, record);
        return updated != null ? Result.ok(updated) : Result.error("记录不存在");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return service.delete(id) ? Result.ok() : Result.error("记录不存在");
    }
}
