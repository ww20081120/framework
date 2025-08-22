/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.demo.jmanus;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hbasesoft.framework.ai.jmanus.dynamic.memory.service.MemoryService;
import com.hbasesoft.framework.ai.jmanus.dynamic.memory.vo.MemoryVo;
import com.hbasesoft.framework.ai.jmanus.planning.PlanningFactory;
import com.hbasesoft.framework.ai.jmanus.planning.coordinator.PlanIdDispatcher;
import com.hbasesoft.framework.ai.jmanus.planning.coordinator.PlanningCoordinator;
import com.hbasesoft.framework.ai.jmanus.planning.model.vo.ExecutionContext;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.demo.jmanus <br>
 */
@RestController
@RequestMapping("/api/executor")
public class ManusController {

	@Autowired
	@Lazy
	private PlanningFactory planningFactory;

	@Autowired
	private PlanIdDispatcher planIdDispatcher;

	@Autowired
	private MemoryService memoryService;

	@PostMapping("/execute")
	public ResponseEntity<Map<String, Object>> executeQuery(@RequestBody Map<String, String> request) {
		String query = request.get("input");
		if (query == null || query.trim().isEmpty()) {
			return ResponseEntity.badRequest().body(Map.of("error", "Query content cannot be empty"));
		}
		ExecutionContext context = new ExecutionContext();
		context.setUserRequest(query);
		// Use PlanIdDispatcher to generate a unique plan ID
		String planId = planIdDispatcher.generatePlanId();
		context.setCurrentPlanId(planId);
		context.setRootPlanId(planId);
		context.setNeedSummary(true);

		String memoryId = request.get("memoryId");

		if (StringUtils.isEmpty(memoryId)) {
			memoryId = RandomStringUtils.randomAlphabetic(8);
		}

		context.setMemoryId(memoryId);

		// Get or create planning flow
		PlanningCoordinator planningFlow = planningFactory.createPlanningCoordinator(context);

		// Asynchronous execution of task
		CompletableFuture.supplyAsync(() -> {
			try {
				memoryService.saveMemory(new MemoryVo(context.getMemoryId(), query));
				return planningFlow.executePlan(context);
			} catch (Exception e) {
				LoggerUtil.error("Failed to execute plan", e);
				throw new RuntimeException("Failed to execute plan: " + e.getMessage(), e);
			}
		});

		// Return task ID and initial status
		Map<String, Object> response = new HashMap<>();
		response.put("planId", planId);
		response.put("status", "processing");
		response.put("message", "Task submitted, processing");
		response.put("memoryId", memoryId);

		return ResponseEntity.ok(response);
	}
}
