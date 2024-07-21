/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core.wrapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.hbasesoft.framework.common.utils.date.DateUtil;
import com.hbasesoft.framework.db.core.utils.LambdaUtils;
import com.hbasesoft.framework.db.core.wrapper.lambda.LambdaSett;
import com.hbasesoft.framework.db.core.wrapper.lambda.SFunction;
import com.hbasesoft.framework.db.core.wrapper.lambda.SerializedLambda;

/**
 * <Description> <br>
 * 
 * @param <M> 新类型
 * @param <T> 数据库对应的entity
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2024年5月8日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.core.wrapper <br>
 */
public class LambdaQueryWrapper<T, M> extends AbstractQueryWrapper<T> {

    /**
     * <Description> 用于or的情况，比如 订单号或者名称包含某个 <br>
     * 
     * @param <T> 入参类型
     * @param <M> 返回类型
     * @author 王伟<br>
     * @version 1.0<br>
     * @taskId <br>
     * @CreateDate 2024年5月8日 <br>
     * @since V1.0<br>
     * @see com.hbasesoft.framework.db.core.wrapper <br>
     */
    @FunctionalInterface
    public interface TempLambdaQueryWrapper<T, M> {

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param wrapper <br>
         */
        void exec(LambdaQueryWrapper<T, M> wrapper);
    }

    /**
     * 缓存
     */
    private static Map<String, LambdaSett> lambdaSettMap = new HashMap<>();

    /**
     * 缓存
     */
    private static Map<String, LambdaSett> resultLambdaSettMap = new HashMap<>();

    private static String resolveFieldName(final String methodName) {
        return StringUtils
            .uncapitalize(methodName.startsWith("get") ? methodName.substring("get".length()) : methodName);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param fieldLambda
     * @return <br>
     */
    public LambdaQueryWrapper<T, M> avg(final SFunction<T, ?> fieldLambda) {
        String field = fieldLambda2FieldName(fieldLambda);
        getSelectionList().add(TempSelection.builder().field(field).operator(Operator.AVG).alias(field).build());
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param fieldLambda
     * @param alias
     * @return <br>
     */
    public LambdaQueryWrapper<T, M> avg(final SFunction<T, ?> fieldLambda, final SFunction<M, ?> alias) {
        getSelectionList().add(TempSelection.builder().field(fieldLambda2FieldName(fieldLambda)).operator(Operator.AVG)
            .alias(fieldLambda2Result(alias)).build());
        return this;
    }

    /**
     * Description: between lower，upper <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param condition
     * @param fieldLambda
     * @param lower
     * @param upper
     * @return <br>
     */
    public LambdaQueryWrapper<T, M> between(final boolean condition, final SFunction<T, ?> fieldLambda,
        final Comparable<?> lower, final Comparable<?> upper) {
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldLambda2FieldName(fieldLambda))
                .operator(Operator.BETWEEN).value(new Comparable[] {
                    lower, upper
                }).build());
        }
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param condition
     * @param fieldLambda
     * @param dates
     * @return <br>
     */
    public LambdaQueryWrapper<T, M> between(final boolean condition, final SFunction<T, ?> fieldLambda,
        final Date[] dates) {
        if (condition) {
            Date before = null;
            Date after = null;
            if (dates != null && dates.length > 0) {
                before = dates[0];
                if (dates.length > 1) {
                    after = dates[1];
                    if (after != null && "00:00:00".equals(DateUtil.format(after, "HH:mm:ss"))) {
                        after = DateUtil.midnight(after);
                    }
                }
            }
            between(true, fieldLambda, before, after);
        }
        return this;
    }

