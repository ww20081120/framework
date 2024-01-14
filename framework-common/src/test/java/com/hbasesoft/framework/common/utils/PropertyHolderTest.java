/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.hbasesoft.framework.common.ErrorCodeDef;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年4月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common <br>
 */
public class PropertyHolderTest {

    /** number */
    private static final int NUM_3 = 3;

    /** number */
    private static final long NUM_3L = 3L;

    /** number */
    private static final int NUM_8888 = 8888;

    /** number */
    private static final int NUM_9998 = 9998;

    /** number */
    private static final int NUM_9999 = 9999;

    /** number */
    private static final long NUM_1000L = 1000L;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void getProperties() {
        Map<String, String> map = PropertyHolder.getProperties();
        // 这个属性定义在 application.yml里面
        Assert.isTrue(map.containsKey("test.str.str1"), ErrorCodeDef.SYSTEM_ERROR);

        // 这个定义在ext01.properties里面
        Assert.isTrue(map.containsKey("test01"), ErrorCodeDef.SYSTEM_ERROR);

        // 这个定义在ext02.yml里面
        Assert.isTrue(map.containsKey("test02"), ErrorCodeDef.SYSTEM_ERROR);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void getBooleanProperty() {
        boolean b1 = PropertyHolder.getBooleanProperty("test.bool.bool1");
        Assert.isTrue(b1, ErrorCodeDef.SYSTEM_ERROR);

        boolean b2 = PropertyHolder.getBooleanProperty("test.bool.none", false);
        Assert.isFalse(b2, ErrorCodeDef.SYSTEM_ERROR);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void getIntProperty() {
        int a = PropertyHolder.getIntProperty("test.int.int1");
        Assert.isTrue(a == 1, ErrorCodeDef.SYSTEM_ERROR);

        a = PropertyHolder.getIntProperty("test.int.none", NUM_3);
        Assert.isTrue(a == NUM_3, ErrorCodeDef.SYSTEM_ERROR);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void getLongProperty() {
        long a = PropertyHolder.getLongProperty("test.long.long1");
        Assert.isTrue(a == NUM_1000L, ErrorCodeDef.SYSTEM_ERROR);

        a = PropertyHolder.getLongProperty("test.long.none", NUM_3L);
        Assert.isTrue(a == NUM_3L, ErrorCodeDef.SYSTEM_ERROR);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void getProperty() {
        String a = PropertyHolder.getProperty("test.str.str2");
        Assert.equals(a, "bcd", ErrorCodeDef.SYSTEM_ERROR);

        a = PropertyHolder.getProperty("test.str.none", "abcdefg");
        Assert.equals(a, "abcdefg", ErrorCodeDef.SYSTEM_ERROR);

        a = PropertyHolder.getProperty("test.int.int2");
        Assert.equals(a, "-1", ErrorCodeDef.SYSTEM_ERROR);

        a = PropertyHolder.getProperty("test.long.long2");
        Assert.equals(a, "3000", ErrorCodeDef.SYSTEM_ERROR);

        a = PropertyHolder.getProperty("test.bool.bool2");
        Assert.equals(a, "false", ErrorCodeDef.SYSTEM_ERROR);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void getProjectName() {
        String projectName = PropertyHolder.getProjectName();
        Assert.equals(projectName, "demo", ErrorCodeDef.SYSTEM_ERROR);
    }
}
