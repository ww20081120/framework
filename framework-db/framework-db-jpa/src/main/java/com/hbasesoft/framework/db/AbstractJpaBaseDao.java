/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.db.core.BaseEntity;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.criteria.AbstractQueryWrapper;
import com.hbasesoft.framework.db.core.criteria.AbstractWrapper;
import com.hbasesoft.framework.db.core.criteria.DeleteWrapper;
import com.hbasesoft.framework.db.core.criteria.LambdaDeleteWrapper;
import com.hbasesoft.framework.db.core.criteria.LambdaQueryWrapper;
import com.hbasesoft.framework.db.core.criteria.LambdaUpdateWrapper;
import com.hbasesoft.framework.db.core.criteria.QueryWrapper;
import com.hbasesoft.framework.db.core.criteria.TempPredicate;
import com.hbasesoft.framework.db.core.criteria.TempSelection;
import com.hbasesoft.framework.db.core.criteria.UpdateWrapper;
import com.hbasesoft.framework.db.core.utils.PagerList;

import jakarta.persistence.Id;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CommonAbstractCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;

/**
 * <Description> <br>
 * 
 * @param <T> T
 * @author ww200<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2024年12月14日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.core <br>
 */
public abstract class AbstractJpaBaseDao<T extends BaseEntity> implements BaseJpaDao<T> {

