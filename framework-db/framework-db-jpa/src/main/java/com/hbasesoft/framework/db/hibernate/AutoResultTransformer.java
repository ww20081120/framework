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
import java.sql.Blob;
import java.sql.Clob;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
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
        // 先快速赋值所有字段为默认值,避免构造函数抛出异常时对象处于部分初始化状态
        this.resultClass = resultClass;

        // 再执行可能抛出异常的初始化逻辑
        if (this.resultClass == null) {
            throw new IllegalArgumentException("resultClass cannot be null");
        }
        this.isSimpleClass = BeanUtil.isSimpleValueType(this.resultClass) || Date.class.equals(this.resultClass);
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
                        // clob转成String (Hibernate 6 兼容)
                        Clob clob = (Clob) tuple[i];
                        Reader inStreamDoc = clob.getCharacterStream();
                        try {
                            char[] tempDoc = new char[(int) clob.length()];
                            int readCount = inStreamDoc.read(tempDoc);
                            // 检查读取的字符数，确保数据完整
                            if (readCount == -1) {
                                tuple[i] = "";
                            }
                            else if (readCount < tempDoc.length) {
                                tuple[i] = new String(tempDoc, 0, readCount);
                            }
                            else {
                                tuple[i] = new String(tempDoc);
                            }
                        }
                        finally {
                            inStreamDoc.close();
                        }
                    }
                    else if (tuple[i] instanceof Blob) {
                        // blob 转化成byte[] (Hibernate 6 兼容)
                        Blob blob = (Blob) tuple[i];
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
