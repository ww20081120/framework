/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.mongo;

import static com.hbasesoft.framework.common.ErrorCodeDef.DAO_PROPERTY_IS_EMPTY;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.alibaba.fastjson2.JSONObject;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.db.core.BaseEntity;
import com.hbasesoft.framework.db.core.DaoConstants;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.config.DataParam;
import com.hbasesoft.framework.db.core.criteria.AbstractQueryWrapper;
import com.hbasesoft.framework.db.core.criteria.AbstractWrapper;
import com.hbasesoft.framework.db.core.criteria.DeleteWrapper;
import com.hbasesoft.framework.db.core.criteria.LambdaDeleteWrapper;
import com.hbasesoft.framework.db.core.criteria.LambdaQueryWrapper;
import com.hbasesoft.framework.db.core.criteria.LambdaUpdateWrapper;
import com.hbasesoft.framework.db.core.criteria.Operator;
import com.hbasesoft.framework.db.core.criteria.QueryWrapper;
import com.hbasesoft.framework.db.core.criteria.TempSelection;
import com.hbasesoft.framework.db.core.criteria.UpdateWrapper;
import com.hbasesoft.framework.db.core.utils.PagerList;
import com.hbasesoft.framework.db.core.utils.SQlCheckUtil;
import com.mongodb.BasicDBObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

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
public class MongoBaseDao<T extends BaseEntity> implements BaseMongoDao<T> {

    /** */
    private static Logger logger = new Logger(MongoBaseDao.class);

    /** */
    private static final int MAX_SIZE = 1000;

    /** */
    private Class<T> entityClazz;

    /** */
    public static final Map<Class<?>, Object> NUMBER_DEFAULT = new HashMap<Class<?>, Object>() {
        {
            put(Integer.class, 0);
            put(int.class, 0);
            put(Long.class, 0L);
            put(long.class, 0L);
            put(Double.class, 0.0d);
            put(double.class, 0.0d);
            put(Float.class, 0.0f);
            put(float.class, 0.0f);
            put(Short.class, (short) 0);
            put(short.class, (short) 0);
            put(Byte.class, (byte) 0);
            put(byte.class, (byte) 0);
        }
    };

    /** */
    private static final int PAGE_SIZE = 10;

