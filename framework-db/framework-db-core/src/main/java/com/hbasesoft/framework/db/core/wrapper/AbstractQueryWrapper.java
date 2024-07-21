/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core.wrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang3.StringUtils;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.db.core.BaseDao.CriterialQuerySpecification;
import com.hbasesoft.framework.db.core.DaoException;

/**
 * <Description> <br>
 * 
 * @param <T> T
 * @author ww200<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2024年6月26日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.core.wrapper <br>
 */
public abstract class AbstractQueryWrapper<T> extends AbstractWrapper<T> {

    /**
     * 分组条件
     */
    private List<String> groupBy = new ArrayList<>();

    /**
     * 排序
     */
    private List<OrderBy> orderByList = new ArrayList<>();

    /**
     * 查询类型
     */
    private List<TempSelection> selectionList = new ArrayList<>();

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public CriterialQuerySpecification<T> build() {
        return (root, query, cb) -> {

            if (this.selectionList.isEmpty()) {
                query.select(root);
            }
            else {
                query.multiselect(toSelection(root, query, cb));
            }

            if (!this.groupBy.isEmpty()) {
                query.groupBy(toGroupBy(root, query, cb));
            }

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
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    protected List<String> getGroupList() {
        return this.groupBy;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    protected List<OrderBy> getOrderByList() {
        return this.orderByList;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    protected List<TempSelection> getSelectionList() {
        return this.selectionList;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param root
     * @param query
     * @param cb
     * @return <br>
     */
    private Expression<?>[] toGroupBy(final Root<? extends Tuple> root, final CriteriaQuery<Tuple> query,
        final CriteriaBuilder cb) {
        Expression<?>[] expressions = new Expression<?>[this.groupBy.size()];
        for (int i = 0; i < this.groupBy.size(); i++) {
            expressions[i] = root.get(this.groupBy.get(i));
        }
        return expressions;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param root
     * @param query
     * @param cb
     * @return <br>
     */
    @SuppressWarnings("unchecked")
    private Selection<? extends Object>[] toSelection(final Root<? extends Tuple> root, final CriteriaQuery<?> query,
        final CriteriaBuilder cb) {

        Selection<? extends Object>[] selections = new Selection[selectionList.size()];

        // 如果没有条件和order by 直接返回
        if (Objects.equals(selections.length, 0)) {
            return null;
        }

        // 组装查询条件
        for (int i = 0; i < selectionList.size(); ++i) {
            selections[i] = toSelection(root, query, cb, selectionList.get(i));
        }
        return selections;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param root
     * @param query
     * @param cb
     * @param tempSelection
     * @return <br>
     */
    private Selection<? extends Object> toSelection(final Root<? extends Tuple> root, final CriteriaQuery<?> query,
        final CriteriaBuilder cb, final TempSelection tempSelection) {
        Selection<? extends Object> selection = null;
        switch (tempSelection.getOperator()) {
            case FIELD:
                selection = root.get(tempSelection.getField());
                break;
            case SUM:
                selection = cb.sum(root.get(tempSelection.getField()));
                break;
            case SUMMING:
                selection = cb.sum(root.get(tempSelection.getField()), root.get(tempSelection.getField2()));
                break;
            case AVG:
                selection = cb.avg(root.get(tempSelection.getField()));
                break;
            case DIFF:
                selection = cb.diff(root.get(tempSelection.getField()), root.get(tempSelection.getField2()));
                break;
            case COUNT:
                selection = cb.count(root.get(tempSelection.getField()));
                break;
            case MAX:
                selection = cb.max(root.get(tempSelection.getField()));
                break;
            case MIN:
                selection = cb.min(root.get(tempSelection.getField()));
                break;
            default:
                throw new DaoException(ErrorCodeDef.PARAM_ERROR, tempSelection.getOperator().name());
        }
        if (StringUtils.isNotEmpty(tempSelection.getAlias())) {
            selection.alias(tempSelection.getAlias());
        }
        return selection;
    }

}