    /**
     * Description: between lower，upper <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param fieldLambda
     * @param lower
     * @param upper
     * @return <br>
     */
    public LambdaQueryWrapper<T, M> between(final SFunction<T, ?> fieldLambda, final Comparable<?> lower,
        final Comparable<?> upper) {
        between(true, fieldLambda, lower, upper);
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param fieldLambda
     * @param lower
     * @param dates
     * @return <br>
     */
    public LambdaQueryWrapper<T, M> between(final SFunction<T, ?> fieldLambda, final Comparable<?> lower,
        final Date[] dates) {
        between(true, fieldLambda, dates);
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param fieldLambda
     * @return <br>
     */
    public LambdaQueryWrapper<T, M> count(final SFunction<T, ?> fieldLambda) {
        String field = fieldLambda2FieldName(fieldLambda);
        getSelectionList().add(TempSelection.builder().field(field).operator(Operator.COUNT).alias(field).build());
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param fieldLambda
     * @param alias
     * @return <br>
     */
    public LambdaQueryWrapper<T, M> count(final SFunction<T, ?> fieldLambda, final SFunction<M, ?> alias) {
        getSelectionList().add(TempSelection.builder().field(fieldLambda2FieldName(fieldLambda))
            .operator(Operator.COUNT).alias(fieldLambda2Result(alias)).build());
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param fieldLambda1
     * @param fieldLambda2
     * @return <br>
     */
    public LambdaQueryWrapper<T, M> diff(final SFunction<T, ?> fieldLambda1, final SFunction<T, ?> fieldLambda2) {
        String field = fieldLambda2FieldName(fieldLambda1);
        getSelectionList().add(TempSelection.builder().field(field).field2(fieldLambda2FieldName(fieldLambda2))
            .operator(Operator.DIFF).alias(field).build());
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param fieldLambda1
     * @param fieldLambda2
     * @param alias
     * @return <br>
     */
    public LambdaQueryWrapper<T, M> diff(final SFunction<T, ?> fieldLambda1, final SFunction<T, ?> fieldLambda2,
        final SFunction<M, ?> alias) {
        getSelectionList().add(TempSelection.builder().field(fieldLambda2FieldName(fieldLambda1))
            .field2(fieldLambda2FieldName(fieldLambda2)).operator(Operator.DIFF).alias(fieldLambda2Result(alias))
            .build());
        return this;
    }

    /**
     * =
     *
     * @param condition 是否需要使用本条件
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> eq(final boolean condition, final SFunction<T, ?> fieldLambda, final Object value) {
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldLambda2FieldName(fieldLambda))
                .operator(Operator.EQ).value(value).build());
        }
        return this;
    }

    /**
     * =
     *
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> eq(final SFunction<T, ?> fieldLambda, final Object value) {
        eq(true, fieldLambda, value);
        return this;
    }

    private String fieldLambda2FieldName(final SFunction<T, ?> fieldLambda) {
        return getLambdaSett(fieldLambda).getFiledName();
    }

    private String fieldLambda2Result(final SFunction<M, ?> fieldLambda) {
        return getLambdaSett4Result(fieldLambda).getFiledName();
    }

    /**
     * >=
     *
     * @param condition 是否需要使用本条件
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> ge(final boolean condition, final SFunction<T, ?> fieldLambda, final Number value) {
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldLambda2FieldName(fieldLambda))
                .operator(Operator.GE).value(value).build());
        }
        return this;
    }

    /**
     * >=
     *
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> ge(final SFunction<T, ?> fieldLambda, final Number value) {
        ge(true, fieldLambda, value);
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param fieldLambda
     * @return <br>
     */
    protected LambdaSett getLambdaSett(final SFunction<T, ?> fieldLambda) {
        SerializedLambda lambda = LambdaUtils.resolve(fieldLambda);
        String filedName = resolveFieldName(lambda.getImplMethodName());
        String key = lambda.getImplClass().getName() + filedName;
        LambdaSett lambdaSett;
        if (lambdaSettMap.containsKey(key)) {
            lambdaSett = lambdaSettMap.get(key);
        }
        else {
            lambdaSett = new LambdaSett(lambda, filedName);
            lambdaSettMap.put(key, lambdaSett);
        }
        return lambdaSett;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param fieldLambda
     * @return <br>
     */
    protected LambdaSett getLambdaSett4Result(final SFunction<M, ?> fieldLambda) {
        SerializedLambda lambda = LambdaUtils.resolve(fieldLambda);
        String filedName = resolveFieldName(lambda.getImplMethodName());
        String key = lambda.getImplClass().getName() + filedName;
        LambdaSett lambdaSett;
        if (resultLambdaSettMap.containsKey(key)) {
            lambdaSett = resultLambdaSettMap.get(key);
        }
        else {
            lambdaSett = new LambdaSett(lambda, filedName);
            resultLambdaSettMap.put(key, lambdaSett);
        }
        return lambdaSett;
    }

    /**
     * >
     *
     * @param condition 是否需要使用本条件
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> greaterThan(final boolean condition, final SFunction<T, ?> fieldLambda,
        final Comparable<?> value) {
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldLambda2FieldName(fieldLambda))
                .operator(Operator.GREATER_THAN).value(value).build());
        }
        return this;
    }

    /**
     * >
     *
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> greaterThan(final SFunction<T, ?> fieldLambda, final Comparable<?> value) {
        greaterThan(true, fieldLambda, value);
        return this;
    }

    /**
     * >=
     *
     * @param condition 是否需要使用本条件
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> greaterThanOrEqualTo(final boolean condition, final SFunction<T, ?> fieldLambda,
        final Comparable<?> value) {
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldLambda2FieldName(fieldLambda))
                .operator(Operator.GREATER_THAN_OR_EQUAL_TO).value(value).build());
        }
        return this;
    }

    /**
     * >=
     *
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> greaterThanOrEqualTo(final SFunction<T, ?> fieldLambda, final Comparable<?> value) {
        greaterThanOrEqualTo(true, fieldLambda, value);
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param fieldLambda
     * @return <br>
     */
    public LambdaQueryWrapper<T, M> groupBy(final SFunction<T, ?> fieldLambda) {
        getGroupList().add(fieldLambda2FieldName(fieldLambda));
        return this;
    }

    /**
     * >
     *
     * @param condition 是否需要使用本条件
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> gt(final boolean condition, final SFunction<T, ?> fieldLambda, final Number value) {
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldLambda2FieldName(fieldLambda))
                .operator(Operator.GT).value(value).build());
        }
        return this;
    }

    /**
     * >
     *
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> gt(final SFunction<T, ?> fieldLambda, final Number value) {
        gt(true, fieldLambda, value);
        return this;
    }

    /**
     * in 集合
     *
     * @param condition 是否需要使用本条件
     * @param fieldLambda lambda
     * @param values 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> in(final boolean condition, final SFunction<T, ?> fieldLambda,
        final Collection<?> values) {
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldLambda2FieldName(fieldLambda))
                .operator(Operator.IN).value(values).build());
        }
        return this;
    }

    /**
     * in 数组/可变参
     *
     * @param condition 是否需要使用本条件
     * @param fieldLambda lambda
     * @param values 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> in(final boolean condition, final SFunction<T, ?> fieldLambda,
        final Object... values) {
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldLambda2FieldName(fieldLambda))
                .operator(Operator.IN).value(Arrays.asList(values)).build());
        }
        return this;
    }

    /**
     * in 集合
     *
     * @param fieldLambda lambda
     * @param values 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> in(final SFunction<T, ?> fieldLambda, final Collection<?> values) {
        in(true, fieldLambda, values);
        return this;
    }

    /**
     * in 数组/可变参
     *
     * @param fieldLambda lambda
     * @param values 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> in(final SFunction<T, ?> fieldLambda, final Object... values) {
        in(true, fieldLambda, values);
        return this;
    }

    /**
     * isNotNull
     * 
     * @param condition 是否需要使用本条件
     * @param fieldLambda lambda
     * @return this
     */
    public LambdaQueryWrapper<T, M> isNotNull(final boolean condition, final SFunction<T, ?> fieldLambda) {
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldLambda2FieldName(fieldLambda))
                .operator(Operator.NOTNULL).build());
        }
        return this;
    }

    /**
     * isNotNull
     * 
     * @param fieldLambda lambda
     * @return this
     */
    public LambdaQueryWrapper<T, M> isNotNull(final SFunction<T, ?> fieldLambda) {
        isNotNull(true, fieldLambda);
        return this;
    }

    /**
     * isNull
     * 
     * @param condition 是否需要使用本条件
     * @param fieldLambda lambda
     * @return this
     */
    public LambdaQueryWrapper<T, M> isNull(final boolean condition, final SFunction<T, ?> fieldLambda) {
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldLambda2FieldName(fieldLambda))
                .operator(Operator.ISNULL).build());
        }
        return this;
    }

