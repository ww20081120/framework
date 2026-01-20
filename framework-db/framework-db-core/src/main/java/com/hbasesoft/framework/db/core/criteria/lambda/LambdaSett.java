package com.hbasesoft.framework.db.core.criteria.lambda;

import java.lang.reflect.Field;
import java.util.Date;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.bean.ClassUtil;
import com.hbasesoft.framework.db.core.DaoException;

import lombok.Data;

/**
 * 字段配置
 *
 * @author wanglei
 * @date 2020 -02-06 11:06:32
 */
@Data
public class LambdaSett {

    /**
     * 字符串
     */
    public static final int STR = 0;

    /**
     * 日期
     */
    public static final int DATE = 1;

    /**
     * 数字
     */
    public static final int NUMBER = 2;

    /**
     * 字段
     */
    private Field field;

    /**
     * 字段类型
     */
    private int fieldType;

    /**
     * 字段名
     */
    private String filedName;

    /** implClass */
    private Class<?> implClass;

    /**
     * @param lambda
     * @param filedName
     */
    public LambdaSett(final SerializedLambda lambda, final String filedName) {
        // 先快速赋值所有字段为默认值,避免构造函数抛出异常时对象处于部分初始化状态
        this.implClass = lambda.getImplClass();
        this.field = null;
        this.filedName = filedName;
        this.fieldType = STR;

        // 再执行可能抛出异常的初始化逻辑
        try {
            this.field = ClassUtil.getDeclaredField(this.implClass, filedName);
            Class<?> type = this.field.getType();

            if (type == String.class) {
                this.fieldType = STR;
            }
            else if (type == Number.class || Number.class.isAssignableFrom(type)) {
                this.fieldType = NUMBER;
            }
            else if (type == Date.class || Date.class.isAssignableFrom(type)) {
                this.fieldType = DATE;
            }
            else {
                throw new DaoException(ErrorCodeDef.PARAM_ERROR,
                    this.implClass.getName() + "." + filedName
                    + " 类型错误， Wrapper<T> 只能用于 number string date, 注意：数字必须要用包装类型 ");
            }
        } catch (Exception e) {
            // 如果初始化失败，重置字段为安全默认值
            this.field = null;
            this.filedName = null;
            this.fieldType = STR;
            throw e;
        }
    }

}
