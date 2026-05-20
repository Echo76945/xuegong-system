package com.xuegong.ai.rag;

import com.xuegong.common.Result;
import dev.langchain4j.data.segment.TextSegment;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai/knowledge")
@RequiredArgsConstructor
public class KnowledgeBaseController {

    private final RagAssistant ragAssistant;

    /**
     * 搜索知识库
     */
    @GetMapping("/search")
    public Result<List<String>> search(@RequestParam String query) {
        List<TextSegment> results = ragAssistant.search(query);
        List<String> contents = results.stream()
                .map(TextSegment::text)
                .collect(Collectors.toList());
        return Result.ok(contents);
    }
}
