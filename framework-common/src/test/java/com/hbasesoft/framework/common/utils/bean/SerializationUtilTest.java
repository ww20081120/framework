/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.bean;

import org.junit.Test;

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

        Bean bean = new Bean("hello world", NUM_18);
        byte[] bs = SerializationUtil.serial(bean);
        String hexStr = DataUtil.byte2HexStr(bs);
        Assert.equals(hexStr, "0A0B68656C6C6F20776F726C641012", ErrorCodeDef.SYSTEM_ERROR_10001);
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
        Bean bean = new Bean("hello world", NUM_18);
        byte[] bs = SerializationUtil.jdkSerial(bean);
        String hexStr = DataUtil.byte2HexStr(bs);
        Assert.equals(hexStr,
            "ACED00057372002E636F6D2E6862617365736F66742E6672616D65776F726B2E636F6D6"
                + "D6F6E2E7574696C732E6265616E2E4265616E0D3AF53B8EBFF729020002490003616765"
                + "4C00046E616D657400124C6A6176612F6C616E672F537472696E673B78700000001274000B68656C6C6F20776F726C64",
            ErrorCodeDef.SYSTEM_ERROR_10001);

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
        Bean bean = SerializationUtil.unserial(Bean.class, bs);
        Assert.equals(bean.getName(), "hello world", ErrorCodeDef.SYSTEM_ERROR_10001);
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
        Bean bean = (Bean) SerializationUtil.jdkUnserial(bs);
        Assert.equals(bean.getName(), "hello world", ErrorCodeDef.SYSTEM_ERROR_10001);
    }
}
