package com.xuegong.service;

import com.xuegong.ai.AiMessage;
import com.xuegong.ai.AiProvider;
import com.xuegong.ai.AiProviderFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final AiProviderFactory providerFactory;

    private static final String SYSTEM_PROMPT =
            "你是学工管理系统的AI智能助理，专门解答学生关于学业、生活、奖助、请假、活动等方面的问题。" +
            "请用友好、专业的语气回答，回答尽量简洁明了。";

    /**
     * 使用默认提供者进行对话
     */
    public String chat(String userMessage, List<Map<String, String>> history) {
        return chat(userMessage, history, null);
    }

    /**
     * 使用指定提供者进行对话
     */
    public String chat(String userMessage, List<Map<String, String>> history, String providerName) {
        AiProvider provider = providerName != null ?
                providerFactory.getProvider(providerName) :
                providerFactory.getProvider();

        if (provider == null) {
            return "AI 服务未配置，请在 application.properties 中配置 AI 提供者的 API Key。";
        }

        // 转换历史消息格式
        List<AiMessage> aiHistory = new ArrayList<>();
        if (history != null) {
            for (Map<String, String> msg : history) {
                String role = msg.get("role");
                String content = msg.get("content");
                if (("user".equals(role) || "assistant".equals(role)) && content != null) {
                    aiHistory.add(new AiMessage(role, content));
                }
            }
        }

        return provider.chat(userMessage, aiHistory, SYSTEM_PROMPT);
    }

    /**
     * 获取所有可用的 AI 提供者
     */
    public List<AiProvider> getAvailableProviders() {
        return providerFactory.getAvailableProviders();
    }

    /**
     * 获取所有 AI 提供者
     */
    public List<AiProvider> getAllProviders() {
        return providerFactory.getAllProviders();
    }
}
