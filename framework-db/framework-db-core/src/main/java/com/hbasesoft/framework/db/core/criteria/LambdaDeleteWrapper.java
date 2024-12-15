/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core.criteria;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.hbasesoft.framework.common.utils.date.DateUtil;
import com.hbasesoft.framework.db.core.criteria.lambda.LambdaSett;
import com.hbasesoft.framework.db.core.criteria.lambda.SFunction;
import com.hbasesoft.framework.db.core.criteria.lambda.SerializedLambda;
import com.hbasesoft.framework.db.core.utils.LambdaUtils;

/**
 * <Description> <br>
 * 
 * @param <T> T
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2024年5月8日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.core.wrapper <br>
 */
public class LambdaDeleteWrapper<T> extends AbstractWrapper<T> {

    /**
     * <Description> 用于or的情况，比如 订单号或者名称包含某个 <br>
     * 
     * @param <T> T
     * @author 王伟<br>
     * @version 1.0<br>
     * @taskId <br>
     * @CreateDate 2024年5月8日 <br>
     * @since V1.0<br>
     * @see com.hbasesoft.framework.db.core.wrapper <br>
     */
    @FunctionalInterface
    public interface TempLambdaDeleteWrapper<T> {

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param wrapper <br>
         */
        void exec(LambdaDeleteWrapper<T> wrapper);
    }

    /**
     * 缓存
     */
    private static Map<String, LambdaSett> lambdaSettMap = new HashMap<>();

