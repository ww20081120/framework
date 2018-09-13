/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.bean;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Set;

import org.junit.Test;

import com.hbasesoft.framework.common.Bootstrap;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.StartupListener;
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
public class BeanUtilTest {

    @Test
    public void isAbstract() {
        Method[] methods = Runnable.class.getMethods();
        Method method = methods[0];
        boolean b = BeanUtil.isAbstract(method);
        Assert.isTrue(b, ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println(method + "是抽象方法");

        methods = StartupListener.class.getMethods();
        method = methods[0];
        b = BeanUtil.isAbstract(method);
        Assert.isFalse(b, ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("接口中的default方法不是抽象方法。");
    }

    // @Test
    // public void getMethodParamNames() throws UtilException, NotFoundException {
    //
    // Method[] methods = Bootstrap.class.getDeclaredMethods();
    // Method method = methods[1];
    // String[] names = BeanUtil.getMethodParamNames(method);
    // Assert.equals(names[0], "context", ErrorCodeDef.SYSTEM_ERROR_10001);
    // System.out.println("获取到了Bootstrap after方法中的参数名称");
    // }

    @Test
    public void getMethodSignature() {
        Method[] methods = Bootstrap.class.getDeclaredMethods();
        Method method = methods[1];

        String name = BeanUtil.getMethodSignature(method);
        Assert.equals(name, "after(org.springframework.context.ApplicationContext,)", ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("获取到了Bootstrap after方法的签名");

    }

    @Test
    public void getClasses() {
        Set<Class<?>> classes = BeanUtil.getClasses("com.hbasesoft.framework.common.utils.bean");
        Assert.notEmpty(classes, ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("找到了com.hbasesoft.framework.common.utils.bean下所有的类");

    }

    @Test
    public void toUnderlineName() {
        String str1 = "toUnderlineName";
        String str2 = BeanUtil.toUnderlineName(str1);
        Assert.equals(str2, "to_underline_name", ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    @Test
    public void toCamelCase() {
        String str1 = "To_CAmEL_CaSE";
        String str2 = BeanUtil.toCamelCase(str1);
        Assert.equals(str2, "toCamelCase", ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    @Test
    public void camelStr2underLine() {
        String str1 = "camelStr2underLine";
        String str2 = BeanUtil.camelStr2underLine(str1);
        Assert.equals(str2, "camel_str2under_line", ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    @Test
    public void toCapitalizeCamelCase() {
        String str1 = "to_caPitaLize_CaMel_case";
        String str2 = BeanUtil.toCapitalizeCamelCase(str1);
        Assert.equals(str2, "ToCapitalizeCamelCase", ErrorCodeDef.SYSTEM_ERROR_10001);
    }

    @Test
    public void isSimpleValueType() {
        boolean a = BeanUtil.isSimpleValueType(Integer.class);
        Assert.isTrue(a, ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("int 是简单类型");

        boolean b = BeanUtil.isSimpleValueType(String.class);
        Assert.isTrue(b, ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("String 是简单类型");

        boolean d = BeanUtil.isSimpleValueType(Date.class);
        Assert.isTrue(d, ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("Date 是简单类型");

        boolean c = BeanUtil.isSimpleValueType(Object.class);
        Assert.isFalse(c, ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("Object 不是简单类型");

        int[] aaa = new int[0];
        boolean e = BeanUtil.isSimpleValueType(aaa.getClass());
        Assert.isFalse(e, ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("int 数组不是简单类型");
    }

    @Test
    public void isSimpleProperty() {

        int[] aaa = new int[0];
        boolean a = BeanUtil.isSimpleProperty(aaa.getClass());
        Assert.isTrue(a, ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("int 数组里面的属性是简单类型");

        Object[] bbb = new Object[0];
        boolean b = BeanUtil.isSimpleProperty(bbb.getClass());
        Assert.isFalse(b, ErrorCodeDef.SYSTEM_ERROR_10001);
        System.out.println("Object 数组里面的属性不是简单类型");

    }
}