    /**
     * Description: <br>
     *
     * @param sqls
     * @param param
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public int[] batchExcuteSql(final String[] sqls, final DataParam param) {
        try {

            int[] result = new int[sqls.length];
            for (int i = 0; i < sqls.length; i++) {
                String sql = sqls[i];
                result[i] = excuteSql(sql, param);
            }
            return result;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DaoException(ErrorCodeDef.BATCH_EXECUTE_ERROR, e);
        }
    }

    /**
     * Description: <br>
     *
     * @param specification <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public void delete(final DeleteSpecification<T> specification) {
        deleteBySpecification(specification.toSpecification(new DeleteWrapper<>()));
    }

    /**
     * Description: <br>
     *
     * @param entity <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public void delete(final T entity) {
        // 解析实体类并获取表结构信息 （true表示忽略空值）
        Map<String, Object> columnValues = parseEntity(entity, true);
        if (MapUtils.isEmpty(columnValues)) {
            throw new DaoException(ErrorCodeDef.ANALYSIS_ENTITY_ERROR);
        }
        // 获取文档名称（集合名称）
        String collectionName = MongoBaseDaoUtil.getTableName(entity.getClass());
        // 构建查询条件
        Query query = buildQueryFromColumnValues(columnValues);
        if (Objects.isNull(query.getQueryObject()) || query.getQueryObject().isEmpty()) {
            throw new DaoException(ErrorCodeDef.PARAM_NOT_NULL, "删除条件");
        }
        getMongoTemplate().remove(query, collectionName);
    }

    /**
     * Description: <br>
     *
     * @param entities <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public void deleteBatch(final Collection<T> entities) {
        if (entities.size() > GlobalConstants.DEFAULT_LINES) {
            throw new DaoException(ErrorCodeDef.TOO_MANY_OBJECTS);
        }
        // 构建查询条件，这里假设每个实体都有一个 getId 方法返回其唯一标识符
        Field field = MongoBaseDaoUtil.findPrimaryKeyField(entities.iterator().next().getClass());
        // 获取文档名称（集合名称）
        String collectionName = MongoBaseDaoUtil.getTableName(entities.iterator().next().getClass());
        if (null == field || StringUtils.isBlank(collectionName)) {
            throw new IllegalStateException("Entity does not have an @Id annotated field.");
        }
        String key = field.getAnnotation(Column.class) != null ? "_id" : field.getName();

        List<Object> idList = entities.stream().map(entity -> {
            try {
                return field.get(entity);
            }
            catch (IllegalAccessException e) {
                throw new DaoException(e);
            }
        }).collect(Collectors.toList());

        Query query = new Query(Criteria.where(key).in(idList));
        // 使用 MongoTemplate 执行批量删除
        getMongoTemplate().remove(query, collectionName);
    }

    /**
     * Description: <br>
     *
     * @param id <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public void deleteById(final Serializable id) {
        // 获取文档名称（集合名称）
        String collectionName = MongoBaseDaoUtil.getTableName(entityClazz);
        Field field = MongoBaseDaoUtil.findPrimaryKeyField(getEntityClazz());
        if (null == field || StringUtils.isBlank(collectionName)) {
            throw new IllegalStateException("Entity does not have an @Id annotated field.");
        }
        String key = field.getAnnotation(Column.class) != null ? "_id" : field.getName();
        Query query = new Query(Criteria.where(key).is(id));
        getMongoTemplate().remove(query, collectionName);
    }

    /**
     * Description: <br>
     *
     * @param ids <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public void deleteByIds(final Collection<? extends Serializable> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        Field field = MongoBaseDaoUtil.findPrimaryKeyField(getEntityClazz());
        // 获取文档名称（集合名称）
        String collectionName = MongoBaseDaoUtil.getTableName(entityClazz);
        if (null == field || StringUtils.isBlank(collectionName)) {
            throw new IllegalStateException("Entity does not have an @Id annotated field.");
        }
        String key = field.getAnnotation(Column.class) != null ? "_id" : field.getName();
        Query query = new Query(Criteria.where(key).in(new ArrayList<>(ids)));
        getMongoTemplate().remove(query, collectionName);
    }

    /**
     * Description: <br>
     *
     * @param specification <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public void deleteByLambda(final LambdaDeleteSpecification<T> specification) {
        deleteBySpecification(specification.toSpecification(new LambdaDeleteWrapper<>()));
    }

    /**
     * Description: <br>
     *
     * @param wrapper <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public void deleteByLambda(final LambdaDeleteWrapper<T> wrapper) {
        deleteBySpecification(wrapper);
    }

    /**
     * Description: <br>
     *
     * @param sql
     * @param param
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public int excuteSql(final String sql, final DataParam param) {
        try {
            SQlCheckUtil.checkSql(sql);
            Document commandDocument = Document.parse(sql);
            MongoBaseDaoUtil.replacePlaceholders(commandDocument, param.getParamMap());

            Document document = getMongoTemplate().getDb().runCommand(commandDocument);
            // 根据不同的操作类型获取受影响的文档数
            if (document.containsKey("n") && document.get("n") instanceof Number) {
                return ((Number) document.get("n")).intValue();
            }
            else if (document.containsKey("deletedCount") && document.get("deletedCount") instanceof Number) {
                return ((Number) document.get("deletedCount")).intValue();
            }
            else if (document.containsKey("upsertedCount") && document.get("upsertedCount") instanceof Number) {
                return ((Number) document.get("upsertedCount")).intValue();
            }
            else if (document.containsKey("matchedCount") && document.get("matchedCount") instanceof Number) {
                return ((Number) document.get("matchedCount")).intValue();
            }
            else if (document.containsKey("insertedCount") && document.get("insertedCount") instanceof Number) {
                return ((Number) document.get("insertedCount")).intValue();
            }
            else {
                logger.warn("无法确定受影响的文档数，返回0。");
                return 0;
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DaoException(ErrorCodeDef.EXECUTE_ERROR, e);
        }
    }

    /**
     * Description: <br>
     *
     * @param specification
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public T get(final QuerySpecification<T> specification) {
        return getBySpecification(specification.toSpecification(new QueryWrapper<>()));
    }

    /**
     * Description: <br>
     *
     * @param <M>
     * @param specification
     * @param clazz
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public <M> M get(final QuerySpecification<T> specification, final Class<M> clazz) {
        return getBySpecification(specification.toSpecification(new QueryWrapper<>()), clazz);
    }

    /**
     * Description: <br>
     *
     * @param wrapper
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public T get(final QueryWrapper<T> wrapper) {
        return getBySpecification(wrapper);
    }

    /**
     * Description: <br>
     *
     * @param <M>
     * @param wrapper
     * @param clazz
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public <M> M get(final QueryWrapper<T> wrapper, final Class<M> clazz) {
        return getBySpecification(wrapper, clazz);
    }

    /**
     * Description: <br>
     *
     * @param id
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public T get(final Serializable id) {
        Field field = MongoBaseDaoUtil.findPrimaryKeyField(getEntityClazz());
        if (null == field) {
            throw new IllegalStateException("Entity does not have an @Id annotated field.");
        }
        String key = field.getAnnotation(Column.class) != null ? "_id" : field.getName();
        // 获取文档名称（集合名称）
        String collectionName = MongoBaseDaoUtil.getTableName(entityClazz);
        Query query = new Query(Criteria.where(key).is(id));

        Map one = getMongoTemplate().findOne(query, Map.class, collectionName);

        Map<String, Object> columnMapping = MongoBaseDaoUtil.parseEntityName(entityClazz, false);
        T t = queryMapToEntity(one, entityClazz, columnMapping);
        return t;
    }

    /**
     * Description: <br>
     *
     * @param <M>
     * @param specification
     * @param clazz
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public <M> M getByLambda(final LambdaQuerySpecification<T, M> specification, final Class<M> clazz) {
        return getBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()), clazz);
    }

    /**
     * Description: <br>
     *
     * @param specification
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public T getByLambda(final LambdaQuerySpecification<T, T> specification) {
        return getBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()));
    }

    /**
     * Description: <br>
     *
     * @param <M>
     * @param wrapper
     * @param clazz
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public <M> M getByLambda(final LambdaQueryWrapper<T, M> wrapper, final Class<M> clazz) {
        return getBySpecification(wrapper, clazz);
    }

    /**
     * Description: <br>
     *
     * @param wrapper
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public T getByLambda(final LambdaQueryWrapper<T, T> wrapper) {
        return getBySpecification(wrapper);
    }

    /**
     * @param specification
     * @return T
     */
    public T getBySpecification(final AbstractQueryWrapper<T> specification) {
        return getBySpecification(specification, entityClazz);
    }

