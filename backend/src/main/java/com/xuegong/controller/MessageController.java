package com.xuegong.controller;

import com.xuegong.common.Result;
import com.xuegong.entity.Message;
import com.xuegong.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService service;

    @GetMapping
    public Result<List<Message>> list(@RequestParam(required = false) Long userId) {
        if (userId != null) {
            return Result.ok(service.findByUserId(userId));
        }
        return Result.ok(service.findAll());
    }

    @GetMapping("/conversation")
    public Result<List<Message>> getConversation(@RequestParam Long userId1, @RequestParam Long userId2) {
        return Result.ok(service.findConversation(userId1, userId2));
    }

    @GetMapping("/{id}")
    public Result<Message> getById(@PathVariable Long id) {
        Message m = service.findById(id);
        return m != null ? Result.ok(m) : Result.error("消息不存在");
    }

    @PostMapping
    public Result<Message> create(@RequestBody Message message) {
        return Result.ok(service.save(message));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return service.delete(id) ? Result.ok() : Result.error("消息不存在");
    }

    @PutMapping("/{id}/read")
    public Result<Message> markAsRead(@PathVariable Long id) {
        Message marked = service.markAsRead(id);
        return marked != null ? Result.ok(marked) : Result.error("消息不存在");
    }
}
