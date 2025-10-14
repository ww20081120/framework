/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hbasesoft.framework.ai.agent.planning.model.vo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.hbasesoft.framework.ai.agent.planning.listener.ExecutionEventType;
import com.hbasesoft.framework.ai.agent.planning.listener.ExecutionListener;

/**
 * Execution context class for passing and maintaining state information during the creation, execution, and
 * summarization of plans. This class serves as the core data carrier in the plan execution process, passing between
 * various stages of {@link com.alibaba.cloud.ai.example.manus.planning.coordinator.PlanningCoordinator}. Main
 * responsibilities: - Store plan ID and plan entity information - Save user original request - Maintain plan execution
 * status - Store execution result summary - Control whether execution summary generation is needed
 *
 * @see com.alibaba.cloud.ai.example.manus.planning.model.vo.ExecutionPlan
 * @see com.alibaba.cloud.ai.example.manus.planning.coordinator.PlanningCoordinator
 */
public class ExecutionContext {

    private Map<String, String> toolsContext = new HashMap<>();

    /**
     * Tool context for storing context information of tool execution
     */
    /** Unique identifier of the plan */
    private String currentPlanId;

    private String rootPlanId;

    /**
     * Execution plan entity containing detailed plan information and execution steps
     */
    private PlanInterface plan;

    /** User's original request content */
    private String userRequest;

    private Long thinkActRecordId;

    /** Result summary after plan execution completion */
    private String resultSummary;

    /**
     * Whether to call large model to generate summary for execution results, true calls large model, false does not
     * call and outputs results directly
     */
    private boolean needSummary;

    /** Flag indicating whether plan execution was successful */
    private boolean success = false;

    /**
     * Whether to use memory, scenario is if only building plan, then memory should not be used, otherwise memory cannot
     * be deleted
     */
    private boolean useMemory = false;

    /**
     * Memory ID for memory usage
     */
    private String memoryId;

    /**
     * 执行监听器列表，用于监听计划执行过程中的各种事件
     */
    private List<ExecutionListener> listeners = new CopyOnWriteArrayList<>();

    /**
     * Get plan ID
     * 
     * @return Unique identifier of the plan
     */
    public String getCurrentPlanId() {
        return currentPlanId;
    }

    /**
     * Set plan ID
     * 
     * @param currentPlanId Unique identifier of the plan
     */
    public void setCurrentPlanId(String currentPlanId) {
        this.currentPlanId = currentPlanId;
    }

    public String getRootPlanId() {
        return rootPlanId;
    }

    public void setRootPlanId(String parentPlanId) {
        this.rootPlanId = parentPlanId;
    }

    /**
     * Get think-act record ID
     * 
     * @return Think-act record ID for sub-plan executions
     */
    public Long getThinkActRecordId() {
        return thinkActRecordId;
    }

    /**
     * Set think-act record ID
     * 
     * @param thinkActRecordId Think-act record ID for sub-plan executions
     */
    public void setThinkActRecordId(Long thinkActRecordId) {
        this.thinkActRecordId = thinkActRecordId;
    }

    /**
     * Get execution plan entity
     * 
     * @return Execution plan entity object
     */
    public PlanInterface getPlan() {
        return plan;
    }

    /**
     * Set execution plan entity
     * 
     * @param plan Execution plan entity object
     */
    public void setPlan(PlanInterface plan) {
        this.plan = plan;
    }

    /**
     * Check if execution result summary generation is needed
     * 
     * @return Returns true if summary generation is needed, otherwise false
     */
    public boolean isNeedSummary() {
        return needSummary;
    }

    /**
     * Set whether execution result summary generation is needed
     * 
     * @param needSummary Flag indicating whether summary generation is needed
     */
    public void setNeedSummary(boolean needSummary) {
        this.needSummary = needSummary;
    }

    /**
     * Check if plan execution was successful
     * 
     * @return Returns true if execution was successful, otherwise false
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Set plan execution success status
     * 
     * @param success Flag indicating execution success status
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Map<String, String> getToolsContext() {
        return toolsContext;
    }

    public void setToolsContext(Map<String, String> toolsContext) {
        this.toolsContext = toolsContext;
    }

    public void addToolContext(String toolsKey, String value) {
        this.toolsContext.put(toolsKey, value);
    }

    /**
     * Get user's original request content
     * 
     * @return User request string
     */
    public String getUserRequest() {
        return userRequest;
    }

    /**
     * Set user's original request content
     * 
     * @param userRequest User request string
     */
    public void setUserRequest(String userRequest) {
        this.userRequest = userRequest;
    }

    /**
     * Get execution result summary
     * 
     * @return Summary description of execution results
     */
    public String getResultSummary() {
        return resultSummary;
    }

    /**
     * Set execution result summary
     * 
     * @param resultSummary Summary description of execution results
     */
    public void setResultSummary(String resultSummary) {
        this.resultSummary = resultSummary;
    }