    private static String resolveFieldName(final String methodName) {
        return StringUtils
            .uncapitalize(methodName.startsWith("get") ? methodName.substring("get".length()) : methodName);
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
    public LambdaDeleteWrapper<T> between(final boolean condition, final SFunction<T, ?> fieldLambda,
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
    public LambdaDeleteWrapper<T> between(final boolean condition, final SFunction<T, ?> fieldLambda,
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
    public LambdaDeleteWrapper<T> between(final SFunction<T, ?> fieldLambda, final Comparable<?> lower,
        final Comparable<?> upper) {
        between(true, fieldLambda, lower, upper);
        return this;
    }

    /**
     * Description: between lower，upper <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param fieldLambda
     * @param lower
     * @param dates
     * @return <br>
     */
    public LambdaDeleteWrapper<T> between(final SFunction<T, ?> fieldLambda, final Comparable<?> lower,
        final Date[] dates) {
        between(true, fieldLambda, dates);
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
    public LambdaDeleteWrapper<T> eq(final boolean condition, final SFunction<T, ?> fieldLambda, final Object value) {
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
    public LambdaDeleteWrapper<T> eq(final SFunction<T, ?> fieldLambda, final Object value) {
        eq(true, fieldLambda, value);
        return this;
    }

    private String fieldLambda2FieldName(final SFunction<T, ?> fieldLambda) {
        return getLambdaSett(fieldLambda).getFiledName();
    }

    /**
     * >=
     *
     * @param condition 是否需要使用本条件
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaDeleteWrapper<T> ge(final boolean condition, final SFunction<T, ?> fieldLambda, final Number value) {
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
    public LambdaDeleteWrapper<T> ge(final SFunction<T, ?> fieldLambda, final Number value) {
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
     * >
     *
     * @param condition 是否需要使用本条件
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaDeleteWrapper<T> greaterThan(final boolean condition, final SFunction<T, ?> fieldLambda,
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
    public LambdaDeleteWrapper<T> greaterThan(final SFunction<T, ?> fieldLambda, final Comparable<?> value) {
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
    public LambdaDeleteWrapper<T> greaterThanOrEqualTo(final boolean condition, final SFunction<T, ?> fieldLambda,
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
    public LambdaDeleteWrapper<T> greaterThanOrEqualTo(final SFunction<T, ?> fieldLambda, final Comparable<?> value) {
        greaterThanOrEqualTo(true, fieldLambda, value);
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
    public LambdaDeleteWrapper<T> gt(final boolean condition, final SFunction<T, ?> fieldLambda, final Number value) {
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
    public LambdaDeleteWrapper<T> gt(final SFunction<T, ?> fieldLambda, final Number value) {
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
    public LambdaDeleteWrapper<T> in(final boolean condition, final SFunction<T, ?> fieldLambda,
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
    public LambdaDeleteWrapper<T> in(final boolean condition, final SFunction<T, ?> fieldLambda,
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
    public LambdaDeleteWrapper<T> in(final SFunction<T, ?> fieldLambda, final Collection<?> values) {
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
    public LambdaDeleteWrapper<T> in(final SFunction<T, ?> fieldLambda, final Object... values) {
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
    public LambdaDeleteWrapper<T> isNotNull(final boolean condition, final SFunction<T, ?> fieldLambda) {
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
    public LambdaDeleteWrapper<T> isNotNull(final SFunction<T, ?> fieldLambda) {
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
    public LambdaDeleteWrapper<T> isNull(final boolean condition, final SFunction<T, ?> fieldLambda) {
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
    public LambdaDeleteWrapper<T> isNull(final SFunction<T, ?> fieldLambda) {
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
    public LambdaDeleteWrapper<T> le(final boolean condition, final SFunction<T, ?> fieldLambda, final Number value) {
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
    public LambdaDeleteWrapper<T> le(final SFunction<T, ?> fieldLambda, final Number value) {
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
    public LambdaDeleteWrapper<T> lessThan(final boolean condition, final SFunction<T, ?> fieldLambda,
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
    public LambdaDeleteWrapper<T> lessThan(final SFunction<T, ?> fieldLambda, final Comparable<?> value) {
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
    public LambdaDeleteWrapper<T> lessThanOrEqualTo(final boolean condition, final SFunction<T, ?> fieldLambda,
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
    public LambdaDeleteWrapper<T> lessThanOrEqualTo(final SFunction<T, ?> fieldLambda, final Comparable<?> value) {
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
    public LambdaDeleteWrapper<T> like(final boolean condition, final SFunction<T, ?> fieldLambda, final String value) {
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
    public LambdaDeleteWrapper<T> like(final SFunction<T, ?> fieldLambda, final String value) {
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
    public LambdaDeleteWrapper<T> likeLeft(final boolean condition, final SFunction<T, ?> fieldLambda,
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
    public LambdaDeleteWrapper<T> likeLeft(final SFunction<T, ?> fieldLambda, final String value) {
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
    public LambdaDeleteWrapper<T> likeRight(final boolean condition, final SFunction<T, ?> fieldLambda,
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
    public LambdaDeleteWrapper<T> likeRight(final SFunction<T, ?> fieldLambda, final String value) {
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
    public LambdaDeleteWrapper<T> lt(final boolean condition, final SFunction<T, ?> fieldLambda, final Number value) {
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
    public LambdaDeleteWrapper<T> lt(final SFunction<T, ?> fieldLambda, final Number value) {
        lt(true, fieldLambda, value);
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
    public LambdaDeleteWrapper<T> ne(final boolean condition, final SFunction<T, ?> fieldLambda, final Object value) {
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
    public LambdaDeleteWrapper<T> ne(final SFunction<T, ?> fieldLambda, final Object value) {
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
    public LambdaDeleteWrapper<T> notIn(final boolean condition, final SFunction<T, ?> fieldLambda,
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
    public LambdaDeleteWrapper<T> notIn(final boolean condition, final SFunction<T, ?> fieldLambda,
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
    public LambdaDeleteWrapper<T> notIn(final SFunction<T, ?> fieldLambda, final Collection<?> values) {
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
    public LambdaDeleteWrapper<T> notIn(final SFunction<T, ?> fieldLambda, final Object... values) {
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
    public LambdaDeleteWrapper<T> notLike(final boolean condition, final SFunction<T, ?> fieldLambda,
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
    public LambdaDeleteWrapper<T> notLike(final SFunction<T, ?> fieldLambda, final String value) {
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
    public LambdaDeleteWrapper<T> notLikeLeft(final boolean condition, final SFunction<T, ?> fieldLambda,
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
    public LambdaDeleteWrapper<T> notLikeLeft(final SFunction<T, ?> fieldLambda, final String value) {
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
    public LambdaDeleteWrapper<T> notLikeRight(final boolean condition, final SFunction<T, ?> fieldLambda,
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
    public LambdaDeleteWrapper<T> notLikeRight(final SFunction<T, ?> fieldLambda, final String value) {
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
    public LambdaDeleteWrapper<T> or(final TempLambdaDeleteWrapper<T> tempQueryWrapper) {
        LambdaDeleteWrapper<T> lambdaQueryWrapper = new LambdaDeleteWrapper<T>();
        tempQueryWrapper.exec(lambdaQueryWrapper);
        if (!lambdaQueryWrapper.getTempPredicates().isEmpty()) {
            super.getOrTempPredicates().add(lambdaQueryWrapper.getTempPredicates());
        }
        return this;
    }

}
