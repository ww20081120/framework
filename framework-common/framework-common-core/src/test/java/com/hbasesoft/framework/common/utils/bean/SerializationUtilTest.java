/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.bean;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.security.DataUtil;

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
        String hexStr = DataUtil.byte2HexStr(bs);
        assertEquals(hexStr, "0A0B68656C6C6F20776F726C641012");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void serial2() {

        TestBean bean = new TestBean("hello world", NUM_18);
        Map<String, Object> testMap = new HashMap<>();
        testMap.put("test", bean);
        byte[] bs = SerializationUtil.serial(testMap);
        String hexStr = DataUtil.byte2HexStr(bs);
        assertEquals(hexStr,
            "ACED0005737200116A6176612E7574696C2E486173684D61700507DAC1C31660D1"
                + "03000246000A6C6F6164466163746F724900097468726573686F6C6478703F400"
                + "0000000000C770800000010000000017400047465737473720032636F6D2E6862"
                + "617365736F66742E6672616D65776F726B2E636F6D6D6F6E2E7574696C732E626"
                + "5616E2E546573744265616E0D3AF53B8EBFF7290200024900036167654C00046E"
                + "616D657400124C6A6176612F6C616E672F537472696E673B78700000001274000B68656C6C6F20776F726C6478");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void serial3() {
        TestBean bean = new TestBean("hello world", NUM_18);
        byte[] bs = SerializationUtil.serial(Arrays.asList(bean));
        String hexStr = DataUtil.byte2HexStr(bs);
        assertEquals(hexStr,
            "ACED00057372001A6A6176612E7574696C2E4172726179732441727261794C697374D9A43CBE"
                + "CD8806D20200015B0001617400135B4C6A6176612F6C616E672F4F626A6563743B7870757200355B4C636F6D2E68"
                + "62617365736F66742E6672616D65776F726B2E636F6D6D6F6E2E7574696C732E6265616E2E546573744265616E3B"
                + "511CAEB0B005B16102000078700000000173720032636F6D2E6862617365736F66742E6672616D65776F726B2E63"
                + "6F6D6D6F6E2E7574696C732E6265616E2E546573744265616E0D3AF53B8EBFF7290200024900036167654C00046E"
                + "616D657400124C6A6176612F6C616E672F537472696E673B78700000001274000B68656C6C6F20776F726C64");
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
        String hexStr = DataUtil.byte2HexStr(bs);
        assertEquals(hexStr,
            "ACED000573720032636F6D2E6862617365736F66742E6672616D65776F726B2E636"
                + "F6D6D6F6E2E7574696C732E6265616E2E546573744265616E0D3AF53B8EBFF729"
                + "0200024900036167654C00046E616D657400124C6A6176612F6C616E672F537472"
                + "696E673B78700000001274000B68656C6C6F20776F726C64");

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void unserial() {
        byte[] bs = DataUtil.hexStr2Byte("0A0B68656C6C6F20776F726C641012");
        TestBean bean = SerializationUtil.unserial(TestBean.class, bs);
        assertEquals(bean.getName(), "hello world");
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
    public void unserial2() {
        byte[] bs = DataUtil.hexStr2Byte("ACED0005737200116A6176612E7574696C2E486173684D61700507DAC1C31660D1"
            + "03000246000A6C6F6164466163746F724900097468726573686F6C6478703F400"
            + "0000000000C770800000010000000017400047465737473720032636F6D2E6862"
            + "617365736F66742E6672616D65776F726B2E636F6D6D6F6E2E7574696C732E626"
            + "5616E2E546573744265616E0D3AF53B8EBFF7290200024900036167654C00046E"
            + "616D657400124C6A6176612F6C616E672F537472696E673B78700000001274000B68656C6C6F20776F726C6478");
        Map<String, TestBean> bean = SerializationUtil.unserial(Map.class, bs);
        assertEquals(bean.get("test").getName(), "hello world");
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
    public void unserial3() {
        TestBean bean = new TestBean();
        bean.setName("hello world");
        bean.setAge(1);
        List<TestBean> list = new ArrayList<TestBean>();
        list.add(bean);
        byte[] bs = SerializationUtil.serial(list);

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
    @SuppressWarnings("unchecked")
    @Test
    public void jdkUnserial() {
        TestBean bean = new TestBean();
        bean.setName("hello world");
        bean.setAge(1);
        List<TestBean> list = new ArrayList<TestBean>();
        list.add(bean);
        byte[] bs = SerializationUtil.jdkSerial(list);
        List<TestBean> beans = (List<TestBean>) SerializationUtil.jdkUnserial(bs);
        Assert.equals(beans.get(0).getName(), "hello world", ErrorCodeDef.FAILURE);
    }
}
