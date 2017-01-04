/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.hibernate;

import java.io.Reader;
import java.lang.reflect.Proxy;
import java.sql.Clob;
import java.util.Date;
import java.util.List;

import org.hibernate.engine.jdbc.SerializableClobProxy;
import org.hibernate.transform.ResultTransformer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.bean.BeanUtil;
import com.hbasesoft.framework.common.utils.date.DateUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月28日 <br>
 * @see com.hbasesoft.framework.dao.support.hibernate <br>
 */
public class AutoResultTransformer implements ResultTransformer {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 7131196081465940115L;

    /**
     * resultClass
     */
    private final Class<?> resultClass;

    /**
     * isSimpleClass
     */
    private final boolean isSimpleClass;

    /**
     * 默认构造函数
     * 
     * @param resultClass <br>
     */
    public AutoResultTransformer(Class<?> resultClass) {
        if (resultClass == null) {
            throw new IllegalArgumentException("resultClass cannot be null");
        }
        this.resultClass = resultClass;
        this.isSimpleClass = BeanUtil.isSimpleValueType(resultClass) || Date.class.equals(resultClass);
    }

    /**
     * 结果转换时，HIBERNATE调用此方法 Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param tuple <br>
     * @param aliases <br>
     * @return <br>
     */
    public Object transformTuple(Object[] tuple, String[] aliases) {
        if (CommonUtil.isEmpty(tuple)) {
            return null;
        }

        if (isSimpleClass) {
            if (tuple.length > 1) {
                throw new RuntimeException("返回的列数大于一个，请指定返回值类型");
            }

            return getValue(resultClass, tuple[0]);
        }

        Object result = null;
        try {
            result = BeanUtils.instantiate(resultClass);

            BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(result);

            for (int i = 0; i < aliases.length; i++) {
                if (!"ROWNUM_".equals(aliases[i])) {
                    String property = BeanUtil.toCamelCase(aliases[i]);
                    if (tuple[i] instanceof Clob) {
                        // clob转成String
                        SerializableClobProxy proxy = (SerializableClobProxy) Proxy.getInvocationHandler(tuple[i]);
                        Clob clob = proxy.getWrappedClob();
                        Reader inStreamDoc = clob.getCharacterStream();
                        char[] tempDoc = new char[(int) clob.length()];
                        inStreamDoc.read(tempDoc);
                        inStreamDoc.close();
                        tuple[i] = new String(tempDoc);
                    }
                    else if ((Long.class.equals(resultClass) || long.class.equals(resultClass))
                        && (tuple[i] instanceof java.sql.Timestamp || tuple[i] instanceof java.sql.Time)) {
                        tuple[i] = DateUtil.string2Date(tuple[i].toString()).getTime();
                    }
                    wrapper.setPropertyValue(property, tuple[i]);
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException("设置返回值失败", e);
        }

        return result;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param clazz <br>
     * @param value <br>
     * @return <br>
     */
    private Object getValue(Class<?> clazz, Object value) {
        if (value == null || clazz.isAssignableFrom(value.getClass())) {
            return value;
        }

        if (String.class.equals(clazz)) {
            return value.toString();
        }
        else if (Integer.class.equals(clazz) || int.class.equals(clazz)) {
            return Integer.valueOf(value.toString());
        }
        else if (Long.class.equals(clazz) || long.class.equals(clazz)) {
            if (value instanceof java.sql.Timestamp || value instanceof java.sql.Time) {
                return DateUtil.string2Date(value.toString()).getTime();
            }
            else {
                return Long.valueOf(value.toString());
            }
        }
        else if (Double.class.equals(clazz) || double.class.equals(clazz)) {
            return Double.valueOf(value.toString());
        }
        else if (Short.class.equals(clazz) || short.class.equals(clazz)) {
            return Short.valueOf(value.toString());
        }
        else if (Float.class.equals(clazz) || float.class.equals(clazz)) {
            return Float.valueOf(value.toString());
        }
        else if (Character.class.equals(clazz) || char.class.equals(clazz)) {
            return value.toString().charAt(0);
        }
        else if (Boolean.class.equals(clazz) || boolean.class.equals(clazz)) {
            return Boolean.valueOf(value.toString());
        }
        else if (Date.class.equals(clazz)) {
            return DateUtil.string2Date(value.toString());
        }
        throw new ClassCastException("不能将" + value.getClass().getName() + "类型转化为" + clazz.getName() + "类型");
    }

    /**
     * transformList
     * 
     * @param collection <br>
     * @see org.hibernate.transform.ResultTransformer#transformList(java.util.List)
     * @return <br>
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List transformList(List collection) {
        return collection;
    }

}
