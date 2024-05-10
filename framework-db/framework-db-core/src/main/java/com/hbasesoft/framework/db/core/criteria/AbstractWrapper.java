/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core.criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;

import jakarta.persistence.criteria.CommonAbstractCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

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
public abstract class AbstractWrapper<T> {

    /**
     * 临时过滤条件
     */
    private List<TempPredicate> tempPredicates = new ArrayList<>();

    /**
     * 临时过滤条件-复杂的or的时候用的
     */
    private List<List<TempPredicate>> orTempPredicates = new ArrayList<>();

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
    public Predicate[] toPredicate(final Root<T> root, final CommonAbstractCriteria query, final CriteriaBuilder cb) {
        Predicate[] predicates = new Predicate[tempPredicates.size() + orTempPredicates.size()];

        // 如果没有条件和order by 直接返回
        if (Objects.equals(predicates.length, 0)) {
            return null;
        }

        // 组装where条件
        int index = 0;
        for (int i = 0; i < tempPredicates.size(); ++i) {
            predicates[i] = toPredicate(root, query, cb, tempPredicates.get(i));
            index++;
        }
        // 处理or的过滤条件
        for (List<TempPredicate> orTempPredicate : orTempPredicates) {
            Predicate[] oneOr = new Predicate[orTempPredicate.size()];
            for (int i = 0; i < orTempPredicate.size(); ++i) {
                oneOr[i] = toPredicate(root, query, cb, orTempPredicate.get(i));
            }
            predicates[index] = cb.or(oneOr);
            index++;
        }
        return predicates;
    }

    /**
     * Description: TempPredicate 转换为 Predicate <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param root
     * @param query
     * @param criteriaBuilder
     * @param predicate
     * @return <br>
     */
    @SuppressWarnings({
        "rawtypes", "unchecked"
    })
    public Predicate toPredicate(final Root<T> root, final CommonAbstractCriteria query,
        final CriteriaBuilder criteriaBuilder, final TempPredicate predicate) {
        switch (predicate.getOperator()) {
            case EQ:
                return criteriaBuilder.equal(root.get(predicate.getFieldName()), predicate.getValue());
            case NE:
                return criteriaBuilder.notEqual(root.get(predicate.getFieldName()), predicate.getValue());
            case GE:
                return criteriaBuilder.ge(root.get(predicate.getFieldName()), (Number) predicate.getValue());
            case GT:
                return criteriaBuilder.gt(root.get(predicate.getFieldName()), (Number) predicate.getValue());
            case LE:
                return criteriaBuilder.le(root.get(predicate.getFieldName()), (Number) predicate.getValue());
            case LT:
                return criteriaBuilder.lt(root.get(predicate.getFieldName()), (Number) predicate.getValue());
            case IN:
                CriteriaBuilder.In in = criteriaBuilder.in(root.get(predicate.getFieldName()));
                Object[] objects = (Object[]) predicate.getValue();
                for (Object obj : objects) {
                    in.value(obj);
                }
                return criteriaBuilder.and(in);
            case NOTIN:
                return criteriaBuilder.not(root.get(predicate.getFieldName()).in(predicate.getValue()));
            case LIKE:
                String value = (String) predicate.getValue();
                Assert.notEmpty(value, ErrorCodeDef.PARAM_NOT_NULL, predicate.getFieldName());
                return criteriaBuilder.like(root.get(predicate.getFieldName()), value);
            case ISNULL:
                return criteriaBuilder.isNull(root.get(predicate.getFieldName()));
            case NOTNULL:
                return criteriaBuilder.isNotNull(root.get(predicate.getFieldName()));
            default:
                break;
        }
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    protected List<TempPredicate> getTempPredicates() {
        return this.tempPredicates;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    protected List<List<TempPredicate>> getOrTempPredicates() {
        return this.orTempPredicates;
    }
}