    /** entity class */
    private Class<T> entityClazz;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param wrapper
     * @return <br>
     */
    private CriterialQuerySpecification<T> build(final AbstractQueryWrapper<T> wrapper) {
        return (root, query, cb) -> {

            if (wrapper.getSelectionList().isEmpty()) {
                query.select(root);
            }
            else {
                query.multiselect(toSelection(wrapper, root, query, cb));
            }

            if (!wrapper.getGroupList().isEmpty()) {
                query.groupBy(toGroupBy(wrapper, root, query, cb));
            }

            Predicate[] predicates = toPredicate(wrapper, root, query, cb);
            if (predicates == null && wrapper.getOrderByList().isEmpty()) {
                return null;
            }

            if (wrapper.getOrderByList().isEmpty()) {
                return query.where(predicates).getRestriction();
            }
            else {
                Order[] orders = new Order[wrapper.getOrderByList().size()];
                for (int i = 0; i < wrapper.getOrderByList().size(); i++) {
                    orders[i] = wrapper.getOrderByList().get(i).isDesc()
                        ? cb.desc(root.get(wrapper.getOrderByList().get(i).getProperty()))
                        : cb.asc(root.get(wrapper.getOrderByList().get(i).getProperty()));
                }

                if (ArrayUtils.isNotEmpty(predicates)) {
                    query.where(predicates);
                }
                return query.orderBy(orders).getRestriction();
            }

        };
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param wrapper
     * @return <br>
     */
    private CriterialDeleteSpecification<T> build(final DeleteWrapper<T> wrapper) {
        return (root, query, cb) -> {
            Predicate[] predicates = toPredicate(wrapper, root, query, cb);
            Assert.notEmpty(predicates, ErrorCodeDef.PARAM_NOT_NULL, "删除的条件");
            return query.where(predicates).getRestriction();
        };
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param wrapper
     * @return <br>
     */
    private CriterialDeleteSpecification<T> build(final LambdaDeleteWrapper<T> wrapper) {
        return (root, query, cb) -> {
            Predicate[] predicates = toPredicate(wrapper, root, query, cb);
            Assert.notEmpty(predicates, ErrorCodeDef.PARAM_NOT_NULL, "删除的条件");
            return query.where(predicates).getRestriction();
        };
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param wrapper
     * @return <br>
     */
    private CriterialUpdateSpecification<T> build(final LambdaUpdateWrapper<T> wrapper) {
        return (root, query, cb) -> {
            Assert.notEmpty(wrapper.getValueMap(), ErrorCodeDef.PARAM_NOT_NULL, "修改的内容");
            for (Entry<String, Object> entry : wrapper.getValueMap().entrySet()) {
                query.set(root.get(entry.getKey()), entry.getValue());
            }

            Predicate[] predicates = toPredicate(wrapper, root, query, cb);
            Assert.notEmpty(predicates, ErrorCodeDef.PARAM_NOT_NULL, "修改的条件");
            return query.where(predicates).getRestriction();
        };
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param wrapper
     * @return <br>
     */
    private CriterialUpdateSpecification<T> build(final UpdateWrapper<T> wrapper) {
        return (root, query, cb) -> {
            Assert.notEmpty(wrapper.getValueMap(), ErrorCodeDef.PARAM_NOT_NULL, "修改的内容");
            for (Entry<String, Object> entry : wrapper.getValueMap().entrySet()) {
                query.set(root.get(entry.getKey()), entry.getValue());
            }

            Predicate[] predicates = toPredicate(wrapper, root, query, cb);
            Assert.notEmpty(predicates, ErrorCodeDef.PARAM_NOT_NULL, "修改的条件");
            return query.where(predicates).getRestriction();
        };
    }

    /**
     * Description: 根据条件删除<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification <br>
     */
    public void delete(final DeleteSpecification<T> specification) {
        deleteBySpecification(build(specification.toSpecification(new DeleteWrapper<>())));
    }

    /**
     * Description: 根据条件删除<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification <br>
     */
    public void deleteByLambda(final LambdaDeleteSpecification<T> specification) {
        deleteBySpecification(build(specification.toSpecification(new LambdaDeleteWrapper<>())));
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param wrapper <br>
     */
    @Override
    public void deleteByLambda(final LambdaDeleteWrapper<T> wrapper) {
        deleteBySpecification(build(wrapper));
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param specification <br>
     */
    @SuppressWarnings({
        "rawtypes", "unchecked"
    })
    @Override
    public void deleteBySpecification(final CriterialDeleteSpecification<T> specification) {
        CriteriaBuilder cb = criteriaBuilder();
        CriteriaDelete<T> query = cb.createCriteriaDelete(getEntityClazz());
        Root root = query.from(getEntityClazz());
        Assert.notNull(specification, ErrorCodeDef.PARAM_NOT_NULL, "删除条件");
        specification.toPredicate(root, query, cb);
        deleteByCriteria(query);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param clazz
     * @return <br>
     */
    protected Field findPrimaryKeyField(final Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true); // 解除私有访问限制
                return field;
            }
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && !superClass.equals(Object.class)) {
            return findPrimaryKeyField(superClass);
        }
        return null;
    }

    /**
     * Description: 根据条件查询 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification
     * @return <br>
     */
    public T get(final QuerySpecification<T> specification) {
        return getBySpecification(build(specification.toSpecification(new QueryWrapper<>())));
    }

    /**
     * Description: 根据条件查询<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <M>
     * @param specification
     * @param clazz
     * @return <br>
     */
    public <M> M get(final QuerySpecification<T> specification, final Class<M> clazz) {
        return getBySpecification(build(specification.toSpecification(new QueryWrapper<>())), clazz);
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param wrapper
     * @return <br>
     */
    @Override
    public T get(final QueryWrapper<T> wrapper) {
        return getBySpecification(build(wrapper));
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param <M>
     * @param wrapper
     * @param clazz
     * @return <br>
     */
    @Override
    public <M> M get(final QueryWrapper<T> wrapper, final Class<M> clazz) {
        return getBySpecification(build(wrapper), clazz);
    }

    /**
     * Description: 根据条件查询 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification
     * @param clazz
     * @param <M>
     * @return <br>
     */
    public <M> M getByLambda(final LambdaQuerySpecification<T, M> specification, final Class<M> clazz) {
        return getBySpecification(build(specification.toSpecification(new LambdaQueryWrapper<>())), clazz);
    }

    /**
     * Description: 根据条件查询 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification
     * @return <br>
     */
    public T getByLambda(final LambdaQuerySpecification<T, T> specification) {
        return getBySpecification(build(specification.toSpecification(new LambdaQueryWrapper<>())));
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param <M>
     * @param wrapper
     * @param clazz
     * @return <br>
     */
    @Override
    public <M> M getByLambda(final LambdaQueryWrapper<T, M> wrapper, final Class<M> clazz) {
        return getBySpecification(build(wrapper), clazz);
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param wrapper
     * @return <br>
     */
    @Override
    public T getByLambda(final LambdaQueryWrapper<T, T> wrapper) {
        return getBySpecification(build(wrapper));
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param specification
     * @return <br>
     */
    @Override
    public T getBySpecification(final CriterialQuerySpecification<T> specification) {
        return getBySpecification(specification, getEntityClazz());
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    protected Class<T> getEntityClazz() {
        Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
        return entityClazz;
    }

    /**
     * Description: 根据条件查询 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification
     * @return <br>
     */
    public List<T> query(final QuerySpecification<T> specification) {
        return queryBySpecification(build(specification.toSpecification(new QueryWrapper<>())));
    }

    /**
     * Description:根据条件查询 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <M>
     * @param specification
     * @param clazz
     * @return <br>
     */
    public <M> List<M> query(final QuerySpecification<T> specification, final Class<M> clazz) {
        return queryBySpecification(build(specification.toSpecification(new QueryWrapper<>())), clazz);
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param wrapper
     * @return <br>
     */
    @Override
    public List<T> query(final QueryWrapper<T> wrapper) {
        return queryBySpecification(build(wrapper));
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param <M>
     * @param wrapper
     * @param clazz
     * @return <br>
     */
    @Override
    public <M> List<M> query(final QueryWrapper<T> wrapper, final Class<M> clazz) {
        return queryBySpecification(build(wrapper), clazz);
    }

    /**
     * Description: 根据条件查询 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification
     * @param clazz
     * @param <M>
     * @return <br>
     */
    public <M> List<M> queryByLambda(final LambdaQuerySpecification<T, M> specification, final Class<M> clazz) {
        return queryBySpecification(build(specification.toSpecification(new LambdaQueryWrapper<>())), clazz);
    }

    /**
     * Description: 根据条件查询 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification
     * @return <br>
     */
    public List<T> queryByLambda(final LambdaQuerySpecification<T, T> specification) {
        return queryBySpecification(build(specification.toSpecification(new LambdaQueryWrapper<>())));
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param <M>
     * @param wrapper
     * @param clazz
     * @return <br>
     */
    @Override
    public <M> List<M> queryByLambda(final LambdaQueryWrapper<T, M> wrapper, final Class<M> clazz) {
        return queryBySpecification(build(wrapper), clazz);
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param wrapper
     * @return <br>
     */
    @Override
    public List<T> queryByLambda(final LambdaQueryWrapper<T, T> wrapper) {
        return queryBySpecification(build(wrapper));
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param specification
     * @return <br>
     */
    @Override
    public List<T> queryBySpecification(final CriterialQuerySpecification<T> specification) {
        return queryBySpecification(specification, getEntityClazz());
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification
     * @param pageIndex
     * @param pageSize
     * @return <br>
     */
    public PagerList<T> queryPager(final QuerySpecification<T> specification, final int pageIndex, final int pageSize) {
        return queryPagerBySpecification(build(specification.toSpecification(new QueryWrapper<>())), pageIndex,
            pageSize);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification
     * @param pageIndex
     * @param pageSize
     * @param <M> M
     * @param clazz
     * @return <br>
     */
    public <M> PagerList<M> queryPager(final QuerySpecification<T> specification, final int pageIndex,
        final int pageSize, final Class<M> clazz) {
        return queryPagerBySpecification(build(specification.toSpecification(new QueryWrapper<>())), pageIndex,
            pageSize, clazz);
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param wrapper
     * @param pageIndex
     * @param pageSize
     * @return <br>
     */
    @Override
    public PagerList<T> queryPager(final QueryWrapper<T> wrapper, final int pageIndex, final int pageSize) {
        return queryPagerBySpecification(build(wrapper), pageIndex, pageSize);
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param <M>
     * @param wrapper
     * @param pageIndex
     * @param pageSize
     * @param clazz
     * @return <br>
     */
    @Override
    public <M> PagerList<M> queryPager(final QueryWrapper<T> wrapper, final int pageIndex, final int pageSize,
        final Class<M> clazz) {
        return queryPagerBySpecification(build(wrapper), pageIndex, pageSize, clazz);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification
     * @param pageIndex
     * @param pageSize
     * @param <M> M
     * @param clazz
     * @return <br>
     */
    public <M> PagerList<M> queryPagerByLambda(final LambdaQuerySpecification<T, M> specification, final int pageIndex,
        final int pageSize, final Class<M> clazz) {
        return queryPagerBySpecification(build(specification.toSpecification(new LambdaQueryWrapper<>())), pageIndex,
            pageSize, clazz);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification
     * @param pageIndex
     * @param pageSize
     * @return <br>
     */
    public PagerList<T> queryPagerByLambda(final LambdaQuerySpecification<T, T> specification, final int pageIndex,
        final int pageSize) {
        return queryPagerBySpecification(build(specification.toSpecification(new LambdaQueryWrapper<>())), pageIndex,
            pageSize);
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param <M>
     * @param wrapper
     * @param pageIndex
     * @param pageSize
     * @param clazz
     * @return <br>
     */
    @Override
    public <M> PagerList<M> queryPagerByLambda(final LambdaQueryWrapper<T, M> wrapper, final int pageIndex,
        final int pageSize, final Class<M> clazz) {
        return queryPagerBySpecification(build(wrapper), pageIndex, pageSize, clazz);

    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param wrapper
     * @param pageIndex
     * @param pageSize
     * @return <br>
     */
    @Override
    public PagerList<T> queryPagerByLambda(final LambdaQueryWrapper<T, T> wrapper, final int pageIndex,
        final int pageSize) {
        return queryPagerBySpecification(build(wrapper), pageIndex, pageSize);
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param specification
     * @param pi
     * @param pageSize
     * @return <br>
     */
    @Override
    public PagerList<T> queryPagerBySpecification(final CriterialQuerySpecification<T> specification, final int pi,
        final int pageSize) {
        return queryPagerBySpecification(specification, pi, pageSize, getEntityClazz());
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entityClazz <br>
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setEntityClazz(final Class<?> entityClazz) {
        this.entityClazz = (Class<T>) entityClazz;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param wrapper
     * @param root
     * @param query
     * @param cb
     * @return <br>
     */
    private Expression<?>[] toGroupBy(final AbstractQueryWrapper<?> wrapper, final Root<? extends Tuple> root,
        final CriteriaQuery<Tuple> query, final CriteriaBuilder cb) {
        Expression<?>[] expressions = new Expression<?>[wrapper.getGroupList().size()];
        for (int i = 0; i < wrapper.getGroupList().size(); i++) {
            expressions[i] = root.get(wrapper.getGroupList().get(i));
        }
        return expressions;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param wrapper
     * @param root
     * @param query
     * @param cb
     * @return <br>
     */
    private Predicate[] toPredicate(final AbstractWrapper<?> wrapper, final Root<? extends Tuple> root,
        final CommonAbstractCriteria query, final CriteriaBuilder cb) {
        Predicate[] predicates = new Predicate[wrapper.getTempPredicates().size()
            + wrapper.getOrTempPredicates().size()];

        // 如果没有条件和order by 直接返回
        if (Objects.equals(predicates.length, 0)) {
            return null;
        }

        // 组装where条件
        int index = 0;
        for (int i = 0; i < wrapper.getTempPredicates().size(); ++i) {
            predicates[i] = toPredicate(root, query, cb, wrapper.getTempPredicates().get(i));
            index++;
        }
        // 处理or的过滤条件
        for (List<TempPredicate> orTempPredicate : wrapper.getOrTempPredicates()) {
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
    private Predicate toPredicate(final Root<? extends Tuple> root, final CommonAbstractCriteria query,
        final CriteriaBuilder criteriaBuilder, final TempPredicate predicate) {
        switch (predicate.getOperator()) {
            case EQ:
                return criteriaBuilder.equal(root.get(predicate.getFieldName()), predicate.getValue());
            case NE:
                return criteriaBuilder.notEqual(root.get(predicate.getFieldName()), predicate.getValue());
            case GE:
                return criteriaBuilder.ge(root.get(predicate.getFieldName()), (Number) predicate.getValue());
            case GREATER_THAN_OR_EQUAL_TO:
                return criteriaBuilder.greaterThanOrEqualTo(root.get(predicate.getFieldName()),
                    (Comparable) predicate.getValue());
            case GT:
                return criteriaBuilder.gt(root.get(predicate.getFieldName()), (Number) predicate.getValue());
            case GREATER_THAN:
                return criteriaBuilder.greaterThan(root.get(predicate.getFieldName()),
                    (Comparable) predicate.getValue());
            case LE:
                return criteriaBuilder.le(root.get(predicate.getFieldName()), (Number) predicate.getValue());
            case LESS_THAN_OR_EQUAL_TO:
                return criteriaBuilder.lessThanOrEqualTo(root.get(predicate.getFieldName()),
                    (Comparable) predicate.getValue());
            case LT:
                return criteriaBuilder.lt(root.get(predicate.getFieldName()), (Number) predicate.getValue());
            case LESS_THAN:
                return criteriaBuilder.lessThan(root.get(predicate.getFieldName()), (Comparable) predicate.getValue());
            case IN:
                CriteriaBuilder.In in = criteriaBuilder.in(root.get(predicate.getFieldName()));
                Collection<?> objects = (Collection<?>) predicate.getValue();
                for (Object obj : objects) {
                    in.value(obj);
                }
                return criteriaBuilder.and(in);
            case NOTIN:
                in = criteriaBuilder.in(root.get(predicate.getFieldName()));
                objects = (Collection<?>) predicate.getValue();
                for (Object obj : objects) {
                    in.value(obj);
                }
                return criteriaBuilder.not(in);
            case LIKE:
                String value = (String) predicate.getValue();
                Assert.notEmpty(value, ErrorCodeDef.PARAM_NOT_NULL, predicate.getFieldName());
                return criteriaBuilder.like(root.get(predicate.getFieldName()), value);
            case NOTLIKE:
                value = (String) predicate.getValue();
                Assert.notEmpty(value, ErrorCodeDef.PARAM_NOT_NULL, predicate.getFieldName());
                return criteriaBuilder.notLike(root.get(predicate.getFieldName()), value);
            case ISNULL:
                return criteriaBuilder.isNull(root.get(predicate.getFieldName()));
            case NOTNULL:
                return criteriaBuilder.isNotNull(root.get(predicate.getFieldName()));
            case BETWEEN:
                Comparable[] objs = (Comparable[]) predicate.getValue();
                return criteriaBuilder.between(root.get(predicate.getFieldName()), objs[0], objs[1]);
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
     * @param wrapper
     * @param root
     * @param query
     * @param cb
     * @return <br>
     */
    @SuppressWarnings("unchecked")
    private Selection<? extends Object>[] toSelection(final AbstractQueryWrapper<?> wrapper,
        final Root<? extends Tuple> root, final CriteriaQuery<?> query, final CriteriaBuilder cb) {

        Selection<? extends Object>[] selections = new Selection[wrapper.getSelectionList().size()];

        // 如果没有条件和order by 直接返回
        if (Objects.equals(selections.length, 0)) {
            return null;
        }

        // 组装查询条件
        for (int i = 0; i < wrapper.getSelectionList().size(); ++i) {
            selections[i] = toSelection(root, query, cb, wrapper.getSelectionList().get(i));
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

    /**
     * Description: 根据条件来做更新<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification <br>
     */
    public void update(final UpdateSpecification<T> specification) {
        updateBySpecification(build(specification.toSpecification(new UpdateWrapper<>())));
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param wrapper <br>
     */
    @Override
    public void update(final UpdateWrapper<T> wrapper) {
        updateBySpecification(build(wrapper));
    }

    /**
     * Description: 根据条件来做更新<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification <br>
     */
    public void updateByLambda(final LambdaUpdateSpecification<T> specification) {
        updateBySpecification(build(specification.toSpecification(new LambdaUpdateWrapper<>())));
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param wrapper <br>
     */
    @Override
    public void updateByLambda(final LambdaUpdateWrapper<T> wrapper) {
        updateBySpecification(build(wrapper));
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param specification <br>
     */
    @SuppressWarnings({
        "rawtypes", "unchecked"
    })
    @Override
    public void updateBySpecification(final CriterialUpdateSpecification<T> specification) {
        CriteriaBuilder cb = criteriaBuilder();
        CriteriaUpdate<T> query = cb.createCriteriaUpdate(getEntityClazz());
        Root root = query.from(getEntityClazz());
        Assert.notNull(specification, ErrorCodeDef.PARAM_NOT_NULL, "修改条件");
        specification.toPredicate(root, query, cb);
        updateByCriteria(query);
    }

}
