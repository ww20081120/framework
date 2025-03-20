/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.mongo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
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
import com.hbasesoft.framework.db.core.BaseEntity;
import com.hbasesoft.framework.db.core.DaoConstants;
import com.hbasesoft.framework.db.core.criteria.Operator;
import org.bson.Document;
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
import com.hbasesoft.framework.db.core.utils.SQlCheckUtil;
import com.mongodb.BasicDBObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
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

import static com.hbasesoft.framework.common.ErrorCodeDef.DAO_PROPERTY_IS_EMPTY;


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
     *
     */
    private static final List<Class<?>> SCALAR_TYPES = Arrays.asList(
            Integer.class, int.class,
            Long.class, long.class,
            Double.class, double.class,
            Float.class, float.class,
            Short.class, short.class,
            Byte.class, byte.class,
            Boolean.class, boolean.class,
            String.class,
            BigInteger.class,
            BigDecimal.class
    );

    /**
     *
     */
    public static final Map<Class<?>, Object> NUMBER_DEFAULT = new HashMap<Class<?>, Object>() {{
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
    }};


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
            replacePlaceholders(commandDocument, dataMap);


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
                Document document = prepareCountCommand(countCommand); // 修改命令为统计数量
                Document countResult = mongoTemplate.getDb().runCommand(document);
                List<Document> documents = countResult.get("cursor", Document.class)
                        .getList("firstBatch", Document.class);
                long totalDocuments = CollectionUtils.isEmpty(documents)
                        ? 0 : documents.get(0).getInteger("total"); // 执行分页查询
                Document resultDocument = mongoTemplate.getDb().runCommand(commandDocument);
                // 提取查询结果列表
                List<Document> list1 = resultDocument.get("cursor", Document.class)
                        .getList("firstBatch", Document.class);
                List<Object> resultList = new ArrayList<>();
                list1 = CollectionUtils.isEmpty(list1) ? new ArrayList<>() : list1;
                for (Document document1 : list1) {
                    Object entity = mapToEntity(document1, param.getBeanType());
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
                    //基本数据类型 就获取 第一个对象的 value值
                    if (SCALAR_TYPES.contains(beanType)) {
                        if (CollectionUtils.isEmpty(list)) {
                            return NUMBER_DEFAULT.get(beanType);
                        }
                        return list.get(0).values().iterator().next();
                    }
                    //其他类型 ，默认是 引用数据类型
                    if (CollectionUtils.isEmpty(list)) {
                        return null;
                    }
                    Object entity = mapToEntity(list.get(0), beanType);
                    return entity;
                }
                else if (List.class.isAssignableFrom(returnType) && Serializable.class.isAssignableFrom(beanType)) {
                    // 返回的是list集合
                    List<Document> documents = document.get("cursor", Document.class)
                            .getList("firstBatch", Document.class);
                    List<Object> resultList = new ArrayList<>();
                    for (Document document1 : documents) {
                        Object entity = mapToEntity(document1, beanType);
                        resultList.add(entity);
                    }
                    return resultList;
                }
                else {
                    //其他的 就返回 数组 集合自行处理
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
     * 修改传入的 countCommand 对象，移除 $skip 和 $limit 操作，并添加计数阶段。
     * @param countCommand 输入的 MongoDB 命令文档
     * @return 修改后的 MongoDB 命令文档
     */
    public static Document prepareCountCommand(final Document countCommand) {
        // 获取聚合管道
        List<Document> pipeline = countCommand.getList("pipeline", Document.class);

        if (pipeline == null) {
            pipeline = new ArrayList<>();
        }

        // 创建一个新的列表用于存储过滤后的管道阶段
        List<Document> filteredPipeline = new ArrayList<>();

        // 过滤掉 $skip 和 $limit 操作符
        for (Document stage : pipeline) {
            if (!stage.keySet().contains("$skip") && !stage.keySet().contains("$limit")) {
                filteredPipeline.add(stage);
            }
        }

        // 添加 $count 阶段到管道末尾
        Document countStage = new Document("$count", "total");
        filteredPipeline.add(countStage);

        // 更新聚合命令中的管道
        countCommand.put("pipeline", filteredPipeline);

        return countCommand;
    }

    private Object mapToEntity(final Document document, final Class<?> clazz) {
        try {
            Object entity = clazz.newInstance();
            for (String key : document.keySet()) {
                // 去掉下划线并首字母大写
                String fieldName = "_id".equals(key) ? "id" : convertFieldName(key);
                //找不到 就去父类找
                java.lang.reflect.Field field = Objects.isNull(clazz.getDeclaredField(fieldName))
                    ? clazz.getSuperclass().getDeclaredField(fieldName) : clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object object = convertValue(document.get(key), field.getType());
                field.set(entity, object);
            }
            return entity;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DaoException(DAO_PROPERTY_IS_EMPTY);
        }
    }

    /**
     *  类型转换
     * @param value
     * @param targetType
     * @return Object
     */
    public static Object convertValue(final Object value, final Class<?> targetType) {
        if (value == null || targetType.isInstance(value)) {
            // 如果值为null或已经是目标类型的实例，则直接返回
            return value;
        }

        if (targetType == String.class) {
            return value.toString();
        }
        else if (Number.class.isAssignableFrom(targetType)) {
            BigDecimal number = new BigDecimal(value.toString());
            if (targetType == Integer.class || targetType == int.class) {
                return number.intValue();
            }
            else if (targetType == Long.class || targetType == long.class) {
                return number.longValue();
            }
            else if (targetType == Double.class || targetType == double.class) {
                return number.doubleValue();
            }
            else if (targetType == Float.class || targetType == float.class) {
                return number.floatValue();
            }
            else if (targetType == BigDecimal.class) {
                return number;
            }
        }
        else if (targetType == Boolean.class || targetType == boolean.class) {
            if (value instanceof Boolean) {
                return value;
            }
            else if (value instanceof Number) {
                return ((Number) value).intValue() != 0;
            }
            else if (value instanceof String) {
                return Boolean.parseBoolean((String) value);
            }
        }
        else if (targetType == Date.class) {
            if (value instanceof Date) {
                return value;
            }
            else if (value instanceof Number) {
                // 假设数字代表时间戳（毫秒）
                return new Date(((Number) value).longValue());
            }
            else if (value instanceof String) {
                // 尝试解析日期字符串，这里可以根据需要添加更多的日期格式
                try {
                    return new Date(Long.parseLong((String) value));
                }
                catch (NumberFormatException e) {
                    // 解析失败，可以考虑抛出异常或者返回null
                    return null;
                }
            }
        }
        // 默认情况下尝试将值转换为字符串
        return value.toString();
    }




    /**
     * 将MongoDB中的字段名转换为Java实体类中的字段名
     * @param mongoDbFieldName
     * @return String
     */
    private String convertFieldName(final String mongoDbFieldName) {
        // 分割输入字段名，默认情况下按照下划线分割
        String[] parts = mongoDbFieldName.split("_");
        StringBuilder convertedName = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (!part.isEmpty()) { // 确保部分不为空
                if (i == 0) {
                    // 对于第一个部分，直接添加，不改变其大小写
                    convertedName.append(part);
                }
                else {
                    // 对于后续部分，转换首字母为大写，其余部分为小写
                    convertedName.append(part.substring(0, 1).toUpperCase());
                    if (part.length() > 1) {
                        convertedName.append(part.substring(1).toLowerCase());
                    }
                }
            }
        }
        return convertedName.toString();
    }


    /**
     *  模板关键字替换
     * @param doc
     * @param dataMap
     */
    private void replacePlaceholders(final Document doc, final Map<String, Object> dataMap) {
        for (String key : new ArrayList<>(doc.keySet())) { // 使用ArrayList包装以避免并发修改异常
            Object value = doc.get(key);

            if (value instanceof Document) {
                // 递归处理嵌套的Document
                replacePlaceholders((Document) value, dataMap);
            }
            else if (value instanceof List) {
                // 处理List类型的值
                List<Object> list = (List<Object>) value;
                for (int i = 0; i < list.size(); i++) {
                    Object item = list.get(i);
                    if (item instanceof Document) {
                        // 如果列表项是Document，则递归处理
                        replacePlaceholders((Document) item, dataMap);
                    }
                    else if (item instanceof String && ((String) item).startsWith("$")) {
                        // 替换列表中的字符串占位符
                        String strValue = (String) item;
                        if (dataMap.containsKey(strValue.substring(1))) {
                            list.set(i, dataMap.get(strValue.substring(1)));
                        }
                    }
                }
            }
            else if (value instanceof String) {
                String strValue = (String) value;
                // 检查是否是需要替换的占位符
                if (strValue.startsWith("$") && dataMap.containsKey(strValue.substring(1))) {
                    doc.put(key, dataMap.get(strValue.substring(1)));
                }
            }
        }
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
            replacePlaceholders(commandDocument, param.getParamMap());

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
        getMongoTemplate().remove(entity);
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
        java.lang.reflect.Field field = findPrimaryKeyField(entities.iterator().next().getClass());

        if (null == field) {
            throw new IllegalStateException("Document does not have an @Id annotated field.");
        }
        String key = field.getAnnotation(Field.class) != null
                ? field.getAnnotation(Field.class).name() : field.getName();

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
     * @param id <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public void deleteById(final Serializable id) {
        java.lang.reflect.Field field = findPrimaryKeyField(getEntityClazz());
        if (null == field) {
            throw new IllegalStateException("Document does not have an @Id annotated field.");
        }
        String key = field.getAnnotation(Field.class) != null
                ? field.getAnnotation(Field.class).name() : field.getName();
        Query query = new Query(Criteria.where(key).is(id));
        getMongoTemplate().remove(query, entityClazz);
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
        java.lang.reflect.Field field = findPrimaryKeyField(getEntityClazz());
        if (null == field) {
            throw new IllegalStateException("Document does not have an @Id annotated field.");
        }
        String key = field.getAnnotation(Field.class) != null
                ? field.getAnnotation(Field.class).name() : field.getName();
        Query query = new Query(Criteria.where(key).in(new ArrayList<>(ids)));
        getMongoTemplate().remove(query, entityClazz);
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

    private void deleteBySpecification(final AbstractWrapper<?> wrapper) {
        Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
        if (!entityClazz.isAnnotationPresent(org.springframework.data.mongodb.core.mapping.Document.class)) {
            throw new IllegalArgumentException("The provided class is not an Document.");
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
        if (!entityClazz.isAnnotationPresent(org.springframework.data.mongodb.core.mapping.Document.class)) {
            throw new IllegalArgumentException("The provided class is not an Document.");
        }
        Assert.notNull(specification, ErrorCodeDef.PARAM_NOT_NULL, "查询条件");
        Map<String, Object> parseEntityNameMap = parseEntityName(entityClazz, true);
        TypedAggregation<T> aggregation = toQueryPredicate(parseEntityNameMap, specification, entityClazz, null, null);

        if (SCALAR_TYPES.contains(clazz)) {
            //执行 sql
            AggregationResults<Map> results = getMongoTemplate().aggregate(aggregation, Map.class);
            Assert.isTrue(Objects.nonNull(results) && Objects.nonNull(results.getMappedResults()),
                    ErrorCodeDef.EXECUTE_ERROR);
            //基本数据类型如计算等 操作 查询不到的时候结果是null 返回基本数据类型默认值
            if (results.getMappedResults().isEmpty() && NUMBER_DEFAULT.keySet().contains(clazz)) {
                return (M) NUMBER_DEFAULT.get(clazz);
            }
            for (Map<String, Object> result : results.getMappedResults()) {
                // 假设specification中指定了要查询的字段名
                String targetField = specification.getSelectionList().get(0).getField(); // 需要在specification中定义此方法
                return (M) result.get(targetField);
            }
        }
        AggregationResults<M> results = getMongoTemplate().aggregate(aggregation, entityClazz, clazz);
        Assert.isTrue(Objects.nonNull(results) && Objects.nonNull(results.getMappedResults()),
                ErrorCodeDef.EXECUTE_ERROR);
        return (M) results.getMappedResults().stream().findFirst().orElse(null);
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
        if (specification.getGroupList().isEmpty()) {
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
        }

        // 处理分组 (GROUP BY)
//        if (!specification.getGroupList().isEmpty()) {
//            GroupOperation groupOperation = Aggregation.group(
//                    specification.getGroupList().stream()
//                            .map(group -> parseEntityNameMap.getOrDefault(group, group).toString())
//                            .toArray(String[]::new));
//
//            if (specification != null && specification.getSelectionList() != null
//                    && !specification.getSelectionList().isEmpty()) {
//
//            }else{
//                // 将所有字段都放到 $group 阶段中，并保持原来的字段名
//                for (Object field : parseEntityNameMap.values()) {
//                    groupOperation = groupOperation.last((String) field).as((String) field);
//                }
//            }
//            // 添加 $group 阶段到聚合操作中
//            operations.add(groupOperation);
//        }

        if (!specification.getGroupList().isEmpty()) {
            // 获取分组字段列表，并转换为需要的形式
            List<String> groupFields = specification.getGroupList().stream()
                    .map(group -> parseEntityNameMap.getOrDefault(group, group).toString())
                    .collect(Collectors.toList());

            GroupOperation groupOperation = Aggregation.group(groupFields.toArray(new String[0]));
            Map<String, String> aliasMap = new HashMap<>();
            if (specification.getSelectionList() != null && !specification.getSelectionList().isEmpty()) {
                for (TempSelection selectedField : specification.getSelectionList()) {
                    groupOperation = groupToSelection(groupOperation, parseEntityNameMap, selectedField, aliasMap);
                }
            }
            else {
                for (Object field : parseEntityNameMap.values()) {
                    String fieldName = field.toString();
                    if (!groupFields.contains(fieldName)) { // 不对分组字段应用 $last 操作
                        groupOperation = groupOperation.last(
                                "id".equals(fieldName) ? "_" + fieldName : fieldName).as(fieldName);
                    }
                }
            }

            // 创建 Project 操作来重构输出
            ProjectionOperation projectOperation = Aggregation.project();
            // 首先排除默认的_id字段
            projectOperation = projectOperation.andExclude("_id");

//            projectOperation = projectOperation.and("_id").as("id");

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
                    String fieldName = parseEntityNameMap.getOrDefault(selectedField.getField(),
                            selectedField.getField()).toString();
                    if (!groupFields.contains(fieldName)) { // 排除分组字段
                        fieldName = "id".equals(fieldName) ? "_" + fieldName : fieldName;
                       String alia = StringUtils.isBlank(aliasMap.get(fieldName)) ? fieldName : aliasMap.get(fieldName);
                        // 如果是计算字段，则直接使用其别名（已在groupToSelection方法中处理）
                        projectOperation = projectOperation.andInclude(alia);
                    }
                }
            }
            else {
                for (Object field : parseEntityNameMap.values()) {
                    String fieldName = field.toString();
                    if (!groupFields.contains(fieldName)) { // 排除分组字段
                        projectOperation = projectOperation.andInclude(fieldName);
                    }
                }
                // 确保所有计算字段也被包含进来
                for (TempSelection selectedField : specification.getSelectionList()) {
                    String fieldName = parseEntityNameMap.getOrDefault(selectedField.getField(),
                            selectedField.getField()).toString();
                    String alia = StringUtils.isBlank(aliasMap.get(fieldName)) ? fieldName : aliasMap.get(fieldName);
                    projectOperation = projectOperation.and(fieldName).as(alia);
                }
            }


            // 添加 $group 和 $project 阶段到聚合操作中
            operations.add(groupOperation);
            operations.add(projectOperation);
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

    private GroupOperation groupToSelection(final GroupOperation groupOperation,
                                            final Map<String, Object> parseEntityNameMap,
                                            final TempSelection tempSelection, final Map<String, String> aliasMap) {
        String fieldName = parseEntityNameMap.containsKey(tempSelection.getField())
                ? parseEntityNameMap.get(tempSelection.getField()).toString() : tempSelection.getField();
        // 特殊处理 "id" 字段
        String fieldName1 = "id".equals(fieldName) ? "_id" : fieldName;
        String alias = fieldName1;
        if (!StringUtils.equals(fieldName, tempSelection.getAlias())) {
            alias = tempSelection.getAlias();
            aliasMap.put(fieldName1, alias);
        }
        if (Operator.COUNT.compareTo(tempSelection.getOperator()) == 0
                && StringUtils.isBlank(aliasMap.get(fieldName1))) {
            aliasMap.put(fieldName1, "count");
        }
        switch (tempSelection.getOperator()) {
            case SUM:
                return groupOperation.sum(fieldName1).as(alias);
            case AVG:
                return groupOperation.avg(fieldName1).as(alias);
            case COUNT:
                return groupOperation.count().as(fieldName1.equals(alias) ? "count" : alias);
            case MAX:
                return groupOperation.max(fieldName1).as(alias);
            case MIN:
                return groupOperation.min(fieldName1).as(alias);
            case DIFF:
                throw new DaoException(ErrorCodeDef.PARAM_ERROR,
                        "DIFF operation is not supported in this method. Please use a separate projection.");
            case SUMMING:
                if (tempSelection.getField2() != null) {
                    String secondFieldName = parseEntityNameMap.containsKey(tempSelection.getField2())
                            ? parseEntityNameMap.get(tempSelection.getField2()).toString() : tempSelection.getField2();
                    return groupOperation.sum(String.format("%s + %s", fieldName1, secondFieldName)).as(alias);
                }
                else {
                    throw new DaoException(ErrorCodeDef.PARAM_ERROR, "SUMMING requires two fields.");
                }
            default:
                return groupOperation.last(fieldName1).as(alias);
        }
    }


    private void toSelection(final List<AggregationOperation> operations, final Map<String, Object> parseEntityNameMap,
                             final TempSelection tempSelection) {
        // 分割字段名称以支持嵌套字段
        String[] fieldParts = tempSelection.getField().split("\\.", 2);
        String topLevelField = fieldParts[0];
        String mappedTopLevelField = (String) parseEntityNameMap.getOrDefault(topLevelField, topLevelField);
        String fieldName = fieldParts.length > 1 ? mappedTopLevelField + "." + fieldParts[1] : mappedTopLevelField;
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
                operations.add(Aggregation.group().first(fieldName).as(alias != null ? alias : fieldName));
                break;
        }
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
        java.lang.reflect.Field field = findPrimaryKeyField(getEntityClazz());
        if (null == field) {
            throw new IllegalStateException("Document does not have an @Id annotated field.");
        }
        String key = field.getAnnotation(Field.class) != null
                ? field.getAnnotation(Field.class).name() : field.getName();
        Query query = new Query(Criteria.where(key).is(id));
        T one = getMongoTemplate().findOne(query, entityClazz);
        return one;
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
     * @param specification
     * @return List<T>
     */
    private List<T> queryBySpecification(final AbstractQueryWrapper<T> specification) {
        return queryBySpecification(specification, entityClazz);
    }

    /**
     * @param wrapper
     * @param clazz
     * @param <M>
     * @return <M> List<M>
     */
    public <M> List<M> queryBySpecification(final AbstractQueryWrapper<T> wrapper, final Class<M> clazz) {
        Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
        if (!entityClazz.isAnnotationPresent(org.springframework.data.mongodb.core.mapping.Document.class)) {
            throw new IllegalArgumentException("The provided class is not an Document.");
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
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
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
        return queryPagerBySpecification(specification.toSpecification(new QueryWrapper<>()), pageIndex,
                pageSize);
    }

    private PagerList<T> queryPagerBySpecification(final AbstractQueryWrapper<T> specification,
                                                   final int pageIndex, final int pageSize) {
        return queryPagerBySpecification(specification, pageIndex, pageSize, entityClazz);
    }

    /**
     * @param wrapper
     * @param pi
     * @param ps
     * @param clazz
     * @param <M>
     * @return <M> PagerList<M>
     */
    @Override
    public <M> PagerList<M> queryPagerBySpecification(final AbstractQueryWrapper<T> wrapper, final int pi,
                                                      final int ps, final Class<M> clazz) {
        Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
        if (!entityClazz.isAnnotationPresent(org.springframework.data.mongodb.core.mapping.Document.class)) {
            throw new IllegalArgumentException("The provided class is not an Document.");
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
        return queryPagerBySpecification(specification.toSpecification(new QueryWrapper<>()), pageIndex,
                pageSize, clazz);
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
        return queryPagerBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()), pageIndex,
                pageSize, clazz);
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
        return queryPagerBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()),
                pageIndex, pageSize);
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
        getMongoTemplate().save(entity);

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
        getMongoTemplate().insert(entitys, entitys.get(0).getClass());
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
        // 执行更新操作
        getMongoTemplate().updateFirst(query, update, pojo.getClass());


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
     * @param wrapper
     */
    @Override
    public void updateBySpecification(final UpdateWrapper<T> wrapper) {

        Assert.notEmpty(wrapper.getValueMap(), ErrorCodeDef.PARAM_NOT_NULL, "修改的内容");
        Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
        if (!entityClazz.isAnnotationPresent(org.springframework.data.mongodb.core.mapping.Document.class)) {
            throw new IllegalArgumentException("The provided class is not an Document.");
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
     * @param wrapper
     */
    @Override
    public void updateBySpecification(final LambdaUpdateWrapper<?> wrapper) {

        Assert.notEmpty(wrapper.getValueMap(), ErrorCodeDef.PARAM_NOT_NULL, "修改的内容");
        Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
        if (!entityClazz.isAnnotationPresent(org.springframework.data.mongodb.core.mapping.Document.class)) {
            throw new IllegalArgumentException("The provided class is not an Document.");
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
     * @param wrapper <br>
     * @author 王伟<br>
     * @taskId <br>
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
     *
     * @param wrapper
     * @param parseEntityNameMap
     * @return Criteria
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
                .filter(tempPredicate -> {
                    // 分割字段名称以支持嵌套字段
                    String[] fieldParts = tempPredicate.getFieldName().split("\\.", 2);
                    return Objects.nonNull(parseEntityNameMap.get(fieldParts[0]));
                })
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

    private Criteria toPredicate(final Map<String, Object> parseEntityNameMap, final TempPredicate predicate) {
        // 分割字段名称以支持嵌套字段
        String[] fieldParts = predicate.getFieldName().split("\\.", 2);
        String topLevelField = fieldParts[0];
        String mappedTopLevelField = (String) parseEntityNameMap.getOrDefault(topLevelField, topLevelField);

        // 构建完整的映射后字段路径
        String fieldName = fieldParts.length > 1 ? mappedTopLevelField + "." + fieldParts[1] : mappedTopLevelField;

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