    /**
     * Update current instance with content from another ExecutionContext instance
     * <p>
     * This method copies the plan entity, user request, and result summary from the passed context to the current
     * instance
     * 
     * @param context Source execution context instance
     */
    public void updateContext(ExecutionContext context) {
        this.plan = context.getPlan();
        this.userRequest = context.getUserRequest();
        this.resultSummary = context.getResultSummary();
    }

    public boolean isUseMemory() {
        return useMemory;
    }

    public void setUseMemory(boolean useMemory) {
        this.useMemory = useMemory;
    }

    public String getMemoryId() {
        return memoryId;
    }

    public void setMemoryId(String memoryId) {
        this.memoryId = memoryId;
    }

    /**
     * 添加执行监听器
     * 
     * @param listener 要添加的监听器
     */
    public void addListener(ExecutionListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * 移除执行监听器
     * 
     * @param listener 要移除的监听器
     */
    public void removeListener(ExecutionListener listener) {
        listeners.remove(listener);
    }

    /**
     * 清除所有监听器
     */
    public void clearListeners() {
        listeners.clear();
    }

    /**
     * 获取所有监听器
     * 
     * @return 监听器列表
     */
    public List<ExecutionListener> getListeners() {
        return new CopyOnWriteArrayList<>(listeners);
    }

    /**
     * 通知所有监听器计划创建完成
     * 
     * @param plan 创建的计划
     */
    public void notifyPlanCreated(PlanInterface plan) {
        for (ExecutionListener listener : listeners) {
            try {
                listener.onPlanCreated(plan);
            } catch (Exception e) {
                // 监听器异常不应影响主流程
                System.err.println("Error notifying listener for plan created: " + e.getMessage());
            }
        }
    }

    /**
     * 通知所有监听器步骤开始
     * 
     * @param step 开始的步骤
     */
    public void notifyStepStart(ExecutionStep step) {
        for (ExecutionListener listener : listeners) {
            try {
                listener.onStepStart(step);
            } catch (Exception e) {
                System.err.println("Error notifying listener for step start: " + e.getMessage());
            }
        }
    }

    /**
     * 通知所有监听器步骤进度
     * 
     * @param step 当前步骤
     * @param progress 进度信息
     */
    public void notifyStepProgress(ExecutionStep step, String progress) {
        for (ExecutionListener listener : listeners) {
            try {
                listener.onStepProgress(step, progress);
            } catch (Exception e) {
                System.err.println("Error notifying listener for step progress: " + e.getMessage());
            }
        }
    }

    /**
     * 通知所有监听器步骤完成
     * 
     * @param step 完成的步骤
     * @param result 执行结果
     */
    public void notifyStepComplete(ExecutionStep step, String result) {
        for (ExecutionListener listener : listeners) {
            try {
                listener.onStepComplete(step, result);
            } catch (Exception e) {
                System.err.println("Error notifying listener for step complete: " + e.getMessage());
            }
        }
    }

    /**
     * 通知所有监听器总结流式输出
     * 
     * @param chunk 流式文本片段
     */
    public void notifySummaryStream(String chunk) {
        for (ExecutionListener listener : listeners) {
            try {
                listener.onSummaryStream(chunk);
            } catch (Exception e) {
                System.err.println("Error notifying listener for summary stream: " + e.getMessage());
            }
        }
    }

    /**
     * 通知所有监听器执行完成
     */
    public void notifyExecutionComplete() {
        for (ExecutionListener listener : listeners) {
            try {
                listener.onExecutionComplete(this);
            } catch (Exception e) {
                System.err.println("Error notifying listener for execution complete: " + e.getMessage());
            }
        }
    }

    /**
     * 通知所有监听器发生错误
     * 
     * @param error 错误信息
     */
    public void notifyError(Exception error) {
        for (ExecutionListener listener : listeners) {
            try {
                listener.onError(error);
            } catch (Exception e) {
                System.err.println("Error notifying listener for error: " + e.getMessage());
            }
        }
    }

    /**
     * 通知所有监听器状态变化
     * 
     * @param status 状态描述
     */
    public void notifyStatusChange(String status) {
        for (ExecutionListener listener : listeners) {
            try {
                listener.onStatusChange(this, status);
            } catch (Exception e) {
                System.err.println("Error notifying listener for status change: " + e.getMessage());
            }
        }
    }

    /**
     * 通知所有监听器大模型流式响应
     * 
     * @param response 流式响应片段
     * @param responseType 响应类型
     */
    public void notifyLlmResponseStream(String response, String responseType) {
        for (ExecutionListener listener : listeners) {
            try {
                listener.onLlmResponseStream(this, response, responseType);
            } catch (Exception e) {
                System.err.println("Error notifying listener for LLM response stream: " + e.getMessage());
            }
        }
    }

}
