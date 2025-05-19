package com.hbasesoft.framework.ai.demo.graph.controller;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.GraphStateException;
import com.alibaba.cloud.ai.graph.StateGraph;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/graph/writingAssistant")
public class WritingAssistantController {

    private final CompiledGraph compiledGraph;

    public WritingAssistantController(final StateGraph stateGraph) throws GraphStateException {
        this.compiledGraph = stateGraph.compile();
    }

    @GetMapping("/write")
    public Map<String, Object> write(final String input) {
        var resultFuture = compiledGraph.invoke(Map.of("original_text", input));
        var result = resultFuture.get();
        return result.data();
    }
}
