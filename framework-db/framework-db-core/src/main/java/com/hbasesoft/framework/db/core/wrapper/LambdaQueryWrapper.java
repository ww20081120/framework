/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core.wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;

import org.apache.commons.lang3.StringUtils;

import com.hbasesoft.framework.db.core.BaseDao.CriterialQuerySpecification;
import com.hbasesoft.framework.db.core.utils.LambdaUtils;
import com.hbasesoft.framework.db.core.wrapper.lambda.LambdaSett;
import com.hbasesoft.framework.db.core.wrapper.lambda.SFunction;
import com.hbasesoft.framework.db.core.wrapper.lambda.SerializedLambda;

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
public class LambdaQueryWrapper<T> extends AbstractWrapper<T> {

    /**
     * 缓存
     */
    private static Map<String, LambdaSett> lambdaSettMap = new HashMap<>();

    /**
     * 排序
     */
    private List<OrderBy> orderByList = new ArrayList<>();

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public CriterialQuerySpecification<T> build() {
        return (root, query, cb) -> {
            Predicate[] predicates = toPredicate(root, query, cb);
            if (predicates == null && orderByList.isEmpty()) {
                return null;
            }

            if (this.orderByList.isEmpty()) {
                return cb.and(predicates);
            }
            else {
                Order[] orders = new Order[this.orderByList.size()];
                for (int i = 0; i < orderByList.size(); i++) {
                    orders[i] = orderByList.get(i).isDesc() ? cb.desc(root.get(orderByList.get(i).getProperty()))
                        : cb.asc(root.get(orderByList.get(i).getProperty()));
                }
                return query.orderBy(orders).where(predicates).getRestriction();
            }
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
    public LambdaQueryWrapper<T> eq(final boolean condition, final SFunction<T, ?> fieldLambda, final Object value) {
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
    public LambdaQueryWrapper<T> eq(final SFunction<T, ?> fieldLambda, final Object value) {
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
    public LambdaQueryWrapper<T> ne(final boolean condition, final SFunction<T, ?> fieldLambda, final Object value) {
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
    public LambdaQueryWrapper<T> ne(final SFunction<T, ?> fieldLambda, final Object value) {
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
    public LambdaQueryWrapper<T> gt(final boolean condition, final SFunction<T, ?> fieldLambda,
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
    public LambdaQueryWrapper<T> gt(final SFunction<T, ?> fieldLambda, final Comparable<?> value) {
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
    public LambdaQueryWrapper<T> ge(final boolean condition, final SFunction<T, ?> fieldLambda,
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
    public LambdaQueryWrapper<T> ge(final SFunction<T, ?> fieldLambda, final Comparable<?> value) {
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
    public LambdaQueryWrapper<T> lt(final boolean condition, final SFunction<T, ?> fieldLambda,
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
    public LambdaQueryWrapper<T> lt(final SFunction<T, ?> fieldLambda, final Comparable<?> value) {
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
    public LambdaQueryWrapper<T> le(final boolean condition, final SFunction<T, ?> fieldLambda,
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
    public LambdaQueryWrapper<T> le(final SFunction<T, ?> fieldLambda, final Comparable<?> value) {
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
    public LambdaQueryWrapper<T> like(final boolean condition, final SFunction<T, ?> fieldLambda, final String value) {
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
    public LambdaQueryWrapper<T> like(final SFunction<T, ?> fieldLambda, final String value) {
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
    public LambdaQueryWrapper<T> likeRight(final boolean condition, final SFunction<T, ?> fieldLambda,
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
    public LambdaQueryWrapper<T> likeRight(final SFunction<T, ?> fieldLambda, final String value) {
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
    public LambdaQueryWrapper<T> likeLeft(final boolean condition, final SFunction<T, ?> fieldLambda,
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
    public LambdaQueryWrapper<T> or(final TempLambdaQueryWrapper<T> tempQueryWrapper) {
        LambdaQueryWrapper<T> lambdaQueryWrapper = new LambdaQueryWrapper<T>();
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
    public LambdaQueryWrapper<T> likeLeft(final SFunction<T, ?> fieldLambda, final String value) {
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
    public LambdaQueryWrapper<T> between(final boolean condition, final SFunction<T, ?> fieldLambda, final Object lower,
        final Object upper) {
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
    public LambdaQueryWrapper<T> between(final SFunction<T, ?> fieldLambda, final Object lower, final Object upper) {
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
    public LambdaQueryWrapper<T> in(final boolean condition, final SFunction<T, ?> fieldLambda,
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
    public LambdaQueryWrapper<T> in(final SFunction<T, ?> fieldLambda, final Iterable<?> values) {
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
    public LambdaQueryWrapper<T> in(final boolean condition, final SFunction<T, ?> fieldLambda,
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
    public LambdaQueryWrapper<T> in(final SFunction<T, ?> fieldLambda, final Object... values) {
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
    public LambdaQueryWrapper<T> notIn(final boolean condition, final SFunction<T, ?> fieldLambda,
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
    public LambdaQueryWrapper<T> notIn(final SFunction<T, ?> fieldLambda, final Object... values) {
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
    public LambdaQueryWrapper<T> isNull(final boolean condition, final SFunction<T, ?> fieldLambda) {
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
    public LambdaQueryWrapper<T> isNull(final SFunction<T, ?> fieldLambda) {
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
    public LambdaQueryWrapper<T> isNotNull(final boolean condition, final SFunction<T, ?> fieldLambda) {
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
    public LambdaQueryWrapper<T> isNotNull(final SFunction<T, ?> fieldLambda) {
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
    public LambdaQueryWrapper<T> notIn(final boolean condition, final SFunction<T, ?> fieldLambda,
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
    public LambdaQueryWrapper<T> notIn(final SFunction<T, ?> fieldLambda, final Iterable<?> values) {
        notIn(true, fieldLambda, values);
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
    public LambdaQueryWrapper<T> orderByAsc(final SFunction<T, ?> fieldLambda) {
        orderByList.add(OrderBy.builder().isDesc(false).property(fieldLambda2FieldName(fieldLambda)).build());
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
    public LambdaQueryWrapper<T> orderByDesc(final SFunction<T, ?> fieldLambda) {
        orderByList.add(OrderBy.builder().isDesc(true).property(fieldLambda2FieldName(fieldLambda)).build());
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
    public interface TempLambdaQueryWrapper<T> {

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param wrapper <br>
         */
        void exec(LambdaQueryWrapper<T> wrapper);
    }
}
