package com.xuegong.controller;

import com.xuegong.common.Result;
import com.xuegong.entity.Incident;
import com.xuegong.service.IncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService service;

    @GetMapping
    public Result<List<Incident>> list(@RequestParam(required = false) String keyword,
                                       @RequestParam(required = false) String severity,
                                       @RequestParam(required = false) String status) {
        List<Incident> list;
        if (severity != null && !severity.isEmpty()) {
            list = service.findBySeverity(severity);
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
    public Result<Incident> getById(@PathVariable Long id) {
        Incident i = service.findById(id);
        return i != null ? Result.ok(i) : Result.error("上报不存在");
    }

    @PostMapping
    public Result<Incident> create(@Valid @RequestBody Incident incident) {
        return Result.ok(service.save(incident));
    }

    @PutMapping("/{id}")
    public Result<Incident> update(@PathVariable Long id, @Valid @RequestBody Incident incident) {
        Incident updated = service.update(id, incident);
        return updated != null ? Result.ok(updated) : Result.error("上报不存在");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return service.delete(id) ? Result.ok() : Result.error("上报不存在");
    }

    @PutMapping("/{id}/process")
    public Result<Incident> process(@PathVariable Long id, @RequestParam String handler) {
        Incident processed = service.process(id, handler);
        return processed != null ? Result.ok(processed) : Result.error("上报不存在");
    }

    @PutMapping("/{id}/resolve")
    public Result<Incident> resolve(@PathVariable Long id) {
        Incident resolved = service.resolve(id);
        return resolved != null ? Result.ok(resolved) : Result.error("上报不存在");
    }
}
