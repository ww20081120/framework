package com.hbasesoft.framework.ai.demo.jmanus.agent;

public enum AgentState {

    /** 未启动 */
    NOT_STARTED("not_started"),

    /** 运行中 */
    IN_PROGRESS("in_progress"),

    /** 已完成 */
    COMPLETED("completed"),

    /** 已阻塞 */
    BLOCKED("blocked"),

    /** 已失败 */
    FAILED("failed");

    /** 状态 */
    private final String state;

    /**
     * Description: 构造函数  <br>
     *
     * @param state <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    AgentState(final String state) {
        this.state = state;
    }

    /**
     * Description:  <br>
     *
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public String toString() {
        return state;
    }
}
