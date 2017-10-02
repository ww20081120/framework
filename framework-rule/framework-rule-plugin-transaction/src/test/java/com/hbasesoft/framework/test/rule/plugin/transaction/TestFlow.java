/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.test.rule.plugin.transaction;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hbasesoft.framework.boostrap.normal.Application;
import com.hbasesoft.framework.rule.core.FlowHelper;

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
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class TestFlow {

    @Test
    public void flowStart() {
        TestFlowBean flowBean = new TestFlowBean();
        flowBean.setName("小华");
        flowBean.setAge(10);
        FlowHelper.flowStart(flowBean, "demo");
        System.out.println("------------华丽的分割线---2-------------");
        flowBean = new TestFlowBean();
        flowBean.setName("小明");
        flowBean.setAge(15);
        FlowHelper.flowStart(flowBean, "demo");
        System.out.println("------------华丽的分割线---3-------------");
        flowBean = new TestFlowBean();
        flowBean.setName("小丽");
        flowBean.setAge(30);
        FlowHelper.flowStart(flowBean, "demo");
    }

}
