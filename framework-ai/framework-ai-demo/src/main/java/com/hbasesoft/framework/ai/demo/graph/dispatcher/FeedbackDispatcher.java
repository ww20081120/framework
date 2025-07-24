package com.hbasesoft.framework.ai.demo.graph.dispatcher;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.EdgeAction;

public class FeedbackDispatcher implements EdgeAction {
    @Override
    public String apply(final OverAllState state) throws Exception {
        String feedback = (String) state.value("summary_feedback").orElse("");
        if (feedback.contains("positive")) {
            return "positive";
        }
        return "negative";
    }
}
