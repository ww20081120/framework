package com.hbasesoft.framework.db.core.criteria;

import java.util.ArrayList;
import java.util.List;

import com.hbasesoft.framework.db.core.BaseDao.CriterialQuerySpecification;

import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;

/**
 * <Description>普通的queryWrapper 根据字段名 <br>
 * 
 * @param <T> T
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2024年5月8日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.core.wrapper <br>
 */
public class QueryWrapper<T> extends AbstractWrapper<T> {

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
                return query.where(predicates).getRestriction();
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
     * @param fieldName 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> eq(final boolean condition, final String fieldName, final Object value) {
        if (condition) {
            getTempPredicates()
                .add(TempPredicate.builder().fieldName(fieldName).operator(Operator.EQ).value(value).build());
        }
        return this;
    }

    /**
     * =
     *
     * @param fieldName 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> eq(final String fieldName, final Object value) {
        eq(true, fieldName, value);
        return this;
    }

    /**
     * !=
     *
     * @param condition 是否需要使用本条件
     * @param fieldName 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> ne(final boolean condition, final String fieldName, final Object value) {
        if (condition) {
            getTempPredicates()
                .add(TempPredicate.builder().fieldName(fieldName).operator(Operator.NE).value(value).build());
        }
        return this;
    }

    /**
     * !=
     *
     * @param fieldName 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> ne(final String fieldName, final Object value) {
        ne(true, fieldName, value);
        return this;
    }

    /**
     * >
     *
     * @param condition 是否需要使用本条件
     * @param fieldName 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> gt(final boolean condition, final String fieldName, final Comparable<?> value) {
        if (condition) {
            getTempPredicates()
                .add(TempPredicate.builder().fieldName(fieldName).operator(Operator.GT).value(value).build());
        }
        return this;
    }

    /**
     * >
     * 
     * @param fieldName 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> gt(final String fieldName, final Comparable<?> value) {
        gt(true, fieldName, value);
        return this;
    }

    /**
     * >=
     *
     * @param condition 是否需要使用本条件
     * @param fieldName 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> ge(final boolean condition, final String fieldName, final Comparable<?> value) {
        if (condition) {
            getTempPredicates()
                .add(TempPredicate.builder().fieldName(fieldName).operator(Operator.GE).value(value).build());
        }
        return this;
    }

    /**
     * >=
     *
     * @param fieldName 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> ge(final String fieldName, final Comparable<?> value) {
        ge(true, fieldName, value);
        return this;
    }

    /**
     * <
     *
     * @param condition 是否需要使用本条件
     * @param fieldName 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> lt(final boolean condition, final String fieldName, final Comparable<?> value) {
        if (condition) {
            getTempPredicates()
                .add(TempPredicate.builder().fieldName(fieldName).operator(Operator.LT).value(value).build());
        }
        return this;
    }

    /**
     * <
     *
     * @param fieldName 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> lt(final String fieldName, final Comparable<?> value) {
        lt(true, fieldName, value);
        return this;
    }

    /**
     * <=
     *
     * @param condition 是否需要使用本条件
     * @param fieldName 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> le(final boolean condition, final String fieldName, final Comparable<?> value) {
        if (condition) {
            getTempPredicates()
                .add(TempPredicate.builder().fieldName(fieldName).operator(Operator.LE).value(value).build());
        }
        return this;
    }

    /**
     * <=
     *
     * @param fieldName 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> le(final String fieldName, final Comparable<?> value) {
        le(true, fieldName, value);
        return this;
    }

    /**
     * like '%xx%'
     *
     * @param condition 是否需要使用本条件
     * @param fieldName 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> like(final boolean condition, final String fieldName, final String value) {
        if (condition) {
            getTempPredicates().add(
                TempPredicate.builder().fieldName(fieldName).operator(Operator.LIKE).value("%" + value + "%").build());
        }
        return this;
    }

    /**
     * like '%xx%'
     *
     * @param fieldName 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> like(final String fieldName, final String value) {
        like(true, fieldName, value);
        return this;
    }

    /**
     * like 'xx%'
     *
     * @param condition 是否需要使用本条件
     * @param fieldName 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> likeRight(final boolean condition, final String fieldName, final String value) {
        if (condition) {
            getTempPredicates()
                .add(TempPredicate.builder().fieldName(fieldName).operator(Operator.LIKE).value(value + "%").build());
        }
        return this;
    }

    /**
     * like 'xx%'
     *
     * @param fieldName 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> likeRight(final String fieldName, final String value) {
        likeRight(true, fieldName, value);
        return this;
    }

    /**
     * like '%xx'
     *
     * @param condition 是否需要使用本条件
     * @param fieldName 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> likeLeft(final boolean condition, final String fieldName, final String value) {
        if (condition) {
            getTempPredicates()
                .add(TempPredicate.builder().fieldName(fieldName).operator(Operator.LIKE).value("%" + value).build());
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
    public QueryWrapper<T> or(final TempQueryWrapper<T> tempQueryWrapper) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<T>();
        tempQueryWrapper.exec(queryWrapper);
        if (!queryWrapper.getTempPredicates().isEmpty()) {
            this.getOrTempPredicates().add(queryWrapper.getTempPredicates());
        }
        return this;
    }

    /**
     * like '%xx'
     *
     * @param fieldName 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> likeLeft(final String fieldName, final String value) {
        likeLeft(true, fieldName, value);
        return this;
    }

    /**
     * Description: between lower，upper <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param condition
     * @param fieldName
     * @param lower
     * @param upper
     * @return <br>
     */
    public QueryWrapper<T> between(final boolean condition, final String fieldName, final Object lower,
        final Object upper) {
        if (condition) {
            getTempPredicates()
                .add(TempPredicate.builder().fieldName(fieldName).operator(Operator.BETWEEN).value(new Object[] {
                    lower, upper
                }).build());
        }
        return this;
    }