    /**
     * isNull
     * 
     * @param fieldLambda lambda
     * @return this
     */
    public LambdaQueryWrapper<T, M> isNull(final SFunction<T, ?> fieldLambda) {
        isNull(true, fieldLambda);
        return this;
    }

    /**
     * <=
     *
     * @param condition 是否需要使用本条件
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> le(final boolean condition, final SFunction<T, ?> fieldLambda, final Number value) {
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldLambda2FieldName(fieldLambda))
                .operator(Operator.LE).value(value).build());
        }
        return this;
    }

    /**
     * <=
     *
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> le(final SFunction<T, ?> fieldLambda, final Number value) {
        le(true, fieldLambda, value);
        return this;
    }

    /**
     * <
     *
     * @param condition 是否需要使用本条件
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> lessThan(final boolean condition, final SFunction<T, ?> fieldLambda,
        final Comparable<?> value) {
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldLambda2FieldName(fieldLambda))
                .operator(Operator.LESS_THAN).value(value).build());
        }
        return this;
    }

    /**
     * <
     *
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> lessThan(final SFunction<T, ?> fieldLambda, final Comparable<?> value) {
        lessThan(true, fieldLambda, value);
        return this;
    }

    /**
     * <=
     *
     * @param condition 是否需要使用本条件
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> lessThanOrEqualTo(final boolean condition, final SFunction<T, ?> fieldLambda,
        final Comparable<?> value) {
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldLambda2FieldName(fieldLambda))
                .operator(Operator.LESS_THAN_OR_EQUAL_TO).value(value).build());
        }
        return this;
    }

    /**
     * <=
     *
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> lessThanOrEqualTo(final SFunction<T, ?> fieldLambda, final Comparable<?> value) {
        lessThanOrEqualTo(true, fieldLambda, value);
        return this;
    }

    /**
     * like '%xx%'
     *
     * @param condition 是否需要使用本条件
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> like(final boolean condition, final SFunction<T, ?> fieldLambda,
        final String value) {
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldLambda2FieldName(fieldLambda))
                .operator(Operator.LIKE).value("%" + value + "%").build());
        }
        return this;
    }

    /**
     * like '%xx%'
     *
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> like(final SFunction<T, ?> fieldLambda, final String value) {
        like(true, fieldLambda, value);
        return this;
    }

    /**
     * like '%xx'
     *
     * @param condition 是否需要使用本条件
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> likeLeft(final boolean condition, final SFunction<T, ?> fieldLambda,
        final String value) {
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldLambda2FieldName(fieldLambda))
                .operator(Operator.LIKE).value("%" + value).build());
        }
        return this;
    }

    /**
     * like '%xx'
     *
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> likeLeft(final SFunction<T, ?> fieldLambda, final String value) {
        likeLeft(true, fieldLambda, value);
        return this;
    }

    /**
     * like 'xx%'
     *
     * @param condition 是否需要使用本条件
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> likeRight(final boolean condition, final SFunction<T, ?> fieldLambda,
        final String value) {
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldLambda2FieldName(fieldLambda))
                .operator(Operator.LIKE).value(value + "%").build());
        }
        return this;
    }

    /**
     * like 'xx%'
     *
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> likeRight(final SFunction<T, ?> fieldLambda, final String value) {
        likeRight(true, fieldLambda, value);
        return this;
    }

    /**
     * <
     *
     * @param condition 是否需要使用本条件
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> lt(final boolean condition, final SFunction<T, ?> fieldLambda, final Number value) {
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldLambda2FieldName(fieldLambda))
                .operator(Operator.LT).value(value).build());
        }
        return this;
    }

    /**
     * <
     *
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> lt(final SFunction<T, ?> fieldLambda, final Number value) {
        lt(true, fieldLambda, value);
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param fieldLambda
     * @return <br>
     */
    public LambdaQueryWrapper<T, M> max(final SFunction<T, ?> fieldLambda) {
        String field = fieldLambda2FieldName(fieldLambda);
        getSelectionList().add(TempSelection.builder().field(field).operator(Operator.MAX).alias(field).build());
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param fieldLambda
     * @param alias
     * @return <br>
     */
    public LambdaQueryWrapper<T, M> max(final SFunction<T, ?> fieldLambda, final SFunction<M, ?> alias) {
        getSelectionList().add(TempSelection.builder().field(fieldLambda2FieldName(fieldLambda)).operator(Operator.MAX)
            .alias(fieldLambda2Result(alias)).build());
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param fieldLambda
     * @return <br>
     */
    public LambdaQueryWrapper<T, M> min(final SFunction<T, ?> fieldLambda) {
        String field = fieldLambda2FieldName(fieldLambda);
        getSelectionList().add(TempSelection.builder().field(field).operator(Operator.MIN).alias(field).build());
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param fieldLambda
     * @param alias
     * @return <br>
     */
    public LambdaQueryWrapper<T, M> min(final SFunction<T, ?> fieldLambda, final SFunction<M, ?> alias) {
        getSelectionList().add(TempSelection.builder().field(fieldLambda2FieldName(fieldLambda)).operator(Operator.MIN)
            .alias(fieldLambda2Result(alias)).build());
        return this;
    }

    /**
     * !=
     *
     * @param condition 是否需要使用本条件
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> ne(final boolean condition, final SFunction<T, ?> fieldLambda, final Object value) {
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldLambda2FieldName(fieldLambda))
                .operator(Operator.NE).value(value).build());
        }
        return this;
    }

    /**
     * !=
     *
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> ne(final SFunction<T, ?> fieldLambda, final Object value) {
        ne(true, fieldLambda, value);
        return this;
    }

    /**
     * notIn 集合
     *
     * @param condition 是否需要使用本条件
     * @param fieldLambda lambda
     * @param values 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> notIn(final boolean condition, final SFunction<T, ?> fieldLambda,
        final Collection<?> values) {
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldLambda2FieldName(fieldLambda))
                .operator(Operator.NOTIN).value(values).build());
        }
        return this;
    }

    /**
     * notIn 数组/可变参
     *
     * @param condition 是否需要使用本条件
     * @param fieldLambda lambda
     * @param values 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> notIn(final boolean condition, final SFunction<T, ?> fieldLambda,
        final Object... values) {
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldLambda2FieldName(fieldLambda))
                .operator(Operator.NOTIN).value(Arrays.asList(values)).build());
        }
        return this;
    }

    /**
     * notIn 集合
     *
     * @param fieldLambda lambda
     * @param values 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> notIn(final SFunction<T, ?> fieldLambda, final Collection<?> values) {
        notIn(true, fieldLambda, values);
        return this;
    }

    /**
     * notIn 数组/可变参
     *
     * @param fieldLambda lambda
     * @param values 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> notIn(final SFunction<T, ?> fieldLambda, final Object... values) {
        notIn(true, fieldLambda, values);
        return this;
    }

    /**
     * like '%xx%'
     *
     * @param condition 是否需要使用本条件
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> notLike(final boolean condition, final SFunction<T, ?> fieldLambda,
        final String value) {
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldLambda2FieldName(fieldLambda))
                .operator(Operator.NOTLIKE).value("%" + value + "%").build());
        }
        return this;
    }

    /**
     * like '%xx%'
     *
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> notLike(final SFunction<T, ?> fieldLambda, final String value) {
        notLike(true, fieldLambda, value);
        return this;
    }

    /**
     * like '%xx'
     *
     * @param condition 是否需要使用本条件
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> notLikeLeft(final boolean condition, final SFunction<T, ?> fieldLambda,
        final String value) {
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldLambda2FieldName(fieldLambda))
                .operator(Operator.NOTLIKE).value("%" + value).build());
        }
        return this;
    }

    /**
     * like '%xx'
     *
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> notLikeLeft(final SFunction<T, ?> fieldLambda, final String value) {
        notLikeLeft(true, fieldLambda, value);
        return this;
    }

    /**
     * like 'xx%'
     *
     * @param condition 是否需要使用本条件
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> notLikeRight(final boolean condition, final SFunction<T, ?> fieldLambda,
        final String value) {
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldLambda2FieldName(fieldLambda))
                .operator(Operator.NOTLIKE).value(value + "%").build());
        }
        return this;
    }

    /**
     * like 'xx%'
     *
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaQueryWrapper<T, M> notLikeRight(final SFunction<T, ?> fieldLambda, final String value) {
        notLikeRight(true, fieldLambda, value);
        return this;
    }

    /**
     * Description: or <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param tempQueryWrapper
     * @return <br>
     */
    public LambdaQueryWrapper<T, M> or(final TempLambdaQueryWrapper<T, M> tempQueryWrapper) {
        LambdaQueryWrapper<T, M> lambdaQueryWrapper = new LambdaQueryWrapper<T, M>();
        tempQueryWrapper.exec(lambdaQueryWrapper);
        if (!lambdaQueryWrapper.getTempPredicates().isEmpty()) {
            super.getOrTempPredicates().add(lambdaQueryWrapper.getTempPredicates());
        }
        return this;
    }

    /**
     * Description:根据字段排序 正序 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param fieldLambda
     * @return <br>
     */
    public LambdaQueryWrapper<T, M> orderByAsc(final SFunction<T, ?> fieldLambda) {
        getOrderByList().add(OrderBy.builder().isDesc(false).property(fieldLambda2FieldName(fieldLambda)).build());
        return this;
    }

    /**
     * Description: 根据字段排序 倒序 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param fieldLambda
     * @return <br>
     */
    public LambdaQueryWrapper<T, M> orderByDesc(final SFunction<T, ?> fieldLambda) {
        getOrderByList().add(OrderBy.builder().isDesc(true).property(fieldLambda2FieldName(fieldLambda)).build());
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param fieldLambda
     * @return <br>
     */
    public LambdaQueryWrapper<T, M> select(final SFunction<T, ?> fieldLambda) {
        String field = fieldLambda2FieldName(fieldLambda);
        getSelectionList().add(TempSelection.builder().field(field).operator(Operator.FIELD).alias(field).build());
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param fieldLambda
     * @param alias
     * @return <br>
     */
    public LambdaQueryWrapper<T, M> select(final SFunction<T, ?> fieldLambda, final SFunction<M, ?> alias) {
        getSelectionList().add(TempSelection.builder().field(fieldLambda2FieldName(fieldLambda))
            .operator(Operator.FIELD).alias(fieldLambda2Result(alias)).build());
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param fieldLambda
     * @return <br>
     */
    public LambdaQueryWrapper<T, M> sum(final SFunction<T, ?> fieldLambda) {
        String field = fieldLambda2FieldName(fieldLambda);
        getSelectionList().add(TempSelection.builder().field(field).operator(Operator.SUM).alias(field).build());
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param fieldLambda
     * @param alias
     * @return <br>
     */
    public LambdaQueryWrapper<T, M> sum(final SFunction<T, ?> fieldLambda, final SFunction<M, ?> alias) {
        getSelectionList().add(TempSelection.builder().field(fieldLambda2FieldName(fieldLambda)).operator(Operator.SUM)
            .alias(fieldLambda2Result(alias)).build());
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param fieldLambda1
     * @param fieldLambda2
     * @return <br>
     */
    public LambdaQueryWrapper<T, M> summing(final SFunction<T, ?> fieldLambda1, final SFunction<T, ?> fieldLambda2) {
        String field = fieldLambda2FieldName(fieldLambda1);
        getSelectionList().add(TempSelection.builder().field(field).field2(fieldLambda2FieldName(fieldLambda2))
            .operator(Operator.SUMMING).alias(field).build());
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param fieldLambda1
     * @param fieldLambda2
     * @param alias
     * @return <br>
     */
    public LambdaQueryWrapper<T, M> summing(final SFunction<T, ?> fieldLambda1, final SFunction<T, ?> fieldLambda2,
        final SFunction<M, ?> alias) {
        getSelectionList().add(TempSelection.builder().field(fieldLambda2FieldName(fieldLambda1))
            .field2(fieldLambda2FieldName(fieldLambda2)).operator(Operator.SUMMING).alias(fieldLambda2Result(alias))
            .build());
        return this;
    }
}
