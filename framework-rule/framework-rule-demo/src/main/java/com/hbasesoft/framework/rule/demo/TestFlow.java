/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.rule.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.alibaba.fastjson2.JSONObject;
import com.hbasesoft.framework.common.ErrorCode;
import com.hbasesoft.framework.rule.core.FlowHelper;
import com.hbasesoft.framework.rule.core.antv.AntVFlowHelper;
import com.hbasesoft.framework.rule.demo.bean.FlowBean;
import com.hbasesoft.framework.rule.demo.bean.TestStateMachineBean;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月4日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.test.rule.file <br>
 */
@SpringBootTest()
public class TestFlow {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void flowStart() {
        FlowBean flowBean = new FlowBean();
        FlowHelper.flowStart(flowBean, "demo");

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void testStateMachineBean() {
        TestStateMachineBean bean = new TestStateMachineBean();
        bean.setEvent("e1");
        System.out.println(JSONObject.toJSONString(bean));
        ErrorCode result = FlowHelper.flowStart(bean, "stateMachine");
        System.out.println(result);
        System.out.println(JSONObject.toJSONString(bean));
        result = FlowHelper.flowStart(bean, "stateMachine");
        System.out.println(result);
        System.out.println(JSONObject.toJSONString(bean));
        result = FlowHelper.flowStart(bean, "stateMachine");
        System.out.println(result);
        System.out.println(JSONObject.toJSONString(bean));
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void testAntv() {
        String rule = "{\"nodes\":[{\"id\":\"1\",\"component\":\"Child01Component\","
            + "\"size\":\"80*48\",\"position\":\"100, 50\",\"label\":\"学生提交请假申请\"},"
            + "{\"id\":\"2\",\"component\":\"Child02Component\",\"size\":"
            + "\"80*48\",\"position\":\"300, 50\",\"label\":\"班长审批\"},"
            + "{\"id\":\"3\",\"component\":\"Child03Component\",\"size\":"
            + "\"80*48\",\"position\":\"500, 50\",\"label\":\"组长审批\"},"
            + "{\"id\":\"4\",\"component\":\"Child04Component\",\"size\":"
            + "\"80*48\",\"position\":\"700, 50\",\"label\":\"老师审批\"},"
            + "{\"id\":\"5\",\"component\":\"Child01Component\",\"size\":"
            + "\"80*48\",\"position\":\"900, 50\",\"label\":\"年级主任审批\"},"
            + "{\"id\":\"6\",\"component\":\"Child02Component\",\"size\":"
            + "\"80*48\",\"position\":\"1100, 50\",\"label\":\"校长审批\"},"
            + "{\"id\":\"7\",\"component\":\"DemoComponent\",\"size\":"
            + "\"80*48\",\"position\":\"1300, 50\",\"label\":\"流程结束\"}],"
            + "\"edges\":[{\"source\":\"1\",\"target\":\"2\",\"label\":\"通过\"},"
            + "{\"source\":\"2\",\"target\":\"3\",\"label\":\"通过\"},"
            + "{\"source\":\"3\",\"target\":\"4\",\"label\":\"通过\"},"
            + "{\"source\":\"4\",\"target\":\"5\",\"label\":\"通过\"},"
            + "{\"source\":\"5\",\"target\":\"6\",\"label\":\"通过\"},"
            + "{\"source\":\"6\",\"target\":\"7\",\"label\":\"通过\"},"
            + "{\"source\":\"2\",\"target\":\"7\",\"label\":\"拒绝\"},"
            + "{\"source\":\"3\",\"target\":\"7\",\"label\":\"拒绝\"},"
            + "{\"source\":\"4\",\"target\":\"7\",\"label\":\"拒绝\"},"
            + "{\"source\":\"5\",\"target\":\"7\",\"label\":\"拒绝\"},"
            + "{\"source\":\"6\",\"target\":\"7\",\"label\":\"拒绝\"}]}";
        TestStateMachineBean bean = new TestStateMachineBean();
        String result = AntVFlowHelper.flowStart(bean, rule);
        System.out.println(result);
    }
}