    /**
     * between lower，upper
     *
     * @param fieldName 字段名
     * @param lower 最小值
     * @param upper 最大值
     * @return this
     */
    public QueryWrapper<T> between(final String fieldName, final Object lower, final Object upper) {
        between(true, fieldName, lower, upper);
        return this;
    }

    /**
     * in 集合
     *
     * @param condition 是否需要使用本条件
     * @param fieldName 字段名
     * @param values 值
     * @return this
     */
    public QueryWrapper<T> in(final boolean condition, final String fieldName, final Iterable<?> values) {
        List<Object> valuesList = new ArrayList<>();
        values.forEach(value -> {
            valuesList.add(value);
        });
        if (condition) {
            getTempPredicates().add(
                TempPredicate.builder().fieldName(fieldName).operator(Operator.IN).value(valuesList.toArray()).build());
        }
        return this;
    }

    /**
     * in 集合
     *
     * @param fieldName 字段名
     * @param values 值
     * @return this
     */
    public QueryWrapper<T> in(final String fieldName, final Iterable<?> values) {
        in(true, fieldName, values);
        return this;
    }

    /**
     * in 数组/可变参
     *
     * @param condition 是否需要使用本条件
     * @param fieldName 字段名
     * @param values 值
     * @return this
     */
    public QueryWrapper<T> in(final boolean condition, final String fieldName, final Object... values) {
        if (condition) {
            getTempPredicates()
                .add(TempPredicate.builder().fieldName(fieldName).operator(Operator.IN).value(values).build());
        }
        return this;
    }

    /**
     * in 数组/可变参
     *
     * @param fieldName 字段名
     * @param values 值
     * @return this
     */
    public QueryWrapper<T> in(final String fieldName, final Object... values) {
        in(true, fieldName, values);
        return this;
    }

    /**
     * notIn 数组/可变参
     *
     * @param condition 是否需要使用本条件
     * @param fieldName 字段名
     * @param values 值
     * @return this
     */
    public QueryWrapper<T> notIn(final boolean condition, final String fieldName, final Object... values) {
        if (condition) {
            getTempPredicates()
                .add(TempPredicate.builder().fieldName(fieldName).operator(Operator.NOTIN).value(values).build());
        }
        return this;
    }

    /**
     * notIn 数组/可变参
     *
     * @param fieldName 字段名
     * @param values 值
     * @return this
     */
    public QueryWrapper<T> notIn(final String fieldName, final Object... values) {
        notIn(true, fieldName, values);
        return this;
    }

    /**
     * isNull
     * 
     * @param condition 是否需要使用本条件
     * @param fieldName 字段名
     * @return this
     */
    public QueryWrapper<T> isNull(final boolean condition, final String fieldName) {
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldName).operator(Operator.ISNULL).build());
        }
        return this;
    }

    /**
     * isNull
     * 
     * @param fieldName 字段名
     * @return this
     */
    public QueryWrapper<T> isNull(final String fieldName) {
        isNull(true, fieldName);
        return this;
    }

    /**
     * isNotNull
     * 
     * @param condition 是否需要使用本条件
     * @param fieldName 字段名
     * @return this
     */
    public QueryWrapper<T> isNotNull(final boolean condition, final String fieldName) {
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldName).operator(Operator.NOTNULL).build());
        }
        return this;
    }

    /**
     * isNotNull
     * 
     * @param fieldName 字段名
     * @return this
     */
    public QueryWrapper<T> isNotNull(final String fieldName) {
        isNotNull(true, fieldName);
        return this;
    }

    /**
     * notIn 集合
     *
     * @param condition 是否需要使用本条件
     * @param fieldName 字段名
     * @param values 值
     * @return this
     */
    public QueryWrapper<T> notIn(final boolean condition, final String fieldName, final Iterable<?> values) {
        List<Object> valuesList = new ArrayList<>();
        values.forEach(value -> {
            valuesList.add(value);
        });
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldName).operator(Operator.NOTIN)
                .value(valuesList.toArray()).build());
        }
        return this;
    }

    /**
     * notIn 集合
     *
     * @param fieldName 字段名
     * @param values 值
     * @return this
     */
    public QueryWrapper<T> notIn(final String fieldName, final Iterable<?> values) {
        notIn(true, fieldName, values);
        return this;
    }

    /**
     * 根据字段排序 正序
     *
     * @param fieldName 字段名
     * @return this
     */
    public QueryWrapper<T> orderByAsc(final String fieldName) {
        orderByList.add(OrderBy.builder().isDesc(false).property(fieldName).build());
        return this;
    }

    /**
     * 根据字段排序 倒序
     *
     * @param fieldName 字段名
     * @return this
     */
    public QueryWrapper<T> orderByDesc(final String fieldName) {
        orderByList.add(OrderBy.builder().isDesc(true).property(fieldName).build());
        return this;
    }

    /**
     * 合并另外一个wrapper
     * 
     * @param wrapper 另外的wrapper
     * @return this
     */
    public QueryWrapper<T> merge(final QueryWrapper<T> wrapper) {
        if (!wrapper.orderByList.isEmpty()) {
            orderByList.addAll(wrapper.orderByList);
        }
        if (!wrapper.getOrTempPredicates().isEmpty()) {
            super.getOrTempPredicates().addAll(wrapper.getOrTempPredicates());
        }
        if (!wrapper.getTempPredicates().isEmpty()) {
            this.getTempPredicates().addAll(wrapper.getTempPredicates());
        }
        return this;
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
    public interface TempQueryWrapper<T> {

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param wrapper <br>
         */
        void exec(QueryWrapper<T> wrapper);
    }
}
