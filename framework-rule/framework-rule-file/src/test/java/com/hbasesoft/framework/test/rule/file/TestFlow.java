/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.test.rule.file;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hbasesoft.framework.boostrap.normal.Application;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.rule.core.FlowHelper;
import com.hbasesoft.framework.test.rule.file.bean.FlowBean;

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
public class TestFlow implements ApplicationContextAware {

    @Test
    public void flowStart() {
        FlowBean flowBean = new FlowBean();
        flowBean.setTransId(CommonUtil.getTransactionID());
        FlowHelper.flowStart(flowBean, "demo");
        FlowHelper.flowStart(flowBean, "demo02");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param applicationContext
     * @throws BeansException <br>
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ContextHolder.setContext(applicationContext);
    }
}
