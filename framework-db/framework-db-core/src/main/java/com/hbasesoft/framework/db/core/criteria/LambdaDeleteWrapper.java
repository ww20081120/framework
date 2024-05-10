/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core.criteria;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.db.core.BaseDao.CriterialDeleteSpecification;
import com.hbasesoft.framework.db.core.criteria.lambda.LambdaSett;
import com.hbasesoft.framework.db.core.criteria.lambda.SFunction;
import com.hbasesoft.framework.db.core.criteria.lambda.SerializedLambda;
import com.hbasesoft.framework.db.core.utils.LambdaUtils;

import jakarta.persistence.criteria.Predicate;

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
     * 缓存
     */
    private static Map<String, LambdaSett> lambdaSettMap = new HashMap<>();

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public CriterialDeleteSpecification<T> build() {
        return (root, query, cb) -> {
            Predicate[] predicates = toPredicate(root, query, cb);
            Assert.notEmpty(predicates, ErrorCodeDef.PARAM_NOT_NULL, "删除的条件");
            return query.where(predicates).getRestriction();
        };
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
     * >
     *
     * @param condition 是否需要使用本条件
     * @param fieldLambda lambda
     * @param value 值
     * @return this
     */
    public LambdaDeleteWrapper<T> gt(final boolean condition, final SFunction<T, ?> fieldLambda,
        final Comparable<?> value) {
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
    public LambdaDeleteWrapper<T> gt(final SFunction<T, ?> fieldLambda, final Comparable<?> value) {
        gt(true, fieldLambda, value);
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
    public LambdaDeleteWrapper<T> ge(final boolean condition, final SFunction<T, ?> fieldLambda,
        final Comparable<?> value) {
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
    public LambdaDeleteWrapper<T> ge(final SFunction<T, ?> fieldLambda, final Comparable<?> value) {
        ge(true, fieldLambda, value);
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
    public LambdaDeleteWrapper<T> lt(final boolean condition, final SFunction<T, ?> fieldLambda,
        final Comparable<?> value) {
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
    public LambdaDeleteWrapper<T> lt(final SFunction<T, ?> fieldLambda, final Comparable<?> value) {
        lt(true, fieldLambda, value);
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
    public LambdaDeleteWrapper<T> le(final boolean condition, final SFunction<T, ?> fieldLambda,
        final Comparable<?> value) {
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
    public LambdaDeleteWrapper<T> le(final SFunction<T, ?> fieldLambda, final Comparable<?> value) {
        le(true, fieldLambda, value);
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
        final Object lower, final Object upper) {
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldLambda2FieldName(fieldLambda))
                .operator(Operator.BETWEEN).value(new Object[] {
                    lower, upper
                }).build());
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
    public LambdaDeleteWrapper<T> between(final SFunction<T, ?> fieldLambda, final Object lower, final Object upper) {
        between(true, fieldLambda, lower, upper);
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
        final Iterable<?> values) {
        List<Object> valuesList = new ArrayList<>();
        values.forEach(value -> {
            valuesList.add(value);
        });
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldLambda2FieldName(fieldLambda))
                .operator(Operator.IN).value(valuesList.toArray()).build());
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
    public LambdaDeleteWrapper<T> in(final SFunction<T, ?> fieldLambda, final Iterable<?> values) {
        in(true, fieldLambda, values);
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
                .operator(Operator.IN).value(values).build());
        }
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
                .operator(Operator.NOTIN).value(values).build());
        }
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
     * notIn 集合
     *
     * @param condition 是否需要使用本条件
     * @param fieldLambda lambda
     * @param values 值
     * @return this
     */
    public LambdaDeleteWrapper<T> notIn(final boolean condition, final SFunction<T, ?> fieldLambda,
        final Iterable<?> values) {
        List<Object> valuesList = new ArrayList<>();
        values.forEach(value -> {
            valuesList.add(value);
        });
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldLambda2FieldName(fieldLambda))
                .operator(Operator.NOTIN).value(valuesList.toArray()).build());
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
    public LambdaDeleteWrapper<T> notIn(final SFunction<T, ?> fieldLambda, final Iterable<?> values) {
        notIn(true, fieldLambda, values);
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

    private String fieldLambda2FieldName(final SFunction<T, ?> fieldLambda) {
        return getLambdaSett(fieldLambda).getFiledName();
    }

    private static String resolveFieldName(final String methodName) {
        return StringUtils
            .uncapitalize(methodName.startsWith("get") ? methodName.substring("get".length()) : methodName);
    }

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

}
