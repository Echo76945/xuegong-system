package com.xuegong.ai.rag;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RagAssistant {

    @Autowired(required = false)
    private Optional<ChatLanguageModel> chatLanguageModel;

    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private DocumentLoader documentLoader;

    @Value("${ai.rag.enabled:false}")
    private boolean ragEnabled;

    @Value("${ai.rag.max-results:3}")
    private int maxResults;

    @Value("${ai.rag.min-score:0.7}")
    private double minScore;

    private RagAssistantService assistant;

    @PostConstruct
    public void init() {
        if (chatLanguageModel.isEmpty() || !embeddingService.isAvailable()) {
            log.warn("ChatLanguageModel 或 EmbeddingModel 未配置，RAG 助手不可用");
            return;
        }

        List<TextSegment> segments = documentLoader.loadKnowledgeDocuments();
        if (!segments.isEmpty()) {
            try {
                List<Embedding> embeddings = embeddingService.embedAll(segments);
                for (int i = 0; i < segments.size(); i++) {
                    embeddingStore.add(embeddings.get(i), segments.get(i));
                }
                log.info("已加载 {} 个知识片段到向量存储", segments.size());
            } catch (Exception e) {
                log.warn("加载知识库文档失败", e);
            }
        }

        assistant = AiServices.builder(RagAssistantService.class)
                .chatLanguageModel(chatLanguageModel.get())
                .contentRetriever(EmbeddingStoreContentRetriever.builder()
                        .embeddingStore(embeddingStore)
                        .embeddingModel(embeddingService.getEmbeddingModel())
                        .maxResults(maxResults)
                        .minScore(minScore)
                        .build())
                .build();
    }

    public boolean isAvailable() {
        return assistant != null && ragEnabled;
    }

    public String chat(String userMessage) {
        if (!isAvailable()) {
            return null;
        }
        return assistant.chat(userMessage);
    }

    public List<TextSegment> search(String query) {
        if (!embeddingService.isAvailable()) {
            return Collections.emptyList();
        }
        Embedding queryEmbedding = embeddingService.embed(query);
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(maxResults)
                .minScore(minScore)
                .build();
        EmbeddingSearchResult<TextSegment> result = embeddingStore.search(request);
        return result.matches().stream()
                .map(match -> match.embedded())
                .collect(Collectors.toList());
    }

    interface RagAssistantService {
        @SystemMessage("你是学工管理系统的AI智能助理。请根据提供的参考资料回答学生的问题。" +
                "如果参考资料中没有相关信息，请说明你不确定并建议学生咨询相关老师。")
        String chat(@UserMessage String userMessage);
    }
}
