/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.hibernate;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.sql.Blob;
import java.sql.Clob;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.engine.jdbc.SerializableBlobProxy;
import org.hibernate.engine.jdbc.SerializableClobProxy;
import org.hibernate.query.ResultListTransformer;
import org.hibernate.query.TupleTransformer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import com.hbasesoft.framework.common.utils.bean.BeanUtil;
import com.hbasesoft.framework.common.utils.date.DateUtil;

/**
 * <Description> <br>
 * 
 * @param <T> 类型
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月28日 <br>
 * @see com.hbasesoft.framework.dao.support.hibernate <br>
 */
public class AutoResultTransformer<T> implements TupleTransformer<T>, ResultListTransformer<T>, Serializable {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 7131196081465940115L;

    /**
     * isSimpleClass
     */
    private final boolean isSimpleClass;

    /**
     * resultClass
     */
    private final Class<T> resultClass;

    /**
     * 默认构造函数
     * 
     * @param resultClass <br>
     */
    public AutoResultTransformer(final Class<T> resultClass) {
        if (resultClass == null) {
            throw new IllegalArgumentException("resultClass cannot be null");
        }
        this.resultClass = resultClass;
        this.isSimpleClass = BeanUtil.isSimpleValueType(resultClass) || Date.class.equals(resultClass);
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
    private Object getValue(final Class<?> clazz, final Object value) {
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
            return Long.valueOf(value.toString());
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
            return DateUtil.parse(value.toString());
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
    @Override
    public List<T> transformList(final List<T> collection) {
        return collection;
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
    @SuppressWarnings("unchecked")
    public T transformTuple(final Object[] tuple, final String[] aliases) {
        if (ArrayUtils.isEmpty(tuple)) {
            return null;
        }

        if (isSimpleClass) {
            if (tuple.length > 1) {
                throw new RuntimeException("返回的列数大于一个，请指定返回值类型");
            }

            return (T) getValue(resultClass, tuple[0]);
        }

        // 如果已经被hibernate转化过了，就不用转了
        if (tuple.length == 1 && resultClass.isAssignableFrom(tuple[0].getClass())) {
            return (T) tuple[0];
        }

        T result = null;
        try {
            result = BeanUtils.instantiateClass(resultClass);

            BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(result);

            for (int i = 0; i < aliases.length; i++) {
                String alias = StringUtils.lowerCase(aliases[i]);
                if (!"ROWNUM_".equals(alias)) {
                    String property = alias.indexOf('_') == -1 ? alias : BeanUtil.toCamelCase(alias);
                    if (tuple[i] instanceof Clob) {
                        // clob转成String
                        SerializableClobProxy proxy = (SerializableClobProxy) Proxy.getInvocationHandler(tuple[i]);
                        Clob clob = proxy.getWrappedClob();
                        Reader inStreamDoc = clob.getCharacterStream();
                        try {
                            char[] tempDoc = new char[(int) clob.length()];
                            inStreamDoc.read(tempDoc);
                            tuple[i] = new String(tempDoc);
                        }
                        finally {
                            inStreamDoc.close();
                        }
                    }
                    else if (tuple[i] instanceof Blob) {
                        // blob 转化成byte[]
                        SerializableBlobProxy proxy = (SerializableBlobProxy) Proxy.getInvocationHandler(tuple[i]);
                        Blob blob = proxy.getWrappedBlob();
                        InputStream in = blob.getBinaryStream();
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        try {
                            IOUtils.copy(in, out);
                        }
                        finally {
                            in.close();
                            out.close();
                        }
                        tuple[i] = out.toByteArray();
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

}
