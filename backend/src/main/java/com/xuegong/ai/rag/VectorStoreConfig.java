package com.xuegong.ai.rag;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class VectorStoreConfig {

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        log.info("初始化内存向量存储");
        return new InMemoryEmbeddingStore<>();
    }
}
