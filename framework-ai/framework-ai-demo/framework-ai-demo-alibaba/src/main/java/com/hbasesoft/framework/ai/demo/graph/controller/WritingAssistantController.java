package com.hbasesoft.framework.ai.demo.graph.controller;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/graph/writingAssistant")
public class WritingAssistantController {

    /** 编译后的状态图 */
    private final CompiledGraph compiledGraph;

    /**
     * 构造函数
     *
     * @param stateGraph 状态图
     * @throws GraphStateException 状态图异常
     */
    public WritingAssistantController(final StateGraph stateGraph) throws GraphStateException {
        this.compiledGraph = stateGraph.compile();
    }

    /**
     * 写作助手接口
     *
     * @param input 输入文本
     * @return 处理结果
     */
    @GetMapping("/write")
    public Map<String, Object> write(final String input) {
        var resultFuture = compiledGraph.invoke(Map.of("original_text", input));
        var result = resultFuture.get();
        return result.data();
    }
}
