package com.xuegong.ai.rag;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class EmbeddingService {

    @Getter
    @Autowired(required = false)
    private EmbeddingModel embeddingModel;

    public boolean isAvailable() {
        return embeddingModel != null;
    }

    public Embedding embed(String text) {
        if (embeddingModel == null) {
            throw new IllegalStateException("EmbeddingModel 未配置");
        }
        return embeddingModel.embed(text).content();
    }

    public List<Embedding> embedAll(List<TextSegment> segments) {
        if (embeddingModel == null) {
            throw new IllegalStateException("EmbeddingModel 未配置");
        }
        return embeddingModel.embedAll(segments).content();
    }
}
