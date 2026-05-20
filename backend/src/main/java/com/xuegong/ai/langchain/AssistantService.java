package com.xuegong.ai.langchain;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class AssistantService {

    @Autowired(required = false)
    private Optional<ChatLanguageModel> chatLanguageModel;

    @Autowired
    private ChatMemoryProvider chatMemoryProvider;

    @Autowired
    private ApplicationContext applicationContext;

    private Assistant assistant;

    @PostConstruct
    public void init() {
        if (chatLanguageModel.isEmpty()) {
            log.warn("ChatLanguageModel 未配置，LangChain4j Assistant 不可用");
            return;
        }

        // 收集所有工具类（StudentInfoTool 等）
        Map<String, Object> toolBeans = applicationContext.getBeansWithAnnotation(org.springframework.stereotype.Component.class);
        Object[] tools = toolBeans.values().stream()
                .filter(bean -> bean.getClass().getAnnotation(dev.langchain4j.agent.tool.Tool.class) != null ||
                        hasToolMethods(bean))
                .toArray();

        assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(chatLanguageModel.get())
                .chatMemoryProvider(memoryId -> chatMemoryProvider.getMemory((String) memoryId))
                .tools(tools)
                .build();
        log.info("LangChain4j Assistant 初始化完成，加载工具数: {}", tools.length);
    }

    private boolean hasToolMethods(Object bean) {
        for (java.lang.reflect.Method method : bean.getClass().getMethods()) {
            if (method.isAnnotationPresent(dev.langchain4j.agent.tool.Tool.class)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAvailable() {
        return assistant != null;
    }

    public String chat(String sessionId, String userMessage) {
        if (assistant == null) {
            return "LangChain4j 未配置，请设置 openai.api-key";
        }
        return assistant.chat(sessionId, userMessage);
    }

    public void clearMemory(String sessionId) {
        chatMemoryProvider.clearMemory(sessionId);
    }

    public interface Assistant {
        @SystemMessage("你是学工管理系统的AI智能助理，专门解答学生关于学业、生活、奖助、请假、活动等方面的问题。" +
                "请用友好、专业的语气回答，回答尽量简洁明了。" +
                "你可以查询学生信息、班级信息等。")
        String chat(@MemoryId String sessionId, @UserMessage String userMessage);
    }
}
