package com.xuegong.ai.langchain;

import com.xuegong.ai.AiMessage;
import com.xuegong.ai.AiProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class LangChainAiProvider implements AiProvider {

    @Autowired
    private AssistantService assistantService;

    @Value("${openai.api-key:}")
    private String apiKey;

    @Override
    public String getName() {
        return "langchain4j";
    }

    @Override
    public String getDisplayName() {
        return "LangChain4j (GPT-4 + Tools)";
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isEmpty() && assistantService.isAvailable();
    }

    @Override
    public String chat(String userMessage, List<AiMessage> history, String systemPrompt) {
        if (!assistantService.isAvailable()) {
            return "LangChain4j 未配置，请设置 openai.api-key";
        }
        try {
            String sessionId = "default";
            return assistantService.chat(sessionId, userMessage);
        } catch (Exception e) {
            log.error("LangChain4j 调用失败", e);
            return "抱歉，AI 服务暂时不可用：" + e.getMessage();
        }
    }
}
