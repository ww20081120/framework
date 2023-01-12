/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.security;

import org.junit.Test;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年9月13日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.utils.security <br>
 */
public class DataUtilTest {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void md5For16() {
        String str1 = "123456";
        String str2 = DataUtil.md5For16(str1);
        Assert.equals(str2, "49BA59ABBE56E057", ErrorCodeDef.SYSTEM_ERROR);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void md5() {
        String str1 = "123456";
        String str2 = DataUtil.md5(str1);
        Assert.equals(str2, "E10ADC3949BA59ABBE56E057F20F883E", ErrorCodeDef.SYSTEM_ERROR);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void base64Decode() {
        String str1 = "MTIzNDU2";
        String str2 = new String(DataUtil.base64Decode(str1));
        Assert.equals(str2, "123456", ErrorCodeDef.SYSTEM_ERROR);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void base64Encode() {
        String str1 = "123456";
        String str2 = DataUtil.base64Encode(str1.getBytes());
        Assert.equals(str2, "MTIzNDU2", ErrorCodeDef.SYSTEM_ERROR);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void encryptPassowrd() {
        String str1 = "123456";
        String str2 = DataUtil.encryptPassowrd(str1);
        Assert.isTrue(DataUtil.matchPassword(str1, str2), ErrorCodeDef.SYSTEM_ERROR);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void encrypt() {
        String str1 = "123456";
        String str2 = DataUtil.encrypt(str1);
        System.out.println(str2);
        String str3 = DataUtil.decrypt(str2);
        Assert.equals(str1, str3, ErrorCodeDef.SYSTEM_ERROR);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void byte2HexStr() {
        String str1 = "abcdefg";
        String str2 = DataUtil.byte2HexStr(str1.getBytes());
        Assert.equals(str2, "61626364656667", ErrorCodeDef.SYSTEM_ERROR);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void hexStr2Byte() {
        String str1 = "61626364656667";
        String str2 = new String(DataUtil.hexStr2Byte(str1));
        Assert.equals(str2, "abcdefg", ErrorCodeDef.SYSTEM_ERROR);
    }
}
