package com.hbasesoft.framework.ai.jmanus.planning.controller;

import com.hbasesoft.framework.ai.jmanus.planning.model.vo.ExecutionContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/api/executor")
@RestController
public class ManusController {

    /**
     * 异步执行Manus 请求
     *
     * @param request 包含用户查询的请求
     * @return 任务ID及状态
     */
    @PostMapping("/execute")
    public ResponseEntity<Map<String, Object>> executeQuery(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        if (StringUtils.isEmpty(query)) {
            return ResponseEntity.badRequest().body(Map.of("error", "查询内容不能为空"));
        }

        ExecutionContext context = new ExecutionContext();
        context.setUserRequest(query);

        // 使用 PlanIdDispatcher 生成唯一的计划ID
        return null;
    }
}
