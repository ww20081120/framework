package com.hbasesoft.framework.ai.jmanus.tool.code;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ToolExecuteResult {

    /**
     * 工具返回的内容
     */
    private String output;

    /**
     * 是否被中断
     */
    private String interrupted;

    public ToolExecuteResult(String output) {
        this.output = output;
    }

}
