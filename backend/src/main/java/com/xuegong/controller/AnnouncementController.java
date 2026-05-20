package com.xuegong.controller;

import com.xuegong.common.Result;
import com.xuegong.entity.Announcement;
import com.xuegong.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService service;

    @GetMapping
    public Result<List<Announcement>> list(@RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) String scope) {
        List<Announcement> list;
        if (scope != null && !scope.isEmpty()) {
            list = service.findByScope(scope);
        } else if (keyword != null && !keyword.isEmpty()) {
            list = service.search(keyword);
        } else {
            list = service.findAll();
        }
        return Result.ok(list);
    }

    @GetMapping("/{id}")
    public Result<Announcement> getById(@PathVariable Long id) {
        Announcement a = service.findById(id);
        if (a != null) {
            service.incrementViews(id);
            return Result.ok(a);
        }
        return Result.error("公告不存在");
    }

    @PostMapping
    public Result<Announcement> create(@Valid @RequestBody Announcement announcement) {
        return Result.ok(service.save(announcement));
    }

    @PutMapping("/{id}")
    public Result<Announcement> update(@PathVariable Long id, @Valid @RequestBody Announcement announcement) {
        Announcement updated = service.update(id, announcement);
        return updated != null ? Result.ok(updated) : Result.error("公告不存在");
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return service.delete(id) ? Result.ok() : Result.error("公告不存在");
    }
}
