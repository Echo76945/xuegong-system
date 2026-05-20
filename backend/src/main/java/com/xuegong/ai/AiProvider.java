package com.xuegong.ai;

import java.util.List;

public interface AiProvider {

    /**
     * 获取提供者名称
     */
    String getName();

    /**
     * 获取显示名称
     */
    String getDisplayName();

    /**
     * 是否可用
     */
    boolean isAvailable();

    /**
     * 发送聊天请求
     */
    String chat(String userMessage, List<AiMessage> history, String systemPrompt);
}
