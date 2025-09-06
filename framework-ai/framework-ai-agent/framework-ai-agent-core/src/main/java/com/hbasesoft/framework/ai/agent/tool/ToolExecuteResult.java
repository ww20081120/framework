package com.hbasesoft.framework.ai.agent.tool;

public class ToolExecuteResult {

    public ToolExecuteResult() {

    }

    public ToolExecuteResult(String output) {
        setOutput(output);
    }

    public ToolExecuteResult(String output, boolean interrupted) {
        setOutput(output);
        setInterrupted(interrupted);
    }

    /**
     * Content returned by the tool
     */
    private String output;

    /**
     * Whether interrupted
     */
    private boolean interrupted;

    public String getOutput() {
        return output;
    }

    void setOutput(String output) {
        this.output = output;
    }

    boolean isInterrupted() {
        return interrupted;
    }

    void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

}
