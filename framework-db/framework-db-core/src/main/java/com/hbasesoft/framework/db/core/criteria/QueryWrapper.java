package com.hbasesoft.framework.db.core.criteria;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.hbasesoft.framework.common.utils.bean.BeanUtil;
import com.hbasesoft.framework.common.utils.date.DateUtil;

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
public class QueryWrapper<T> extends AbstractQueryWrapper<T> {

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

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param field
     * @return <br>
     */
    public QueryWrapper<T> avg(final String field) {
        getSelectionList().add(TempSelection.builder().field(field).operator(Operator.AVG)
            .alias(BeanUtil.camelStr2underLine(field)).build());
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param field
     * @param alias
     * @return <br>
     */
    public QueryWrapper<T> avg(final String field, final String alias) {
        getSelectionList().add(TempSelection.builder().field(field).operator(Operator.AVG)
            .alias(BeanUtil.camelStr2underLine(alias)).build());
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
    public QueryWrapper<T> between(final boolean condition, final String fieldName, final Comparable<?> lower,
        final Comparable<?> upper) {
        if (condition) {
            getTempPredicates()
                .add(TempPredicate.builder().fieldName(fieldName).operator(Operator.BETWEEN).value(new Comparable[] {
                    lower, upper
                }).build());
        }
        return this;
    }

    /**
     * Description: between lower，upper<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param condition
     * @param fieldName
     * @param dates
     * @return <br>
     */
    public QueryWrapper<T> between(final boolean condition, final String fieldName, final Date[] dates) {
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
            between(true, fieldName, before, after);
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
    public QueryWrapper<T> between(final String fieldName, final Comparable<?> lower, final Comparable<?> upper) {
        between(true, fieldName, lower, upper);
        return this;
    }

    /**
     * Description: between lower，upper<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param fieldName
     * @param dates
     * @return <br>
     */
    public QueryWrapper<T> between(final String fieldName, final Date[] dates) {
        between(true, fieldName, dates);
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param field
     * @return <br>
     */
    public QueryWrapper<T> count(final String field) {
        getSelectionList().add(TempSelection.builder().field(field).operator(Operator.COUNT)
            .alias(BeanUtil.camelStr2underLine(field)).build());
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param field
     * @param alias
     * @return <br>
     */
    public QueryWrapper<T> count(final String field, final String alias) {
        getSelectionList().add(TempSelection.builder().field(field).operator(Operator.COUNT)
            .alias(BeanUtil.camelStr2underLine(alias)).build());
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param field1
     * @param field2
     * @return <br>
     */
    public QueryWrapper<T> diff(final String field1, final String field2) {
        getSelectionList().add(TempSelection.builder().field(field1).field2(field2).operator(Operator.DIFF)
            .alias(BeanUtil.camelStr2underLine(field1)).build());
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param field1
     * @param field2
     * @param alias
     * @return <br>
     */
    public QueryWrapper<T> diff(final String field1, final String field2, final String alias) {
        getSelectionList().add(TempSelection.builder().field(field1).field2(field2).operator(Operator.DIFF)
            .alias(BeanUtil.camelStr2underLine(alias)).build());
        return this;
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
     * >=
     *
     * @param condition 是否需要使用本条件
     * @param fieldName 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> ge(final boolean condition, final String fieldName, final Number value) {
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
    public QueryWrapper<T> ge(final String fieldName, final Number value) {
        ge(true, fieldName, value);
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
    public QueryWrapper<T> greaterThan(final boolean condition, final String fieldName, final Comparable<?> value) {
        if (condition) {
            getTempPredicates()
                .add(TempPredicate.builder().fieldName(fieldName).operator(Operator.GREATER_THAN).value(value).build());
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
    public QueryWrapper<T> greaterThan(final String fieldName, final Comparable<?> value) {
        greaterThan(true, fieldName, value);
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
    public QueryWrapper<T> greaterThanOrEqualTo(final boolean condition, final String fieldName,
        final Comparable<?> value) {
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldName)
                .operator(Operator.GREATER_THAN_OR_EQUAL_TO).value(value).build());
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
    public QueryWrapper<T> greaterThanOrEqualTo(final String fieldName, final Comparable<?> value) {
        greaterThanOrEqualTo(true, fieldName, value);
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param field
     * @return <br>
     */
    public QueryWrapper<T> groupBy(final String field) {
        getGroupList().add(field);
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
    public QueryWrapper<T> gt(final boolean condition, final String fieldName, final Number value) {
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
    public QueryWrapper<T> gt(final String fieldName, final Number value) {
        gt(true, fieldName, value);
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
    public QueryWrapper<T> in(final boolean condition, final String fieldName, final Collection<?> values) {
        List<Object> valuesList = new ArrayList<>();
        values.forEach(value -> {
            valuesList.add(value);
        });
        if (condition) {
            getTempPredicates()
                .add(TempPredicate.builder().fieldName(fieldName).operator(Operator.IN).value(values).build());
        }
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
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldName).operator(Operator.IN)
                .value(Arrays.asList(values)).build());
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
    public QueryWrapper<T> in(final String fieldName, final Collection<?> values) {
        in(true, fieldName, values);
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
     * <=
     *
     * @param condition 是否需要使用本条件
     * @param fieldName 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> le(final boolean condition, final String fieldName, final Number value) {
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
    public QueryWrapper<T> le(final String fieldName, final Number value) {
        le(true, fieldName, value);
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
    public QueryWrapper<T> lessThan(final boolean condition, final String fieldName, final Comparable<?> value) {
        if (condition) {
            getTempPredicates()
                .add(TempPredicate.builder().fieldName(fieldName).operator(Operator.LESS_THAN).value(value).build());
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
    public QueryWrapper<T> lessThan(final String fieldName, final Comparable<?> value) {
        lessThan(true, fieldName, value);
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
    public QueryWrapper<T> lessThanOrEqualTo(final boolean condition, final String fieldName,
        final Comparable<?> value) {
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldName)
                .operator(Operator.LESS_THAN_OR_EQUAL_TO).value(value).build());
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
    public QueryWrapper<T> lessThanOrEqualTo(final String fieldName, final Comparable<?> value) {
        lessThanOrEqualTo(true, fieldName, value);
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
     * <
     *
     * @param condition 是否需要使用本条件
     * @param fieldName 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> lt(final boolean condition, final String fieldName, final Number value) {
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
    public QueryWrapper<T> lt(final String fieldName, final Number value) {
        lt(true, fieldName, value);
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param field
     * @return <br>
     */
    public QueryWrapper<T> max(final String field) {
        getSelectionList().add(TempSelection.builder().field(field).operator(Operator.MAX)
            .alias(BeanUtil.camelStr2underLine(field)).build());
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param field
     * @param alias
     * @return <br>
     */
    public QueryWrapper<T> max(final String field, final String alias) {
        getSelectionList().add(TempSelection.builder().field(field).operator(Operator.MAX)
            .alias(BeanUtil.camelStr2underLine(alias)).build());
        return this;
    }

    /**
     * 合并另外一个wrapper
     * 
     * @param wrapper 另外的wrapper
     * @return this
     */
    public QueryWrapper<T> merge(final QueryWrapper<T> wrapper) {
        if (!wrapper.getOrderByList().isEmpty()) {
            getOrderByList().addAll(wrapper.getOrderByList());
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
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param field
     * @return <br>
     */
    public QueryWrapper<T> min(final String field) {
        getSelectionList().add(TempSelection.builder().field(field).operator(Operator.MIN)
            .alias(BeanUtil.camelStr2underLine(field)).build());
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param field
     * @param alias
     * @return <br>
     */
    public QueryWrapper<T> min(final String field, final String alias) {
        getSelectionList().add(TempSelection.builder().field(field).operator(Operator.MIN)
            .alias(BeanUtil.camelStr2underLine(alias)).build());
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
     * notIn 集合
     *
     * @param condition 是否需要使用本条件
     * @param fieldName 字段名
     * @param values 值
     * @return this
     */
    public QueryWrapper<T> notIn(final boolean condition, final String fieldName, final Collection<?> values) {
        if (condition) {
            getTempPredicates()
                .add(TempPredicate.builder().fieldName(fieldName).operator(Operator.NOTIN).value(values).build());
        }
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
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldName).operator(Operator.NOTIN)
                .value(Arrays.asList(values)).build());
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
    public QueryWrapper<T> notIn(final String fieldName, final Collection<?> values) {
        notIn(true, fieldName, values);
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
     * like '%xx%'
     *
     * @param condition 是否需要使用本条件
     * @param fieldName 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> notLike(final boolean condition, final String fieldName, final String value) {
        if (condition) {
            getTempPredicates().add(TempPredicate.builder().fieldName(fieldName).operator(Operator.NOTLIKE)
                .value("%" + value + "%").build());
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
    public QueryWrapper<T> notLike(final String fieldName, final String value) {
        notLike(true, fieldName, value);
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
    public QueryWrapper<T> notLikeLeft(final boolean condition, final String fieldName, final String value) {
        if (condition) {
            getTempPredicates().add(
                TempPredicate.builder().fieldName(fieldName).operator(Operator.NOTLIKE).value("%" + value).build());
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
    public QueryWrapper<T> notLikeLeft(final String fieldName, final String value) {
        notLikeLeft(true, fieldName, value);
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
    public QueryWrapper<T> notLikeRight(final boolean condition, final String fieldName, final String value) {
        if (condition) {
            getTempPredicates().add(
                TempPredicate.builder().fieldName(fieldName).operator(Operator.NOTLIKE).value(value + "%").build());
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
    public QueryWrapper<T> notLikeRight(final String fieldName, final String value) {
        notLikeRight(true, fieldName, value);
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
     * 根据字段排序 正序
     *
     * @param fieldName 字段名
     * @return this
     */
    public QueryWrapper<T> orderByAsc(final String fieldName) {
        getOrderByList().add(OrderBy.builder().isDesc(false).property(fieldName).build());
        return this;
    }

    /**
     * 根据字段排序 倒序
     *
     * @param fieldName 字段名
     * @return this
     */
    public QueryWrapper<T> orderByDesc(final String fieldName) {
        getOrderByList().add(OrderBy.builder().isDesc(true).property(fieldName).build());
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param field
     * @return <br>
     */
    public QueryWrapper<T> select(final String field) {
        select(field, field);
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param field
     * @param alias
     * @return <br>
     */
    public QueryWrapper<T> select(final String field, final String alias) {
        getSelectionList().add(TempSelection.builder().field(field).operator(Operator.FIELD)
            .alias(BeanUtil.camelStr2underLine(alias)).build());
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param field
     * @return <br>
     */
    public QueryWrapper<T> sum(final String field) {
        getSelectionList().add(TempSelection.builder().field(field).operator(Operator.SUM)
            .alias(BeanUtil.camelStr2underLine(field)).build());
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param field
     * @param alias
     * @return <br>
     */
    public QueryWrapper<T> sum(final String field, final String alias) {
        getSelectionList().add(TempSelection.builder().field(field).operator(Operator.SUM)
            .alias(BeanUtil.camelStr2underLine(alias)).build());
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param field1
     * @param field2
     * @return <br>
     */
    public QueryWrapper<T> summing(final String field1, final String field2) {
        getSelectionList().add(TempSelection.builder().field(field1).field2(field2).operator(Operator.SUMMING)
            .alias(BeanUtil.camelStr2underLine(field1)).build());
        return this;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param field1
     * @param field2
     * @param alias
     * @return <br>
     */
    public QueryWrapper<T> summing(final String field1, final String field2, final String alias) {
        getSelectionList().add(TempSelection.builder().field(field1).field2(field2).operator(Operator.SUMMING)
            .alias(BeanUtil.camelStr2underLine(alias)).build());
        return this;
    }

}