    /**
     * @param specification
     * @param clazz
     * @param <M>
     * @return <M> M
     */
    public <M> M getBySpecification(final AbstractQueryWrapper<T> specification, final Class<M> clazz) {
        Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
        if (!entityClazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }
        Assert.notNull(specification, ErrorCodeDef.PARAM_NOT_NULL, "查询条件");
        Map<String, Object> parseEntityNameMap = MongoBaseDaoUtil.parseEntityName(entityClazz, true);
        Aggregation aggregation = toQueryPredicate(parseEntityNameMap, specification, null, null);
        // 获取文档名称（集合名称）
        String collectionName = MongoBaseDaoUtil.getTableName(entityClazz);
        if (MongoBaseDaoUtil.SCALAR_TYPES.contains(clazz)) {
            // 执行 sql
            AggregationResults<Map> results = getMongoTemplate().aggregate(aggregation, collectionName, Map.class);
            Assert.isTrue(Objects.nonNull(results) && Objects.nonNull(results.getMappedResults()),
                ErrorCodeDef.EXECUTE_ERROR);
            // 基本数据类型如计算等 操作 查询不到的时候结果是null 返回基本数据类型默认值
            if (results.getMappedResults().isEmpty() && NUMBER_DEFAULT.keySet().contains(clazz)) {
                return (M) NUMBER_DEFAULT.get(clazz);
            }
            for (Map<String, Object> result : results.getMappedResults()) {
                // 假设specification中指定了要查询的字段名
                String targetField = specification.getSelectionList().get(0).getField(); // 需要在specification中定义此方法
                return (M) result.get(targetField);
            }
        }
        AggregationResults<Map> results = getMongoTemplate().aggregate(aggregation, collectionName, Map.class);
        Assert.isTrue(Objects.nonNull(results) && Objects.nonNull(results.getMappedResults()),
            ErrorCodeDef.EXECUTE_ERROR);
        // 解析实体类，获取字段映射关系
        Map<String, Object> columnMapping = MongoBaseDaoUtil.parseEntityName(clazz, false);
        T t = queryMapToEntity(results.getMappedResults().stream().findFirst().get(), (Class<T>) clazz, columnMapping);
        return (M) t;
    }

    /**
     * Description: <br>
     *
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
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
     * @param specification
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public List<T> query(final QuerySpecification<T> specification) {
        return queryBySpecification(specification.toSpecification(new QueryWrapper<>()));
    }

    /**
     * Description: <br>
     *
     * @param <M>
     * @param specification
     * @param clazz
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public <M> List<M> query(final QuerySpecification<T> specification, final Class<M> clazz) {
        return queryBySpecification(specification.toSpecification(new QueryWrapper<>()), clazz);
    }

    /**
     * Description: <br>
     *
     * @param wrapper
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public List<T> query(final QueryWrapper<T> wrapper) {
        return queryBySpecification(wrapper);
    }

    /**
     * Description: <br>
     *
     * @param <M>
     * @param wrapper
     * @param clazz
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public <M> List<M> query(final QueryWrapper<T> wrapper, final Class<M> clazz) {
        return queryBySpecification(wrapper, clazz);
    }

    /**
     * Description: <br>
     *
     * @param mongodbSql
     * @param param
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public Object query(final String mongodbSql, final DataParam param) {
        try {
            SQlCheckUtil.checkSql(mongodbSql);

            // Redis缓存序列化时不能有void返回类型，特殊处理一下
            if (param.getReturnType() == null) { // @sql返回值类型
                param.setReturnType(void.class);
            }

            Map<String, Object> dataMap = param.getParamMap();
            MongoTemplate mongoTemplate = getMongoTemplate();
            // 将dataMap中的参数设置到mongodbSql中对应的占位符上
            Document commandDocument = Document.parse(mongodbSql);
            MongoBaseDaoUtil.replacePlaceholders(commandDocument, dataMap);

            // 处理分页逻辑
            if (dataMap.containsKey(DaoConstants.PAGE_INDEX)) {
                int pageIndex = Integer.parseInt(dataMap.get(DaoConstants.PAGE_INDEX).toString());
                if (pageIndex <= 0) {
                    pageIndex = 1;
                }
                int pageSize = dataMap.containsKey(DaoConstants.PAGE_SIZE)
                    ? Integer.parseInt(dataMap.get(DaoConstants.PAGE_SIZE).toString())
                    : PAGE_SIZE;
                if (pageSize <= 0 || pageSize > MAX_SIZE) {
                    pageSize = PAGE_SIZE;
                }
                // 首先查询总记录数
                Document countCommand = new Document(commandDocument);
                Document document = MongoBaseDaoUtil.prepareCountCommand(countCommand); // 修改命令为统计数量
                Document countResult = mongoTemplate.getDb().runCommand(document);
                List<Document> documents = countResult.get("cursor", Document.class).getList("firstBatch",
                    Document.class);

                // 执行分页查询
                long totalDocuments = CollectionUtils.isEmpty(documents) ? 0 : documents.get(0).getInteger("total");
                Document resultDocument = mongoTemplate.getDb().runCommand(commandDocument);
                // 提取查询结果列表
                List<Document> list1 = resultDocument.get("cursor", Document.class).getList("firstBatch",
                    Document.class);
                List<Object> resultList = new ArrayList<>();
                list1 = CollectionUtils.isEmpty(list1) ? new ArrayList<>() : list1;
                for (Document document1 : list1) {
                    Object entity = MongoBaseDaoUtil.mapToEntity(document1, param.getBeanType());
                    resultList.add(entity);
                }
                PagerList list = new PagerList();
                list.setPageIndex(pageIndex);
                list.setPageSize(pageSize);
                list.setTotalCount(totalDocuments);
                list.addAll(resultList);

                return list;
            }
            else {
                Document document = mongoTemplate.getDb().runCommand(commandDocument);
                // 根据param.getReturnType()转换结果
                Class<?> returnType = param.getReturnType();
                Class<?> beanType = param.getBeanType();
                if (returnType == void.class && beanType == void.class) {
                    return null; // 或者按照业务需要处理
                }
                else if (returnType == void.class && Serializable.class.isAssignableFrom(beanType)) {
                    List<Document> list = document.get("cursor", Document.class).getList("firstBatch", Document.class);
                    // 基本数据类型 就获取 第一个对象的 value值
                    if (MongoBaseDaoUtil.SCALAR_TYPES.contains(beanType)) {
                        if (CollectionUtils.isEmpty(list)) {
                            return NUMBER_DEFAULT.get(beanType);
                        }
                        return list.get(0).values().iterator().next();
                    }
                    // 其他类型 ，默认是 引用数据类型
                    if (CollectionUtils.isEmpty(list)) {
                        return null;
                    }
                    Object entity = MongoBaseDaoUtil.mapToEntity(list.get(0), beanType);
                    return entity;
                }
                else if (List.class.isAssignableFrom(returnType) && Serializable.class.isAssignableFrom(beanType)) {
                    // 返回的是list集合
                    List<Document> documents = document.get("cursor", Document.class).getList("firstBatch",
                        Document.class);
                    List<Object> resultList = new ArrayList<>();
                    for (Document document1 : documents) {
                        Object entity = MongoBaseDaoUtil.mapToEntity(document1, beanType);
                        resultList.add(entity);
                    }
                    return resultList;
                }
                else {
                    // 其他的 就返回 数组 集合自行处理
                    return document.get("cursor", Document.class).getList("firstBatch", Document.class);
                }
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DaoException(ErrorCodeDef.QUERY_ERROR, e);
        }
    }

    /**
     * Description: <br>
     *
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public List<T> queryAll() {
        Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
        // 创建一个空的 Query 对象，这将匹配集合中的所有文档
        Query query = new Query();
        // 获取文档名称（集合名称）
        String collectionName = MongoBaseDaoUtil.getTableName(entityClazz);
        // 执行查询并返回结果列表
        List<Map> objects = getMongoTemplate().find(query, Map.class, collectionName);

        Map<String, Object> columnMapping = MongoBaseDaoUtil.parseEntityName(entityClazz, false);
        List<T> resultList = objects.stream().map(result -> queryMapToEntity(result, entityClazz, columnMapping))
            .collect(Collectors.toList());
        return resultList;
    }

    /**
     * Description: <br>
     *
     * @param <M>
     * @param specification
     * @param clazz
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public <M> List<M> queryByLambda(final LambdaQuerySpecification<T, M> specification, final Class<M> clazz) {
        return queryBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()), clazz);
    }

    /**
     * Description: <br>
     *
     * @param specification
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public List<T> queryByLambda(final LambdaQuerySpecification<T, T> specification) {
        return queryBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()));
    }

    /**
     * Description: <br>
     *
     * @param <M>
     * @param wrapper
     * @param clazz
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public <M> List<M> queryByLambda(final LambdaQueryWrapper<T, M> wrapper, final Class<M> clazz) {
        return queryBySpecification(wrapper, clazz);
    }

    /**
     * Description: <br>
     *
     * @param wrapper
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public List<T> queryByLambda(final LambdaQueryWrapper<T, T> wrapper) {
        return queryBySpecification(wrapper);
    }

    /**
     * @param wrapper
     * @param clazz
     * @param <M>
     * @return <M> List<M>
     */
    public <M> List<M> queryBySpecification(final AbstractQueryWrapper<T> wrapper, final Class<M> clazz) {
        Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
        if (!entityClazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }
        Assert.notNull(wrapper, ErrorCodeDef.PARAM_NOT_NULL, "查询条件");
        Map<String, Object> parseEntityNameMap = MongoBaseDaoUtil.parseEntityName(entityClazz, true);

        Aggregation queryPredicate = toQueryPredicate(parseEntityNameMap, wrapper, null, null);
        // 获取文档名称（集合名称）
        String collectionName = MongoBaseDaoUtil.getTableName(entityClazz);
        // 使用 MongoTemplate 执行聚合查询，并将结果映射到指定的类
        List<Map> results = getMongoTemplate().aggregate(queryPredicate, collectionName, Map.class).getMappedResults();

        Map<String, Object> columnMapping = MongoBaseDaoUtil.parseEntityName(clazz, false);
        List<M> resultList = (List<M>) results.stream()
            .map(result -> queryMapToEntity(result, (Class<T>) clazz, columnMapping)).collect(Collectors.toList());
        return resultList;
    }

