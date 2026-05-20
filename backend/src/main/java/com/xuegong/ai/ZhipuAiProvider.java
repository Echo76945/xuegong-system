package com.xuegong.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Component
public class ZhipuAiProvider implements AiProvider {

    @Value("${zhipu.api-key:}")
    private String apiKey;

    @Value("${zhipu.api-url:https://open.bigmodel.cn/api/paas/v4/chat/completions}")
    private String apiUrl;

    @Value("${zhipu.model:glm-4.5-flash}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String getName() {
        return "zhipu";
    }

    @Override
    public String getDisplayName() {
        return "智谱 AI (GLM-4)";
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isEmpty();
    }

    @Override
    public String chat(String userMessage, List<AiMessage> history, String systemPrompt) {
        try {
            List<Map<String, String>> messages = new ArrayList<>();

            // 系统提示
            if (systemPrompt != null && !systemPrompt.isEmpty()) {
                messages.add(Map.of("role", "system", "content", systemPrompt));
            }

            // 历史消息
            if (history != null) {
                for (AiMessage msg : history) {
                    if ("user".equals(msg.getRole()) || "assistant".equals(msg.getRole())) {
                        messages.add(Map.of("role", msg.getRole(), "content", msg.getContent()));
                    }
                }
            }

            // 当前消息
            messages.add(Map.of("role", "user", "content", userMessage));

            // 构建请求
            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("messages", messages);
            body.put("temperature", 0.7);
            body.put("max_tokens", 1024);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, Map.class);

            if (response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }

            return "抱歉，AI 未能生成回复。";
        } catch (Exception e) {
            log.error("智谱 AI 调用失败", e);
            return "抱歉，AI 服务暂时不可用：" + e.getMessage();
        }
    }
}
