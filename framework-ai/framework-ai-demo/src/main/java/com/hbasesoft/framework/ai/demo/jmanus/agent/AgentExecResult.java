package com.hbasesoft.framework.ai.demo.jmanus.agent;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AgentExecResult {

    private String result;

    private AgentState state;

}
