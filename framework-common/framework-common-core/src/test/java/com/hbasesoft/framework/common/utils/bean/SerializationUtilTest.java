/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.bean;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年9月12日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.utils.bean <br>
 */
public class SerializationUtilTest {

    /** number */
    private static final int NUM_18 = 18;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void serial() {
        TestBean bean = new TestBean("hello world", NUM_18);
        byte[] bs = SerializationUtil.serial(bean);
        TestBean bean2 = SerializationUtil.unserial(TestBean.class, bs);
        assertEquals(bean.getAge(), bean2.getAge());
        assertEquals(bean.getName(), bean2.getName());
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @SuppressWarnings("unchecked")
    @Test
    public void serial2() {

        TestBean bean = new TestBean("hello world", NUM_18);
        Map<String, Object> testMap = new HashMap<>();
        testMap.put("test", bean);
        byte[] bs = SerializationUtil.serial(testMap);

        Map<String, TestBean> bean2 = SerializationUtil.unserial(Map.class, bs);
        assertEquals(bean2.get("test").getName(), bean.getName());
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @SuppressWarnings("unchecked")
    @Test
    public void serial3() {
        TestBean bean = new TestBean("hello world", NUM_18);
        byte[] bs = SerializationUtil.serial(Arrays.asList(bean));
        List<TestBean> beans = SerializationUtil.unserial(List.class, bs);
        Assert.equals(beans.get(0).getName(), "hello world", ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void jdkSerial() {
        TestBean bean = new TestBean("hello world", NUM_18);
        byte[] bs = SerializationUtil.jdkSerial(bean);
        TestBean bean2 = (TestBean) SerializationUtil.jdkUnserial(bs);
        Assert.equals(bean2.getName(), bean.getName(), ErrorCodeDef.FAILURE);

    }
}