    /**
     * Description: <br>
     *
     * @param specification
     * @param pageIndex
     * @param pageSize
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public PagerList<T> queryPager(final QuerySpecification<T> specification, final int pageIndex, final int pageSize) {
        return queryPagerBySpecification(specification.toSpecification(new QueryWrapper<>()), pageIndex, pageSize);
    }

    /**
     * Description: <br>
     *
     * @param <M>
     * @param specification
     * @param pageIndex
     * @param pageSize
     * @param clazz
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public <M> PagerList<M> queryPager(final QuerySpecification<T> specification, final int pageIndex,
        final int pageSize, final Class<M> clazz) {
        return queryPagerBySpecification(specification.toSpecification(new QueryWrapper<>()), pageIndex, pageSize,
            clazz);
    }

    /**
     * Description: <br>
     *
     * @param wrapper
     * @param pageIndex
     * @param pageSize
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public PagerList<T> queryPager(final QueryWrapper<T> wrapper, final int pageIndex, final int pageSize) {
        return queryPagerBySpecification(wrapper, pageIndex, pageSize);
    }

    /**
     * Description: <br>
     *
     * @param <M>
     * @param wrapper
     * @param pageIndex
     * @param pageSize
     * @param clazz
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public <M> PagerList<M> queryPager(final QueryWrapper<T> wrapper, final int pageIndex, final int pageSize,
        final Class<M> clazz) {
        return queryPagerBySpecification(wrapper, pageIndex, pageSize, clazz);
    }

    /**
     * Description: <br>
     *
     * @param <M>
     * @param specification
     * @param pageIndex
     * @param pageSize
     * @param clazz
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public <M> PagerList<M> queryPagerByLambda(final LambdaQuerySpecification<T, M> specification, final int pageIndex,
        final int pageSize, final Class<M> clazz) {
        return queryPagerBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()), pageIndex, pageSize,
            clazz);
    }

    /**
     * Description: <br>
     *
     * @param specification
     * @param pageIndex
     * @param pageSize
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public PagerList<T> queryPagerByLambda(final LambdaQuerySpecification<T, T> specification, final int pageIndex,
        final int pageSize) {
        return queryPagerBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()), pageIndex,
            pageSize);
    }

    /**
     * Description: <br>
     *
     * @param <M>
     * @param wrapper
     * @param pageIndex
     * @param pageSize
     * @param clazz
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public <M> PagerList<M> queryPagerByLambda(final LambdaQueryWrapper<T, M> wrapper, final int pageIndex,
        final int pageSize, final Class<M> clazz) {
        return queryPagerBySpecification(wrapper, pageIndex, pageSize, clazz);
    }

    /**
     * Description: <br>
     *
     * @param wrapper
     * @param pageIndex
     * @param pageSize
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public PagerList<T> queryPagerByLambda(final LambdaQueryWrapper<T, T> wrapper, final int pageIndex,
        final int pageSize) {
        return queryPagerBySpecification(wrapper, pageIndex, pageSize);
    }

    /**
     * Description: <br>
     *
     * @param entity <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public void save(final T entity) {
        Map<String, Object> columnValues = parseEntity(entity, false);
        if (MapUtils.isEmpty(columnValues)) {
            throw new DaoException(ErrorCodeDef.ANALYSIS_ENTITY_ERROR);
        }
        // 删除主键标识
        columnValues.remove("ENTITY_ID");
        // 获取文档名称（集合名称）
        String collectionName = MongoBaseDaoUtil.getTableName(entity.getClass());
        // 将实体对象转换为 MongoDB 文档
        Document document = new Document(columnValues);
        getMongoTemplate().insert(document, collectionName);
    }

    /**
     * Description: <br>
     *
     * @param entitys <br>
     * @author 王伟<br>
     * @taskId <br>
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
        // 获取集合名称
        String collectionName = MongoBaseDaoUtil.getTableName(entitys.get(0).getClass());
        // 将实体列表转换为 MongoDB 文档列表
        List<Document> documents = new ArrayList<>();
        for (T entity : entitys) {
            Map<String, Object> columnValues = parseEntity(entity, false); // 解析实体类
            if (MapUtils.isEmpty(columnValues)) {
                continue;
            }
            // 删除主键标识
            columnValues.remove("ENTITY_ID");
            documents.add(new Document(columnValues));
        }
        // 构建查询条件，这里假设每个实体都有一个 getId 方法返回其唯一标识符
        getMongoTemplate().insert(documents, collectionName);
    }

    /**
     * Description: <br>
     *
     * @param entityClazz <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public void setEntityClazz(final Class<?> entityClazz) {
        this.entityClazz = (Class<T>) entityClazz;
    }

    /**
     * Description: <br>
     *
     * @param pojo <br>
     * @author 王伟<br>
     * @taskId <br>
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
        // 获取集合名称
        String collectionName = MongoBaseDaoUtil.getTableName(pojo.getClass());
        // 执行更新操作
        getMongoTemplate().updateFirst(query, update, collectionName);

    }

    /**
     * Description: <br>
     *
     * @param specification <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public void update(final UpdateSpecification<T> specification) {
        updateBySpecification(specification.toSpecification(new UpdateWrapper<>()));
    }

    /**
     * Description: <br>
     *
     * @param wrapper <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public void update(final UpdateWrapper<T> wrapper) {
        updateBySpecification(wrapper);
    }

    /**
     * Description: <br>
     *
     * @param entitys <br>
     * @author 王伟<br>
     * @taskId <br>
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
     * @param specification <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public void updateByLambda(final LambdaUpdateSpecification<T> specification) {
        updateBySpecification(specification.toSpecification(new LambdaUpdateWrapper<>()));
    }

    /**
     * Description: <br>
     *
     * @param wrapper <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public void updateByLambda(final LambdaUpdateWrapper<T> wrapper) {
        updateBySpecification(wrapper);
    }

    private Query buildQueryFromColumnValues(final Map<String, Object> columnValues) {
        Query query = new Query();
        for (Map.Entry<String, Object> entry : columnValues.entrySet()) {
            if (StringUtils.equals("ENTITY_ID", entry.getKey())) {
                continue;
            }
            query.addCriteria(Criteria.where(entry.getKey()).is(entry.getValue()));
        }
        return query;
    }

    private void deleteBySpecification(final AbstractWrapper<?> wrapper) {
        Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
        if (!entityClazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }
        // 获取文档名称（集合名称）
        String collectionName = MongoBaseDaoUtil.getTableName(entityClazz);
        Map<String, Object> parseEntityNameMap = MongoBaseDaoUtil.parseEntityName(entityClazz, true);
        Query query = new Query();
        Criteria criteria = toPredicate(wrapper, parseEntityNameMap);
        // 如果没有条件直接抛出异常
        Assert.notNull(criteria, ErrorCodeDef.PARAM_NOT_NULL, "删除的条件");
        query.addCriteria(criteria);
        // 执行 sql
        getMongoTemplate().remove(query, collectionName);
    }

    private Map<String, Object> parseEntity(final T entity, final boolean continueEntity) {
        // 解析实体类，提取带有@Column注解的字段及其值
        Map<String, Object> columnValues = new HashMap<>();
        try {
            for (Field field : entity.getClass().getDeclaredFields()) {
                Column columnAnnotation = field.getAnnotation(Column.class);
                if (columnAnnotation != null) {
                    field.setAccessible(true); // 取消Java的访问控制检查
                    if (field.isAnnotationPresent(Id.class)) {
                        columnValues.put("ENTITY_ID", "_id");
                        columnValues.put("_id", field.get(entity));
                        continue;
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

    private List<T> queryBySpecification(final AbstractQueryWrapper<T> specification) {
        return queryBySpecification(specification, entityClazz);
    }

    private T queryMapToEntity(final Map object, final Class<T> queryEntityClazz,
        final Map<String, Object> columnMapping) {
        try {
            if (object == null || object.size() == 0) {
                return null;
            }
            // 创建实体类实例
            T entity = queryEntityClazz.getDeclaredConstructor().newInstance();
            for (Object keyObj : object.keySet()) {
                String mongoFieldName = (String) keyObj;
                Object mongoFieldValue = object.get(mongoFieldName);

                // 检查是否存在 @Column 注解映射
                String fieldName = (String) columnMapping.get(mongoFieldName);
                if (fieldName == null) {
                    // 如果没有 @Column 注解，转换字段名格式
                    fieldName = MongoBaseDaoUtil.convertFieldName(mongoFieldName);
                }

                // 设置字段值
                Field field = Objects.isNull(queryEntityClazz.getDeclaredField(fieldName))
                    ? queryEntityClazz.getSuperclass().getDeclaredField(fieldName)
                    : queryEntityClazz.getDeclaredField(fieldName);
                if (List.class.isAssignableFrom(field.getType())) {
                    List<?> mongoList;
                    if (!(mongoFieldValue instanceof List)) {
                        mongoList = Collections.singletonList(mongoFieldValue); // 将单个值包装成 List
                    }
                    else {
                        mongoList = (List<?>) mongoFieldValue;
                    }
                    ParameterizedType listType = (ParameterizedType) field.getGenericType();
                    Class<?> listItemType = (Class<?>) listType.getActualTypeArguments()[0]; // 获取 List 的泛型类型
                    List<Object> resultList = new ArrayList<>();
                    for (Object item : mongoList) {
                        if (MongoBaseDaoUtil.SCALAR_TYPES.contains(listItemType)) {
                            // 如果是基本数据类型，直接添加到结果列表
                            resultList.add(item);
                        }
                        else {
                            // 如果是复杂类型，递归解析
                            Object nestedEntity = queryMapToEntity((Map<String, Object>) item, (Class<T>) listItemType,
                                new HashMap<>());
                            resultList.add(nestedEntity);
                        }
                    }
                    field.set(entity, resultList);
                }
                else if (Map.class.isAssignableFrom(field.getType())) {
                    Map<String, Object> mongoMap;
                    if (!(mongoFieldValue instanceof Map)) {
                        mongoMap = JSONObject.parseObject((String) mongoFieldValue, Map.class);
                    }
                    else {
                        mongoMap = (Map<String, Object>) mongoFieldValue;
                    }
                    ParameterizedType mapType = (ParameterizedType) field.getGenericType();
                    Class<?> valueType = (Class<?>) mapType.getActualTypeArguments()[1]; // 获取 Map 的 Value 泛型类型
                    Map<String, Object> resultMap = new HashMap<>();
                    for (Map.Entry<String, Object> entry : mongoMap.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();

                        if (MongoBaseDaoUtil.SCALAR_TYPES.contains(valueType)) {
                            // 如果是基本数据类型，直接放入结果 Map
                            resultMap.put(key, value);
                        }
                        else {
                            // 如果是复杂类型，递归解析
                            Object nestedEntity = queryMapToEntity((Map<String, Object>) value, (Class<T>) valueType,
                                new HashMap<>());
                            resultMap.put(key, nestedEntity);
                        }
                    }
                    field.set(entity, resultMap);
                }
                else if (!MongoBaseDaoUtil.SCALAR_TYPES.contains(field.getType())) {
                    // 如果字段值是对象或者map，就默认转map
                    Object nestedEntity = queryMapToEntity((Map<String, Object>) mongoFieldValue,
                        (Class<T>) field.getType(), columnMapping);
                    field.set(entity, nestedEntity);
                }
                else {
                    field.setAccessible(true);
                    field.set(entity, "_id".equals(mongoFieldName) ? mongoFieldValue.toString() : mongoFieldValue);
                }
            }
            return entity;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DaoException(DAO_PROPERTY_IS_EMPTY);
        }
    }

    private PagerList<T> queryPagerBySpecification(final AbstractQueryWrapper<T> specification, final int pageIndex,
        final int pageSize) {
        return queryPagerBySpecification(specification, pageIndex, pageSize, entityClazz);
    }

    private <M> PagerList<M> queryPagerBySpecification(final AbstractQueryWrapper<T> wrapper, final int pi,
        final int ps, final Class<M> clazz) {
        Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
        if (!entityClazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }
        Assert.notNull(wrapper, ErrorCodeDef.PARAM_NOT_NULL, "查询条件");
        Map<String, Object> parseEntityNameMap = MongoBaseDaoUtil.parseEntityName(entityClazz, true);
        int pageSize = ps < 1 ? PAGE_SIZE : ps;
        int pageIndex = pi < 1 ? 1 : pi;
        Aggregation queryPredicate = toQueryPredicate(parseEntityNameMap, wrapper, pageIndex, pageSize);
        // 获取文档名称（集合名称）
        String collectionName = MongoBaseDaoUtil.getTableName(entityClazz);
        // 使用 MongoTemplate 执行聚合查询，并将结果映射到指定的类
        List<Map> results = getMongoTemplate().aggregate(queryPredicate, collectionName, Map.class).getMappedResults();

        Map<String, Object> columnMapping = MongoBaseDaoUtil.parseEntityName(clazz, false);
        List<M> resultList = (List<M>) results.stream()
            .map(result -> queryMapToEntity(result, (Class<T>) clazz, columnMapping)).collect(Collectors.toList());

        TypedAggregation<T> countQueryPredicate = toCountQueryPredicate(parseEntityNameMap, wrapper, (Class<T>) clazz);
        AggregationResults<BasicDBObject> countResults = getMongoTemplate().aggregate(countQueryPredicate,
            collectionName, BasicDBObject.class);
        BasicDBObject countResult = countResults.getMappedResults().stream().findFirst().orElse(new BasicDBObject());
        long totalCount = countResult.getLong("total", 0L);

        // 创建并返回 PagerList 对象
        PagerList<M> pagerList = new PagerList<>();
        pagerList.setPageIndex(pi);
        pagerList.setPageSize(pageSize);
        pagerList.setTotalCount(totalCount);
        pagerList.addAll(resultList);
        return pagerList;
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

    private Criteria toPredicate(final AbstractWrapper<?> wrapper, final Map<String, Object> parseEntityNameMap) {
        // 检查是否有任何条件
        boolean hasConditions = CollectionUtils.isNotEmpty(wrapper.getTempPredicates())
            || CollectionUtils.isNotEmpty(wrapper.getOrTempPredicates());
        // 如果没有条件和order by 直接返回
        if (!hasConditions) {
            return null;
        }

        // 组装 AND 条件
        List<Criteria> andCriteriaList = wrapper.getTempPredicates().stream().filter(tempPredicate -> {
            // 分割字段名称以支持嵌套字段
            String[] fieldParts = tempPredicate.getFieldName().split("\\.", 2);
            return Objects.nonNull(parseEntityNameMap.get(fieldParts[0]));
        }).map(tempPredicate -> MongoBaseDaoUtil.toPredicate(parseEntityNameMap, tempPredicate))
            .collect(Collectors.toList());

        // 组装 OR 条件
        // 只处理非空的OR条件组
        List<Criteria> orCriteriaList = wrapper.getOrTempPredicates().stream().filter(CollectionUtils::isNotEmpty)
            .map(orGroup -> orGroup.stream()
                .filter(tempPredicate -> Objects.nonNull(parseEntityNameMap.get(tempPredicate.getFieldName())))
                .map(tempPredicate -> MongoBaseDaoUtil.toPredicate(parseEntityNameMap, tempPredicate))
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
            if (andCriteriaList.size() == 1) {
                criteria = andCriteriaList.get(0);
            }
            else {
                criteria.andOperator(andCriteriaList.toArray(new Criteria[0]));
            }
        }

        if (CollectionUtils.isNotEmpty(orCriteriaList)) {
            if (orCriteriaList.size() == 1) {
                if (criteria != null) {
                    // 如果已经有AND条件，需要确保正确地合并AND和OR条件
                    criteria = new Criteria().andOperator(criteria).orOperator(orCriteriaList.get(0));
                }
                else {
                    criteria = orCriteriaList.get(0);
                }
            }
            else {
                if (criteria != null) {
                    criteria.orOperator(orCriteriaList.toArray(new Criteria[0]));
                }
                else {
                    criteria = new Criteria().orOperator(orCriteriaList.toArray(new Criteria[0]));
                }
            }
        }

        return criteria;

    }

    private Aggregation toQueryPredicate(final Map<String, Object> parseEntityNameMap,
        final AbstractQueryWrapper<T> specification, final Integer pi, final Integer pageSize) {
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
        if (specification.getGroupList().isEmpty()) {
            if (specification != null && specification.getSelectionList() != null
                && !specification.getSelectionList().isEmpty()) {
                // 分离普通字段和运算字段
                List<TempSelection> regularFields = new ArrayList<>();
                List<TempSelection> aggregationFields = new ArrayList<>();
                // 需要group的类型
                List<Operator> aggregationOperators = Arrays.asList(Operator.AVG, Operator.COUNT, Operator.MAX,
                    Operator.MIN, Operator.SUM, Operator.SUMMING);
                for (TempSelection selection : specification.getSelectionList()) {
                    if (aggregationOperators.contains(selection.getOperator())) {
                        aggregationFields.add(selection);
                    }
                    else {
                        regularFields.add(selection);
                    }
                }
                // 处理普通字段
                if (CollectionUtils.isNotEmpty(regularFields)) {
                    ProjectionOperation projectOperation = Aggregation.project();
                    for (TempSelection selection : regularFields) {
                        projectOperation = MongoBaseDaoUtil.toSelection(projectOperation, parseEntityNameMap,
                            selection);
                    }
                    operations.add(projectOperation);
                }
                // 处理运算字段
                if (CollectionUtils.isNotEmpty(aggregationFields)) {
                    MongoBaseDaoUtil.handleAggregationOperations(operations, aggregationFields, parseEntityNameMap);
                }
            }
            else {
                // 默认选择所有字段
                operations.add(Aggregation.project().andInclude(parseEntityNameMap.values().toArray(new String[0])));
            }
        }
        // 或者使用 ObjectMapper 转换为 JSON 字符串
        if (!specification.getGroupList().isEmpty()) {
            // 获取分组字段列表，并转换为需要的形式
            List<String> groupFields = specification.getGroupList().stream()
                .map(group -> parseEntityNameMap.getOrDefault(group, group).toString()).collect(Collectors.toList());

            GroupOperation groupOperation = Aggregation.group(groupFields.toArray(new String[0]));
            Map<String, String> aliasMap = new HashMap<>();
            if (specification.getSelectionList() != null && !specification.getSelectionList().isEmpty()) {
                for (TempSelection selectedField : specification.getSelectionList()) {
                    groupOperation = MongoBaseDaoUtil.groupToSelection(groupOperation, parseEntityNameMap,
                        selectedField, aliasMap);
                }
            }
            else {
                for (Object field : parseEntityNameMap.values()) {
                    String fieldName = field.toString();
                    if (!groupFields.contains(fieldName)) { // 不对分组字段应用 $last 操作
                        groupOperation = groupOperation.last(fieldName)
                            .as("_id".equals(fieldName) ? "collect" + fieldName : fieldName);
                    }
                }
            }

            // 创建 Project 操作来重构输出
            ProjectionOperation projectOperation = Aggregation.project();
            // 首先排除默认的_id字段
            projectOperation = projectOperation.andExclude("_id");

            // 提取分组字段
            if (groupFields.size() == 1) {
                String alia = StringUtils.isBlank(aliasMap.get(groupFields.get(0))) ? groupFields.get(0)
                    : aliasMap.get(groupFields.get(0));
                projectOperation = projectOperation.and("_id").as(alia);
            }
            else {
                for (String groupField : groupFields) {
                    projectOperation = projectOperation.and("_id." + groupField).as(groupField);
                }
            }

            // 添加其他非分组字段和计算字段
            if (specification.getSelectionList() != null && !specification.getSelectionList().isEmpty()) {
                for (TempSelection selectedField : specification.getSelectionList()) {
                    String fieldName = parseEntityNameMap
                        .getOrDefault(selectedField.getField(), selectedField.getField()).toString();
                    if (!groupFields.contains(fieldName)) { // 排除分组字段
                        fieldName = "id".equals(fieldName) ? "_" + fieldName : fieldName;
                        String alia = StringUtils.isBlank(aliasMap.get(fieldName)) ? fieldName
                            : aliasMap.get(fieldName);
                        // 如果是计算字段，则直接使用其别名（已在groupToSelection方法中处理）
                        projectOperation = projectOperation.andInclude(alia);
                    }
                }
            }
            else {
                for (Object field : parseEntityNameMap.values()) {
                    String fieldName = field.toString();
                    if (!groupFields.contains(fieldName) && !"_id".equals(fieldName)) { // 排除分组字段
                        projectOperation = projectOperation.andInclude(fieldName);
                    }
                }
                // 确保所有计算字段也被包含进来
                for (TempSelection selectedField : specification.getSelectionList()) {
                    String fieldName = parseEntityNameMap
                        .getOrDefault(selectedField.getField(), selectedField.getField()).toString();
                    String alia = StringUtils.isBlank(aliasMap.get(fieldName)) ? fieldName : aliasMap.get(fieldName);
                    projectOperation = projectOperation.and(fieldName).as(alia);
                }
            }
            projectOperation = projectOperation.and("collect_id").as("id");

            // 添加 $group 和 $project 阶段到聚合操作中
            operations.add(groupOperation);
            operations.add(projectOperation);
        }
        // 构建 ORDER BY 子句
        if (!specification.getOrderByList().isEmpty()) {
            SortOperation sortOperation = Aggregation.sort(specification.getOrderByList().stream()
                .map(order -> Sort.by(order.isDesc() ? Sort.Direction.DESC : Sort.Direction.ASC,
                    parseEntityNameMap.getOrDefault(order.getProperty(), order.getProperty()).toString()))
                .reduce(Sort::and) // 当存在多个排序条件 汇合成一个最终的排序规则
                .orElse(Sort.unsorted()) // 不存在 返回一个空排序规则
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
        return Aggregation.newAggregation(operations);
    }

    private void updateBySpecification(final LambdaUpdateWrapper<?> wrapper) {

        Assert.notEmpty(wrapper.getValueMap(), ErrorCodeDef.PARAM_NOT_NULL, "修改的内容");
        Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
        if (!entityClazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }
        // 获取 解析实体类，提取带有@Column注解的字段及属性名称
        Map<String, Object> parseEntityNameMap = MongoBaseDaoUtil.parseEntityName(entityClazz, true);
        // 创建更新对象
        Update update = new Update();
        for (Map.Entry<String, Object> entry : wrapper.getValueMap().entrySet()) {
            String fieldName = parseEntityNameMap.getOrDefault(entry.getKey(), entry.getKey()).toString();
            update.set(fieldName, entry.getValue());
        }
        Criteria criteria = toPredicate(wrapper, parseEntityNameMap);
        // 如果没有条件直接抛出异常
        Assert.isTrue(criteria != null && !criteria.getCriteriaObject().isEmpty(), ErrorCodeDef.PARAM_NOT_NULL,
            "修改的条件");
        // 执行 sql
        Query query = new Query(criteria);
        // 获取集合名称
        String collectionName = MongoBaseDaoUtil.getTableName(entityClazz);
        // 执行更新操作
        getMongoTemplate().updateMulti(query, update, collectionName);
    }

    private void updateBySpecification(final UpdateWrapper<T> wrapper) {

        Assert.notEmpty(wrapper.getValueMap(), ErrorCodeDef.PARAM_NOT_NULL, "修改的内容");
        Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
        if (!entityClazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }
        // 获取 解析实体类，提取带有@Column注解的字段及属性名称
        Map<String, Object> parseEntityNameMap = MongoBaseDaoUtil.parseEntityName(entityClazz, true);
        // 创建更新对象
        Update update = new Update();
        for (Map.Entry<String, Object> entry : wrapper.getValueMap().entrySet()) {
            String fieldName = parseEntityNameMap.getOrDefault(entry.getKey(), entry.getKey()).toString();
            update.set(fieldName, entry.getValue());
        }

        Criteria criteria = toPredicate(wrapper, parseEntityNameMap);

        // 如果没有条件直接抛出异常
        Assert.isTrue(criteria != null && !criteria.getCriteriaObject().isEmpty(), ErrorCodeDef.PARAM_NOT_NULL,
            "修改的条件");
        // 执行 sql
        Query query = new Query(criteria);
        // 获取集合名称
        String collectionName = MongoBaseDaoUtil.getTableName(entityClazz);
        // 执行更新操作
        getMongoTemplate().updateMulti(query, update, collectionName);
    }
}
