package com.xuegong.controller;

import com.xuegong.ai.AiProvider;
import com.xuegong.common.Result;
import com.xuegong.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/chat")
    public Result<Map<String, String>> chat(@RequestBody Map<String, Object> body) {
        String message = (String) body.get("message");
        if (message == null || message.trim().isEmpty()) {
            return Result.error("消息不能为空");
        }

        @SuppressWarnings("unchecked")
        List<Map<String, String>> history = (List<Map<String, String>>) body.get("history");

        // 可选的提供者参数
        String provider = (String) body.get("provider");

        String reply = aiService.chat(message, history, provider);
        return Result.ok(Map.of("reply", reply));
    }

    @GetMapping("/providers")
    public Result<List<Map<String, Object>>> getProviders() {
        List<AiProvider> providers = aiService.getAllProviders();
        List<Map<String, Object>> result = providers.stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", p.getName());
            map.put("displayName", p.getDisplayName());
            map.put("available", p.isAvailable());
            return map;
        }).collect(Collectors.toList());
        return Result.ok(result);
    }
}
