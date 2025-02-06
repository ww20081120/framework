/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.mongo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.db.core.BaseDao;
import com.hbasesoft.framework.db.core.BaseEntity;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.config.DataParam;
import com.hbasesoft.framework.db.core.criteria.AbstractQueryWrapper;
import com.hbasesoft.framework.db.core.criteria.TempPredicate;
import com.hbasesoft.framework.db.core.criteria.LambdaUpdateWrapper;
import com.hbasesoft.framework.db.core.criteria.DeleteWrapper;
import com.hbasesoft.framework.db.core.criteria.LambdaQueryWrapper;
import com.hbasesoft.framework.db.core.criteria.LambdaDeleteWrapper;
import com.hbasesoft.framework.db.core.criteria.TempSelection;
import com.hbasesoft.framework.db.core.criteria.QueryWrapper;
import com.hbasesoft.framework.db.core.criteria.AbstractWrapper;
import com.hbasesoft.framework.db.core.criteria.UpdateWrapper;
import com.hbasesoft.framework.db.core.utils.PagerList;
import com.mongodb.BasicDBObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.persistence.Entity;

/**
 * <Description> <br>
 * 
 * @param <T> t
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2024年12月30日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.mongo <br>
 */
public class MongoBaseDao<T extends BaseEntity> implements BaseDao<T> {


    /**
     *
     */
    private static final int MAX_SIZE = 1000;

    /**
     *
     */
    private static final int PAGE_SIZE = 10;

    /**
     * logger
     */
    private static Logger logger = new Logger(MongoBaseDao.class);


    /**
     *
     */
    private Class<T> entityClazz;


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

