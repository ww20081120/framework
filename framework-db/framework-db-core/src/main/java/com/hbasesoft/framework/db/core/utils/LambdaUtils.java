package com.hbasesoft.framework.db.core.utils;

import com.hbasesoft.framework.db.core.criteria.lambda.SFunction;
import com.hbasesoft.framework.db.core.criteria.lambda.SerializedLambda;

public class LambdaUtils {

    /**
     * 解析 lambda 表达式, 该方法只是调用了 {@link SerializedLambda#resolve(SFunction)} 中的方法，在此基础上加了缓存。 该缓存可能会在任意不定的时间被清除
     *
     * @param func 需要解析的 lambda 对象
     * @param <T> 类型，被调用的 Function 对象的目标类型
     * @return 返回解析后的结果
     * @see SerializedLambda#resolve(SFunction)
     */
    public static <T> SerializedLambda resolve(final SFunction<T, ?> func) {
        return SerializedLambda.resolve(func);
    }
}
