package com.xuegong.ai.langchain;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConditionalOnProperty(name = "openai.api-key")
public class ChatMemoryProvider {

    @Value("${ai.chat-memory.max-messages:20}")
    private int maxMessages;

    private final Map<String, ChatMemory> memories = new ConcurrentHashMap<>();

    public ChatMemory getMemory(String sessionId) {
        return memories.computeIfAbsent(sessionId, id ->
                MessageWindowChatMemory.builder()
                        .maxMessages(maxMessages)
                        .build()
        );
    }

    public void clearMemory(String sessionId) {
        memories.remove(sessionId);
    }

    public void clearAll() {
        memories.clear();
    }
}
