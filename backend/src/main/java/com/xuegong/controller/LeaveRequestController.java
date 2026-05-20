package com.xuegong.controller;

import com.xuegong.common.Result;
import com.xuegong.entity.LeaveRequest;
import com.xuegong.service.LeaveRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/leave-requests")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService service;

    @GetMapping
    public Result<List<LeaveRequest>> list(@RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) String status) {
        List<LeaveRequest> list;
        if (status != null && !status.isEmpty() && !"all".equals(status)) {
            list = service.findByStatus(status);
        } else if (keyword != null && !keyword.isEmpty()) {
            list = service.search(keyword);
        } else {
            list = service.findAll();
        }
        return Result.ok(list);
    }

    @GetMapping("/{id}")
    public Result<LeaveRequest> getById(@PathVariable Long id) {
        LeaveRequest r = service.findById(id);
        return r != null ? Result.ok(r) : Result.error("申请不存在");
    }

    @PostMapping
    public Result<LeaveRequest> create(@Valid @RequestBody LeaveRequest request) {
        return Result.ok(service.save(request));
    }

    @PutMapping("/{id}")
    public Result<LeaveRequest> update(@PathVariable Long id, @Valid @RequestBody LeaveRequest request) {
        LeaveRequest updated = service.update(id, request);
        return updated != null ? Result.ok(updated) : Result.error("申请不存在");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return service.delete(id) ? Result.ok() : Result.error("申请不存在");
    }

    @PutMapping("/{id}/approve")
    public Result<LeaveRequest> approve(@PathVariable Long id, @RequestParam String status) {
        LeaveRequest approved = service.approve(id, status);
        return approved != null ? Result.ok(approved) : Result.error("申请不存在");
    }
}
