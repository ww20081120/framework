/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.bean;

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
        Assert.equals(hexStr, "02FF0187007759BC931200000000000B68656C6C6F20776F726C64", ErrorCodeDef.FAILURE);
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
        System.out.println(hexStr);
        Assert.equals(hexStr,
            "02FF01220001FF011600000474657374FF000090D63AE12774FDBF2900636F6D2E6"
                + "862617365736F66742E6672616D65776F726B2E636F6D6D6F6E2E7574696C732E6"
                + "265616E002AD0CC76A6AA18500800546573744265616E7759BC931200000000000B" + "68656C6C6F20776F726C64",
            ErrorCodeDef.FAILURE);
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
        System.out.println(hexStr);
        Assert.equals(hexStr,
            "02FF014600FF0000291DABAA34ECF06F2B005B4C636F6D2E6862617365736F66742"
                + "E6672616D65776F726B2E636F6D6D6F6E2E7574696C732E6265616E00829058FFA"
                + "8AFCFBC0900546573744265616E3B01000000FF000090D63AE12774FDBF2900636"
                + "F6D2E6862617365736F66742E6672616D65776F726B2E636F6D6D6F6E2E7574696"
                + "C732E6265616E002AD0CC76A6AA18500800546573744265616E7759BC931200000000000B68656C6C6F20776F726C64",
            ErrorCodeDef.FAILURE);
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
        Assert.equals(hexStr,
            "ACED00057372002E636F6D2E6862617365736F66742E6672616D65776F726B2E636F6D6"
                + "D6F6E2E7574696C732E6265616E2E4265616E0D3AF53B8EBFF729020002490003616765"
                + "4C00046E616D657400124C6A6176612F6C616E672F537472696E673B78700000001274000B68656C6C6F20776F726C64",
            ErrorCodeDef.FAILURE);

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
        byte[] bs = DataUtil.hexStr2Byte("02FF0187007759BC931200000000000B68656C6C6F20776F726C64");
        TestBean bean = SerializationUtil.unserial(TestBean.class, bs);
        Assert.equals(bean.getName(), "hello world", ErrorCodeDef.FAILURE);
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
        byte[] bs = DataUtil.hexStr2Byte(
            "02FF01220001FF011600000474657374FF000090D63AE12774FDBF2900636F6D2E6862617365736F66742E6672616D6"
                + "5776F726B2E636F6D6D6F6E2E7574696C732E6265616E002AD0CC76A6AA18500800546573744265616E7759BC9"
                + "31200000000000B68656C6C6F20776F726C64");
        Map<String, TestBean> bean = SerializationUtil.unserial(Map.class, bs);
        System.out.println(bean);
        Assert.equals(bean.get("test").getName(), "hello world", ErrorCodeDef.FAILURE);
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
        byte[] bs = DataUtil.hexStr2Byte("02FF014600FF0000291DABAA34ECF06F2B005B4C636F6D2E6862617365736F66742"
            + "E6672616D65776F726B2E636F6D6D6F6E2E7574696C732E6265616E00829058FFA"
            + "8AFCFBC0900546573744265616E3B01000000FF000090D63AE12774FDBF2900636"
            + "F6D2E6862617365736F66742E6672616D65776F726B2E636F6D6D6F6E2E7574696"
            + "C732E6265616E002AD0CC76A6AA18500800546573744265616E7759BC931200000000000B68656C6C6F20776F726C64");
        List<TestBean> bean = SerializationUtil.unserial(List.class, bs);
        System.out.println(bean);
        Assert.equals(bean.get(0).getName(), "hello world", ErrorCodeDef.FAILURE);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void jdkUnserial() {
        byte[] bs = DataUtil.hexStr2Byte("ACED00057372002E636F6D2E6862617365736F66742E6672616D65776F726B2E636F6D6D6F6E"
            + "2E7574696C732E6265616E2E4265616E0D3AF53B8EBFF7290200024900036167654C00046E61"
            + "6D657400124C6A6176612F6C616E672F537472696E673B78700000001274000B68656C6C6F20776F726C64");
        TestBean bean = (TestBean) SerializationUtil.jdkUnserial(bs);
        Assert.equals(bean.getName(), "hello world", ErrorCodeDef.FAILURE);
    }
}