    protected MongoTemplate getMongoTemplate() {
        // 事务必须是开启的(Required)，否则获取不到
        return TransactionManagerHolder.getMongoTemplate();
    }


    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param sql
     * @param param
     * @return <br>
     */
    @Override
    public Object query(final String sql, final DataParam param) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param sql
     * @param param
     * @return <br>
     */
    @Override
    public int excuteSql(final String sql, final DataParam param) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param sql
     * @param param
     * @return <br>
     */
    @Override
    public int[] batchExcuteSql(final String[] sql, final DataParam param) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entityClazz <br>
     */
    @Override
    public void setEntityClazz(final Class<?> entityClazz) {
        this.entityClazz = (Class<T>) entityClazz;

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification <br>
     */
    @Override
    public void delete(final DeleteSpecification<T> specification) {
        deleteBySpecification(specification.toSpecification(new DeleteWrapper<>()));
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entity <br>
     */
    @Override
    public void delete(final T entity) {
        getMongoTemplate().remove(entity);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entities <br>
     */
    @Override
    public void deleteBatch(final Collection<T> entities) {
        if (entities.size() > GlobalConstants.DEFAULT_LINES) {
            throw new DaoException(ErrorCodeDef.TOO_MANY_OBJECTS);
        }
        // 构建查询条件，这里假设每个实体都有一个 getId 方法返回其唯一标识符
        java.lang.reflect.Field field = findPrimaryKeyField(entities.iterator().next().getClass());

        if (null == field) {
            throw new IllegalStateException("Entity does not have an @Id annotated field.");
        }
        String key = field.getAnnotation(Field.class) != null
                ? field.getAnnotation(Field.class).value() : field.getName();

        List<Object> idList = entities.stream()
                .map(entity -> {
                    try {
                        return field.get(entity);
                    }
                    catch (IllegalAccessException e) {
                        throw new DaoException(e);
                    }
                }).collect(Collectors.toList());

        Query query = new Query(Criteria.where(key).in(idList));
        // 使用 MongoTemplate 执行批量删除
        getMongoTemplate().remove(query, entityClazz);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id <br>
     */
    @Override
    public void deleteById(final Serializable id) {
        java.lang.reflect.Field field = findPrimaryKeyField(getEntityClazz());
        if (null == field) {
            throw new IllegalStateException("Entity does not have an @Id annotated field.");
        }
        String key = field.getAnnotation(Field.class) != null
                ? field.getAnnotation(Field.class).value() : field.getName();
        Query query = new Query(Criteria.where(key).is(id));
        getMongoTemplate().remove(query, entityClazz);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param ids <br>
     */
    @Override
    public void deleteByIds(final Collection<? extends Serializable> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        java.lang.reflect.Field field = findPrimaryKeyField(getEntityClazz());
        if (null == field) {
            throw new IllegalStateException("Entity does not have an @Id annotated field.");
        }
        String key = field.getAnnotation(Field.class) != null
                ? field.getAnnotation(Field.class).value() : field.getName();
        Query query = new Query(Criteria.where(key).in(new ArrayList<>(ids)));
        getMongoTemplate().remove(query, entityClazz);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification <br>
     */
    @Override
    public void deleteByLambda(final LambdaDeleteSpecification<T> specification) {
        deleteBySpecification(specification.toSpecification(new LambdaDeleteWrapper<>()));
    }

    private void deleteBySpecification(final AbstractWrapper<?> wrapper) {
        Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
        if (!entityClazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }
        Map<String, Object> parseEntityNameMap = parseEntityName(entityClazz, true);
        Query query = new Query();
        Criteria criteria = toPredicate(wrapper, parseEntityNameMap);
        // 如果没有条件直接抛出异常
        Assert.notNull(criteria, ErrorCodeDef.PARAM_NOT_NULL, "删除的条件");
        query.addCriteria(criteria);
        //执行 sql
        getMongoTemplate().remove(query, entityClazz);
    }


    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param wrapper <br>
     */
    @Override
    public void deleteByLambda(final LambdaDeleteWrapper<T> wrapper) {
        deleteBySpecification(wrapper);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification
     * @return <br>
     */
    @Override
    public T get(final QuerySpecification<T> specification) {
        return getBySpecification(specification.toSpecification(new QueryWrapper<>()));
    }

    /**
     *
     * @param specification
     * @return T
     */
    public T getBySpecification(final AbstractQueryWrapper<T> specification) {
        return getBySpecification(specification, entityClazz);
    }

    /**
     *
     * @param specification
     * @param clazz
     * @return <M> M
     * @param <M>
     */
    public <M> M getBySpecification(final AbstractQueryWrapper<T> specification, final Class<M> clazz) {

        Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
        if (!entityClazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }
        Assert.notNull(specification, ErrorCodeDef.PARAM_NOT_NULL, "查询条件");
        Map<String, Object> parseEntityNameMap = parseEntityName(entityClazz, true);
        TypedAggregation<T>  aggregation = toQueryPredicate(parseEntityNameMap, specification, entityClazz, null, null);

        //执行 sql
        AggregationResults<M> results = getMongoTemplate().aggregate(aggregation, clazz);
        return  results.getMappedResults().stream().findFirst().orElse(null);
    }

    private TypedAggregation<T> toQueryPredicate(final Map<String, Object> parseEntityNameMap,
                                  final AbstractQueryWrapper<T> specification, final Class<T> clazz,
                                  final Integer pi, final Integer pageSize) {
        // 构建匹配条件 (WHERE)
        Criteria criteria = toPredicate(specification, parseEntityNameMap);

        // 创建聚合管道(排序和分组 使用)
        List<AggregationOperation> operations = new ArrayList<>();

        // 添加匹配操作
        if (criteria != null && !criteria.getCriteriaObject().isEmpty()) {
            MatchOperation matchOperation = Aggregation.match(criteria);
            operations.add(matchOperation);
        }
        // 添加select后面的 查询字段
        if (specification != null && specification.getSelectionList() != null
                && !specification.getSelectionList().isEmpty()) {
            for (TempSelection selection : specification.getSelectionList()) {
                toSelection(operations, parseEntityNameMap, selection);
            }
        }
        else {
            // 默认选择所有字段
            operations.add(Aggregation.project().andInclude(parseEntityNameMap.values().toArray(new String[0])));
        }

        // 处理分组 (GROUP BY)
        if (!specification.getGroupList().isEmpty()) {
            GroupOperation groupOperation = Aggregation.group(
                    specification.getGroupList().stream()
                            .map(group -> parseEntityNameMap.getOrDefault(group, group).toString())
                            .toArray(String[]::new)
            );
            operations.add(groupOperation);
        }
        // 构建 ORDER BY 子句
        if (!specification.getOrderByList().isEmpty()) {
            SortOperation sortOperation = Aggregation.sort(
                    specification.getOrderByList().stream()
                            .map(order -> Sort.by(
                                    order.isDesc() ? Sort.Direction.DESC : Sort.Direction.ASC,
                                    parseEntityNameMap.getOrDefault(order.getProperty(), order.getProperty()).toString()
                            ))
                            .reduce(Sort::and) //当存在多个排序条件  汇合成一个最终的排序规则
                            .orElse(Sort.unsorted()) //不存在 返回一个空排序规则
            );
            operations.add(sortOperation);
        }
        // 添加限制操作以限制返回的文档数量为 1000
        if (pageSize != null && pageSize > 0) {
            // 计算要跳过的文档数
            int skipCount = (pi - 1) * pageSize;

            // 添加 skip 和 limit 操作以实现分页
            operations.add(Aggregation.skip(skipCount));
            operations.add(Aggregation.limit(pageSize));
        }
        else {
            // 添加限制操作以限制返回的文档数量为 1000
            operations.add(Aggregation.limit(MAX_SIZE));
        }
        return Aggregation.newAggregation(clazz, operations);
    }

    private TypedAggregation<T> toCountQueryPredicate(final Map<String, Object> parseEntityNameMap,
                                                 final AbstractQueryWrapper<T> specification, final Class<T> clazz) {
        // 构建匹配条件 (WHERE)
        Criteria criteria = toPredicate(specification, parseEntityNameMap);

        // 创建聚合管道
        List<AggregationOperation> operations = new ArrayList<>();

        // 添加匹配操作
        if (criteria != null && !criteria.getCriteriaObject().isEmpty()) {
            MatchOperation matchOperation = Aggregation.match(criteria);
            operations.add(matchOperation);
        }

        // 添加分组操作，仅计算匹配文档的数量
        GroupOperation groupOperation = Aggregation.group().count().as("total");
        operations.add(groupOperation);

        // 返回构建好的聚合对象
        return Aggregation.newAggregation(clazz, operations);
    }

    private void toSelection(final List<AggregationOperation> operations, final Map<String, Object> parseEntityNameMap,
                             final TempSelection tempSelection) {
        String fieldName = parseEntityNameMap.getOrDefault(tempSelection.getField(),
                tempSelection.getField()).toString();
        String alias = tempSelection.getAlias();
        switch (tempSelection.getOperator()) {
            case SUM:
                operations.add(Aggregation.group().sum(fieldName).as(alias != null ? alias : fieldName));
                break;
            case AVG:
                operations.add(Aggregation.group().avg(fieldName).as(alias != null ? alias : fieldName));
                break;
            case COUNT:
                operations.add(Aggregation.group().count().as(alias != null ? alias : "count"));
                break;
            case MAX:
                operations.add(Aggregation.group().max(fieldName).as(alias != null ? alias : fieldName));
                break;
            case MIN:
                operations.add(Aggregation.group().min(fieldName).as(alias != null ? alias : fieldName));
                break;
            case DIFF:
                if (tempSelection.getField2() != null) {
                    String secondFieldName = parseEntityNameMap.getOrDefault(tempSelection.getField2(),
                            tempSelection.getField2()).toString();
                    // 使用 $project 来计算差异
                    operations.add(Aggregation.project().andExpression(String.format("%s - %s",
                            fieldName, secondFieldName)).as(alias));
                }
                else {
                    throw new DaoException(ErrorCodeDef.PARAM_ERROR, "DIFF requires two fields.");
                }
                break;
            case SUMMING:
                if (tempSelection.getField2() != null) {
                    String secondFieldName = parseEntityNameMap.getOrDefault(tempSelection.getField2(),
                            tempSelection.getField2()).toString();
                    operations.add(Aggregation.group().sum(String.format("%s + %s", fieldName,
                            secondFieldName)).as(alias));
                }
                else {
                    throw new DaoException(ErrorCodeDef.PARAM_ERROR, "SUMMING requires two fields.");
                }
                break;
            default:
                throw new DaoException(ErrorCodeDef.PARAM_ERROR, tempSelection.getOperator().name());
        }
    }





    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <M>
     * @param specification
     * @param clazz
     * @return <br>
     */
    @Override
    public <M> M get(final QuerySpecification<T> specification, final Class<M> clazz) {
        return getBySpecification(specification.toSpecification(new QueryWrapper<>()), clazz);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param wrapper
     * @return <br>
     */
    @Override
    public T get(final QueryWrapper<T> wrapper) {
        return getBySpecification(wrapper);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <M>
     * @param wrapper
     * @param clazz
     * @return <br>
     */
    @Override
    public <M> M get(final QueryWrapper<T> wrapper, final Class<M> clazz) {
        return getBySpecification(wrapper, clazz);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @return <br>
     */
    @Override
    public T get(final Serializable id) {
        java.lang.reflect.Field field = findPrimaryKeyField(getEntityClazz());
        if (null == field) {
            throw new IllegalStateException("Entity does not have an @Id annotated field.");
        }
        String key = field.getAnnotation(Field.class) != null
                ? field.getAnnotation(Field.class).value() : field.getName();
        Query query = new Query(Criteria.where(key).is(id));
        T one = getMongoTemplate().findOne(query, entityClazz);
        return one;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <M>
     * @param specification
     * @param clazz
     * @return <br>
     */
    @Override
    public <M> M getByLambda(final LambdaQuerySpecification<T, M> specification, final Class<M> clazz) {
        return getBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()), clazz);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification
     * @return <br>
     */
    @Override
    public T getByLambda(final LambdaQuerySpecification<T, T> specification) {
        return getBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()));
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <M>
     * @param wrapper
     * @param clazz
     * @return <br>
     */
    @Override
    public <M> M getByLambda(final LambdaQueryWrapper<T, M> wrapper, final Class<M> clazz) {
        return getBySpecification(wrapper, clazz);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param wrapper
     * @return <br>
     */
    @Override
    public T getByLambda(final LambdaQueryWrapper<T, T> wrapper) {
        return getBySpecification(wrapper);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification
     * @return <br>
     */
    @Override
    public List<T> query(final QuerySpecification<T> specification) {
        return queryBySpecification(specification.toSpecification(new QueryWrapper<>()));

    }

    /**
     *
     * @param specification
     * @return List<T>
     */
    private List<T> queryBySpecification(final AbstractQueryWrapper<T> specification) {
        return queryBySpecification(specification, entityClazz);
    }

    /**
     *
     * @param wrapper
     * @param clazz
     * @return <M> List<M>
     * @param <M>
     */
    public <M> List<M> queryBySpecification(final AbstractQueryWrapper<T> wrapper, final Class<M> clazz) {
        Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
        if (!entityClazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }
        Assert.notNull(wrapper, ErrorCodeDef.PARAM_NOT_NULL, "查询条件");
        Map<String, Object> parseEntityNameMap = parseEntityName(entityClazz, true);

        TypedAggregation<T> queryPredicate = toQueryPredicate(parseEntityNameMap, wrapper,
                (Class<T>) clazz, null, null);

        // 使用 MongoTemplate 执行聚合查询，并将结果映射到指定的类
        List<M> results = getMongoTemplate().aggregate(queryPredicate, entityClazz, clazz).getMappedResults();
        return results;
    }




    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <M>
     * @param specification
     * @param clazz
     * @return <br>
     */
    @Override
    public <M> List<M> query(final QuerySpecification<T> specification, final Class<M> clazz) {
        return queryBySpecification(specification.toSpecification(new QueryWrapper<>()), clazz);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param wrapper
     * @return <br>
     */
    @Override
    public List<T> query(final QueryWrapper<T> wrapper) {
        return queryBySpecification(wrapper);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <M>
     * @param wrapper
     * @param clazz
     * @return <br>
     */
    @Override
    public <M> List<M> query(final QueryWrapper<T> wrapper, final Class<M> clazz) {
        return queryBySpecification(wrapper, clazz);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public List<T> queryAll() {
        Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
        // 创建一个空的 Query 对象，这将匹配集合中的所有文档
        Query query = new Query();
        // 执行查询并返回结果列表
        return getMongoTemplate().find(query, entityClazz);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <M>
     * @param specification
     * @param clazz
     * @return <br>
     */
    @Override
    public <M> List<M> queryByLambda(final LambdaQuerySpecification<T, M> specification, final Class<M> clazz) {
        return queryBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()), clazz);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification
     * @return <br>
     */
    @Override
    public List<T> queryByLambda(final LambdaQuerySpecification<T, T> specification) {
        return queryBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()));
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <M>
     * @param wrapper
     * @param clazz
     * @return <br>
     */
    @Override
    public <M> List<M> queryByLambda(final LambdaQueryWrapper<T, M> wrapper, final Class<M> clazz) {
        return queryBySpecification(wrapper, clazz);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param wrapper
     * @return <br>
     */
    @Override
    public List<T> queryByLambda(final LambdaQueryWrapper<T, T> wrapper) {
        return queryBySpecification(wrapper);
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
    @Override
    public PagerList<T> queryPager(final QuerySpecification<T> specification, final int pageIndex, final int pageSize) {
        return queryPagerBySpecification(specification.toSpecification(new QueryWrapper<>()), pageIndex,
                pageSize);
    }
    private PagerList<T> queryPagerBySpecification(final AbstractQueryWrapper<T> specification,
                                                   final int pageIndex, final int pageSize) {
        return queryPagerBySpecification(specification, pageIndex, pageSize, entityClazz);
    }

    /**
     *
     * @param wrapper
     * @param pi
     * @param ps
     * @param clazz
     * @return <M> PagerList<M>
     * @param <M>
     */
    public <M> PagerList<M> queryPagerBySpecification(final AbstractQueryWrapper<T> wrapper, final int pi,
                                                      final int ps, final Class<M> clazz) {
        Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
        if (!entityClazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }
        Assert.notNull(wrapper, ErrorCodeDef.PARAM_NOT_NULL, "查询条件");
        Map<String, Object> parseEntityNameMap = parseEntityName(entityClazz, true);
        int pageSize = ps < 1 ? PAGE_SIZE : ps;
        int pageIndex = pi < 1 ? 1 : pi;
        TypedAggregation<T> queryPredicate = toQueryPredicate(parseEntityNameMap,
                wrapper, (Class<T>) clazz, pageIndex, pageSize);

        // 使用 MongoTemplate 执行聚合查询，并将结果映射到指定的类
        List<M> results = getMongoTemplate().aggregate(queryPredicate, entityClazz, clazz).getMappedResults();

        TypedAggregation<T> countQueryPredicate = toCountQueryPredicate(parseEntityNameMap, wrapper, (Class<T>) clazz);
        AggregationResults<BasicDBObject> countResults =
                getMongoTemplate().aggregate(countQueryPredicate, entityClazz, BasicDBObject.class);
        BasicDBObject countResult = countResults.getMappedResults().stream().findFirst().orElse(new BasicDBObject());
        long totalCount = countResult.getLong("total", 0L);

        // 创建并返回 PagerList 对象
        PagerList<M> pagerList = new PagerList<>();
        pagerList.setPageIndex(pi);
        pagerList.setPageSize(pageSize);
        pagerList.setTotalCount(totalCount);
        pagerList.addAll(results);
        return pagerList;
    }



    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <M>
     * @param specification
     * @param pageIndex
     * @param pageSize
     * @param clazz
     * @return <br>
     */
    @Override
    public <M> PagerList<M> queryPager(final QuerySpecification<T> specification, final int pageIndex,
        final int pageSize, final Class<M> clazz) {
        return queryPagerBySpecification(specification.toSpecification(new QueryWrapper<>()), pageIndex,
                pageSize, clazz);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param wrapper
     * @param pageIndex
     * @param pageSize
     * @return <br>
     */
    @Override
    public PagerList<T> queryPager(final QueryWrapper<T> wrapper, final int pageIndex, final int pageSize) {
        return queryPagerBySpecification(wrapper, pageIndex, pageSize);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
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
        return queryPagerBySpecification(wrapper, pageIndex, pageSize, clazz);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <M>
     * @param specification
     * @param pageIndex
     * @param pageSize
     * @param clazz
     * @return <br>
     */
    @Override
    public <M> PagerList<M> queryPagerByLambda(final LambdaQuerySpecification<T, M> specification, final int pageIndex,
        final int pageSize, final Class<M> clazz) {
        return queryPagerBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()), pageIndex,
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
    @Override
    public PagerList<T> queryPagerByLambda(final LambdaQuerySpecification<T, T> specification, final int pageIndex,
        final int pageSize) {
        return queryPagerBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()),
                pageIndex, pageSize);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
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
        return queryPagerBySpecification(wrapper, pageIndex, pageSize, clazz);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param wrapper
     * @param pageIndex
     * @param pageSize
     * @return <br>
     */
    @Override
    public PagerList<T> queryPagerByLambda(final LambdaQueryWrapper<T, T> wrapper, final int pageIndex,
        final int pageSize) {
        return queryPagerBySpecification(wrapper, pageIndex, pageSize);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entity <br>
     */
    @Override
    public void save(final T entity) {
        getMongoTemplate().save(entity);

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entitys <br>
     */
    @Override
    public void saveBatch(final List<T> entitys) {
        // 使用 MongoTemplate 批量保存实体到 MongoDB
        if (entitys.size() > GlobalConstants.DEFAULT_LINES) {
            throw new DaoException(ErrorCodeDef.TOO_MANY_OBJECTS);
        }
        if (CollectionUtils.isEmpty(entitys)) {
            return;
        }
        getMongoTemplate().insert(entitys, entitys.get(0).getClass());
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param pojo <br>
     */
    @Override
    public void update(final T pojo) {

        Map<String, Object> columnValues = parseEntity(pojo, false);
        if (MapUtils.isEmpty(columnValues)) {
            throw new DaoException(ErrorCodeDef.ANALYSIS_ENTITY_ERROR);
        }
        // 检查是否存在主键 ENTITY_ID，如果不存在则无法执行更新操作
        Object entityId = columnValues.get("ENTITY_ID");
        if (entityId == null) {
            throw new DaoException(ErrorCodeDef.ID_IS_NULL);
        }
        Query query = new Query(Criteria.where((String) entityId).is(columnValues.get(columnValues.get("ENTITY_ID"))));
        // 创建更新对象
        Update update = new Update();
        for (Map.Entry<String, Object> entry : columnValues.entrySet()) {
            // 跳过主键字段，因为它不是要更新的数据部分
            if (StringUtils.equals("ENTITY_ID", entry.getKey()) || Objects.equals(entry.getKey(), entityId)) {
                continue;
            }
            update.set(entry.getKey(), entry.getValue());

        }
        // 执行更新操作
        getMongoTemplate().updateFirst(query, update, pojo.getClass());


    }

    /**
     * Description: <br>
     * @author 王伟<br>
     * @taskId <br>
     * @param specification <br>
     */
    @Override
    public void update(final UpdateSpecification<T> specification) {
        updateBySpecification(specification.toSpecification(new UpdateWrapper<>()));
    }

    /**
     *
     * @param wrapper
     */
    public void updateBySpecification(final UpdateWrapper<T> wrapper) {

        Assert.notEmpty(wrapper.getValueMap(), ErrorCodeDef.PARAM_NOT_NULL, "修改的内容");
        Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
        if (!entityClazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }
        //获取 解析实体类，提取带有@Column注解的字段及属性名称
        Map<String, Object> parseEntityNameMap = parseEntityName(entityClazz, true);
        // 创建更新对象
        Update update = new Update();
        for (Map.Entry<String, Object> entry : wrapper.getValueMap().entrySet()) {
            String fieldName = parseEntityNameMap.getOrDefault(entry.getKey(), entry.getKey()).toString();
            update.set(fieldName, entry.getValue());
        }

        Criteria criteria = toPredicate(wrapper, parseEntityNameMap);

        // 如果没有条件直接抛出异常
        Assert.isTrue(criteria != null && !criteria.getCriteriaObject().isEmpty(),
                ErrorCodeDef.PARAM_NOT_NULL, "修改的条件");
        //执行 sql
        Query query = new Query(criteria);
        // 执行更新操作
        getMongoTemplate().updateMulti(query, update, entityClazz);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param wrapper <br>
     */
    @Override
    public void update(final UpdateWrapper<T> wrapper) {
        updateBySpecification(wrapper);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entitys <br>
     */
    @Override
    public void updateBatch(final List<T> entitys) {
        if (entitys.size() > GlobalConstants.DEFAULT_LINES) {
            throw new DaoException(ErrorCodeDef.TOO_MANY_OBJECTS);
        }
        if (CollectionUtils.isEmpty(entitys)) {
            return;
        }
        for (T entity : entitys) {
           update(entity);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification <br>
     */
    @Override
    public void updateByLambda(final LambdaUpdateSpecification<T> specification) {
        updateBySpecification(specification.toSpecification(new LambdaUpdateWrapper<>()));
    }

    /**
     *
     * @param wrapper
     */
    public void updateBySpecification(final LambdaUpdateWrapper<?> wrapper) {

        Assert.notEmpty(wrapper.getValueMap(), ErrorCodeDef.PARAM_NOT_NULL, "修改的内容");
        Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
        if (!entityClazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }
        //获取 解析实体类，提取带有@Column注解的字段及属性名称
        Map<String, Object> parseEntityNameMap = parseEntityName(entityClazz, true);
        // 创建更新对象
        Update update = new Update();
        for (Map.Entry<String, Object> entry : wrapper.getValueMap().entrySet()) {
            String fieldName = parseEntityNameMap.getOrDefault(entry.getKey(), entry.getKey()).toString();
            update.set(fieldName, entry.getValue());
        }
        Criteria criteria = toPredicate(wrapper, parseEntityNameMap);
        // 如果没有条件直接抛出异常
        Assert.isTrue(criteria != null && !criteria.getCriteriaObject().isEmpty(),
                ErrorCodeDef.PARAM_NOT_NULL, "修改的条件");
        //执行 sql
        Query query = new Query(criteria);
        // 执行更新操作
        getMongoTemplate().updateMulti(query, update, entityClazz);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param wrapper <br>
     */
    @Override
    public void updateByLambda(final LambdaUpdateWrapper<T> wrapper) {
        updateBySpecification(wrapper);
    }

    /**
     * 解析实体类，提取带有@Column注解的字段及其值
     *
     * @param entity
     * @param continueEntity
     * @return Map<String, Object>
     */
    private Map<String, Object> parseEntity(final T entity, final boolean continueEntity) {
        // 解析实体类，提取带有@Column注解的字段及其值
        Map<String, Object> columnValues = new HashMap<>();
        try {
            for (java.lang.reflect.Field field : entity.getClass().getDeclaredFields()) {
                Field columnAnnotation = field.getAnnotation(Field.class);
                if (columnAnnotation != null) {
                    field.setAccessible(true); // 取消Java的访问控制检查
                    if (field.isAnnotationPresent(Id.class)) {
                        columnValues.put("ENTITY_ID", columnAnnotation.name());
                    }
                    if (continueEntity && Objects.isNull(field.get(entity))) {
                        continue;
                    }
                    columnValues.put(columnAnnotation.name(), field.get(entity));

                }
            }
        }
        catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
            new DaoException(ErrorCodeDef.ANALYSIS_ENTITY_ERROR, e);
        }
        return columnValues;
    }

    /**
     * 解析实体类，提取带有@Column注解的字段及属性名称
     *
     * @param entity
     * @param continueEntity
     * @return Map<String, Object>
     */
    private Map<String, Object> parseEntityName(final T entity, final boolean continueEntity) {
        // 解析实体类，提取带有@Column注解的name 和其对应的 类的属性名
        Map<String, Object> columnValues = new HashMap<>();
        try {
            // 获取类的所有声明字段，包括私有字段
            for (java.lang.reflect.Field field : entity.getClass().getDeclaredFields()) {
                // 检查字段是否有@Column注解
                if (field.isAnnotationPresent(Field.class)) {
                    Field columnAnnotation = field.getAnnotation(Field.class);
                    field.setAccessible(true); // 取消Java的访问控制检查
                    String columnName = columnAnnotation.name(); // 获取@Column注解的name属性
                    String fieldName = field.getName(); // 获取字段名
                    if (continueEntity && Objects.isNull(field.get(entity))) {
                        continue;
                    }
                    if (field.isAnnotationPresent(Id.class)) {
                        columnValues.put("ENTITY_ID", columnName);
                    }
                    // 将@Column注解的name和字段名存入map
                    columnValues.put(columnName, fieldName);
                }
            }
        }
        catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
            new DaoException(ErrorCodeDef.ANALYSIS_ENTITY_ERROR, e);
        }
        return columnValues;
    }

    /**
     * 解析实体类，提取带有@Column注解的字段及属性名称
     *
     * @param clazz
     * @param keyIsFieldName 当 为true的时候  那么 属性名为key
     * @return Map<String, Object>
     */
    private Map<String, Object> parseEntityName(final Class<?> clazz, final boolean keyIsFieldName) {
        // 解析实体类，提取带有@Column注解的name 和其对应的 类的属性名
        Map<String, Object> columnValues = new HashMap<>();

        // 获取类的所有声明字段，包括私有字段
        for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
            // 检查字段是否有@Column注解
            if (field.isAnnotationPresent(Field.class)) {
                Field columnAnnotation = field.getAnnotation(Field.class);
                String columnName = columnAnnotation.name(); // 获取@Column注解的name属性
                String fieldName = field.getName(); // 获取字段名
                // 将@Column注解的name和字段名存入map
                if (keyIsFieldName) {
                    columnValues.put(fieldName, columnName);
                }
                else {
                    columnValues.put(columnName, fieldName);
                }

            }
        }

        return columnValues;
    }

    /**
     * 解析主键名称
     *
     * @param clazz
     * @return Field
     */
    private java.lang.reflect.Field findPrimaryKeyField(final Class<?> clazz) {
        for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
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
     * Description: <br>
     * @return Criteria
     * @param wrapper
     * @param parseEntityNameMap
     * @author 王伟<br>
     * @taskId <br>
     */
    private Criteria toPredicate(final AbstractWrapper<?> wrapper, final Map<String, Object> parseEntityNameMap) {
        // 检查是否有任何条件
        boolean hasConditions = CollectionUtils.isNotEmpty(wrapper.getTempPredicates())
                || CollectionUtils.isNotEmpty(wrapper.getOrTempPredicates());
        // 如果没有条件和order by 直接返回
        if (!hasConditions) {
            return null;
        }

        // 组装 AND 条件
        List<Criteria> andCriteriaList = wrapper.getTempPredicates().stream()
                .filter(tempPredicate -> Objects.nonNull(parseEntityNameMap.get(tempPredicate.getFieldName())))
                .map(tempPredicate -> toPredicate(parseEntityNameMap, tempPredicate))
                .collect(Collectors.toList());

        // 组装 OR 条件
        List<Criteria> orCriteriaList = wrapper.getOrTempPredicates().stream()
                .filter(CollectionUtils::isNotEmpty) // 只处理非空的OR条件组
                .map(orGroup -> orGroup.stream()
                        .filter(tempPredicate -> Objects.nonNull(parseEntityNameMap.get(tempPredicate.getFieldName())))
                        .map(tempPredicate -> toPredicate(parseEntityNameMap, tempPredicate))
                        .collect(Collectors.collectingAndThen(Collectors.toList(),
                                subCriteriaList -> subCriteriaList.isEmpty() ? null
                               : new Criteria().orOperator(subCriteriaList.toArray(new Criteria[0])))))
                .filter(Objects::nonNull) // 移除无效（空）的OR条件组
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(andCriteriaList) && CollectionUtils.isEmpty(orCriteriaList)) {
            return null;
        }

        Criteria criteria = new Criteria();
        // 合并 AND 和 OR 条件
        if (CollectionUtils.isNotEmpty(andCriteriaList)) {
            criteria.andOperator(andCriteriaList.toArray(new Criteria[0]));
        }
        if (CollectionUtils.isNotEmpty(orCriteriaList)) {
            criteria.orOperator(orCriteriaList.toArray(new Criteria[0]));
        }
        return criteria;

    }

    private Criteria toPredicate(final Map<String, Object> parseEntityNameMap, final TempPredicate predicate) {
        String fieldName = (String) parseEntityNameMap.get(predicate.getFieldName());
        switch (predicate.getOperator()) {
            case EQ:
                return Criteria.where(fieldName).is(predicate.getValue());
            case NE:
                return Criteria.where(fieldName).ne(predicate.getValue());
            case GE:
                return Criteria.where(fieldName).gte(predicate.getValue());
            case GREATER_THAN_OR_EQUAL_TO:
                return Criteria.where(fieldName).gte(predicate.getValue());
            case GT:
                return Criteria.where(fieldName).gt(predicate.getValue());
            case GREATER_THAN:
                return Criteria.where(fieldName).gt(predicate.getValue());
            case LE:
                return Criteria.where(fieldName).lte(predicate.getValue());
            case LESS_THAN_OR_EQUAL_TO:
                return Criteria.where(fieldName).lte(predicate.getValue());
            case LT:
                return Criteria.where(fieldName).lt(predicate.getValue());
            case LESS_THAN:
                return Criteria.where(fieldName).lt(predicate.getValue());
            case IN:
                Collection<?> objects = (Collection<?>) predicate.getValue();
                return Criteria.where(fieldName).in(objects);
            case NOTIN:
                objects = (Collection<?>) predicate.getValue();
                return Criteria.where(fieldName).nin(objects);
            case LIKE:
                String value = (String) predicate.getValue();
                Assert.notEmpty(value, ErrorCodeDef.PARAM_NOT_NULL, predicate.getFieldName());
                return Criteria.where(fieldName).regex(value);
            case NOTLIKE:
                value = (String) predicate.getValue();
                Assert.notEmpty(value, ErrorCodeDef.PARAM_NOT_NULL, predicate.getFieldName());
                return Criteria.where(fieldName).not().regex(value);
            case ISNULL:
                return Criteria.where(fieldName).isNull();
            case NOTNULL:
                return Criteria.where(fieldName).not();
            case BETWEEN:
                Comparable[] objs = (Comparable[]) predicate.getValue();
                return Criteria.where(fieldName).gte(objs[0]).lte(objs[1]);
            default:
                return null;
        }
    }

}
