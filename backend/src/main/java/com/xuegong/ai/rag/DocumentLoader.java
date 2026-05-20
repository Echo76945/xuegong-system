package com.xuegong.ai.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DocumentLoader {

    private final DocumentParser parser = new TextDocumentParser();
    private final DocumentSplitter splitter = DocumentSplitters.recursive(500, 50);

    /**
     * 加载 classpath 下的知识库文档
     */
    public List<TextSegment> loadKnowledgeDocuments() {
        List<TextSegment> segments = new ArrayList<>();
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:knowledge/*.txt");

            for (Resource resource : resources) {
                try {
                    Document document = parser.parse(resource.getInputStream());
                    document.metadata().put("source", resource.getFilename());
                    List<TextSegment> splits = splitter.split(document);
                    segments.addAll(splits);
                    log.info("已加载知识文档: {}, 分段数: {}", resource.getFilename(), splits.size());
                } catch (Exception e) {
                    log.error("加载文档失败: {}", resource.getFilename(), e);
                }
            }
        } catch (IOException e) {
            log.warn("未找到知识库文档目录", e);
        }
        return segments;
    }
}
