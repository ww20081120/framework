/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.mongo;

import static com.hbasesoft.framework.common.ErrorCodeDef.DAO_PROPERTY_IS_EMPTY;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import com.alibaba.fastjson2.JSONObject;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.criteria.Operator;
import com.hbasesoft.framework.db.core.criteria.TempPredicate;
import com.hbasesoft.framework.db.core.criteria.TempSelection;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年5月14日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.mongo <br>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MongoBaseDaoUtil {

    /** */
    public static final List<Class<?>> SCALAR_TYPES = Arrays.asList(Integer.class, int.class, Long.class, long.class,
        Double.class, double.class, Float.class, float.class, Short.class, short.class, Byte.class, byte.class,
        Boolean.class, boolean.class, String.class, Date.class, BigInteger.class, BigDecimal.class);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param mongoDbFieldName
     * @return <br>
     */
    public static String convertFieldName(final String mongoDbFieldName) {
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
     * 类型转换
     * 
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
            if (value instanceof String) {
                return value.toString();
            }
            else {
                return JSONObject.toJSONString(value);
            }
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
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param clazz
     * @return <br>
     */
    public static Field findPrimaryKeyField(final Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
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
     * @author 王伟<br>
     * @taskId <br>
     * @param field
     * @param parseEntityNameMap
     * @return <br>
     */
    public static String getFieldName(final String field, final Map<String, Object> parseEntityNameMap) {
        String[] fieldParts = field.split("\\.", 2);
        String topLevelField = fieldParts[0];
        String mappedTopLevelField = (String) parseEntityNameMap.getOrDefault(topLevelField, topLevelField);
        return fieldParts.length > 1 ? mappedTopLevelField + "." + fieldParts[1] : mappedTopLevelField;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entityClass
     * @return <br>
     */
    public static String getTableName(final Class<?> entityClass) {
        // 根据@Entity注解获取表名，默认使用类名的小写形式
        Entity entityAnnotation = entityClass.getAnnotation(Entity.class);
        if (entityAnnotation != null && !entityAnnotation.name().isEmpty()) {
            return entityAnnotation.name();
        }
        else {
            Table tableAnnotation = entityClass.getAnnotation(Table.class);
            if (tableAnnotation != null && !tableAnnotation.name().isEmpty()) {
                return tableAnnotation.name();
            }
            return entityClass.getSimpleName().toLowerCase();
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param groupOperation
     * @param parseEntityNameMap
     * @param tempSelection
     * @param aliasMap
     * @return <br>
     */
    public static GroupOperation groupToSelection(final GroupOperation groupOperation,
        final Map<String, Object> parseEntityNameMap, final TempSelection tempSelection,
        final Map<String, String> aliasMap) {
        String fieldName = parseEntityNameMap.containsKey(tempSelection.getField())
            ? parseEntityNameMap.get(tempSelection.getField()).toString()
            : tempSelection.getField();
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
                        ? parseEntityNameMap.get(tempSelection.getField2()).toString()
                        : tempSelection.getField2();
                    return groupOperation.sum(String.format("%s + %s", fieldName1, secondFieldName)).as(alias);
                }
                else {
                    throw new DaoException(ErrorCodeDef.PARAM_ERROR, "SUMMING requires two fields.");
                }
            default:
                return groupOperation.last(fieldName1).as(alias);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param operations
     * @param aggregationFields
     * @param parseEntityNameMap <br>
     */
    public static void handleAggregationOperations(final List<AggregationOperation> operations,
        final List<TempSelection> aggregationFields, final Map<String, Object> parseEntityNameMap) {
        GroupOperation group = Aggregation.group();
        // 添加运算字段
        for (TempSelection selection : aggregationFields) {
            // 分割字段名称以支持嵌套字段
            String fieldName = getFieldName(selection.getField(), parseEntityNameMap);
            String alias = selection.getAlias();
            switch (selection.getOperator()) {
                case SUM:
                    group.sum(fieldName).as(alias != null ? alias : fieldName);
                    break;
                case AVG:
                    group.avg(fieldName).as(alias != null ? alias : fieldName);
                    break;
                case COUNT:
                    group.count().as(alias != null ? alias : "count");
                    break;
                case MAX:
                    group.max(fieldName).as(alias != null ? alias : fieldName);
                    break;
                case MIN:
                    group.min(fieldName).as(alias != null ? alias : fieldName);
                    break;
                case DIFF:
                    throw new DaoException(ErrorCodeDef.PARAM_ERROR, "DIFF requires two fields.");
                default:
                    break;
            }
        }
        // 将合并后的 $group 阶段添加到操作列表中
        operations.add(group);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param document
     * @param clazz
     * @return <br>
     */
    public static Object mapToEntity(final Document document, final Class<?> clazz) {
        try {
            Object entity = clazz.newInstance();
            for (String key : document.keySet()) {
                // 去掉下划线并首字母大写
                String fieldName = "_id".equals(key) ? "id" : convertFieldName(key);
                // 找不到 就去父类找
                Field field = Objects.isNull(clazz.getDeclaredField(fieldName))
                    ? clazz.getSuperclass().getDeclaredField(fieldName)
                    : clazz.getDeclaredField(fieldName);
                if (field == null) {
                    continue; // 如果字段不存在，则跳过
                }
                field.setAccessible(true);
                // 获取字段值
                Object fieldValue = document.get(key);
                // 如果字段类型是复杂类型（如自定义类），递归处理
                if (List.class.isAssignableFrom(field.getType())) {
                    List<?> mongoList;
                    if (!(fieldValue instanceof List)) {
                        mongoList = Collections.singletonList(fieldValue); // 将单个值包装成 List
                    }
                    else {
                        mongoList = (List<?>) fieldValue;
                    }
                    ParameterizedType listType = (ParameterizedType) field.getGenericType();
                    Class<?> listItemType = (Class<?>) listType.getActualTypeArguments()[0]; // 获取 List 的泛型类型
                    List<Object> resultList = new ArrayList<>();
                    for (Object item : mongoList) {
                        if (SCALAR_TYPES.contains(listItemType)) {
                            // 如果是基本数据类型，直接添加到结果列表
                            resultList.add(item);
                        }
                        else {
                            // 如果是复杂类型，递归解析
                            Object nestedEntity = mapToEntity((Document) item, listItemType);
                            resultList.add(nestedEntity);
                        }
                    }
                    field.set(entity, resultList);
                }
                else if (Map.class.isAssignableFrom(field.getType())) {
                    Map<String, Object> mongoMap;
                    if (!(fieldValue instanceof Map)) {
                        mongoMap = JSONObject.parseObject((String) fieldValue, Map.class);
                    }
                    else {
                        mongoMap = (Map<String, Object>) fieldValue;
                    }
                    ParameterizedType mapType = (ParameterizedType) field.getGenericType();
                    Class<?> valueType = (Class<?>) mapType.getActualTypeArguments()[1]; // 获取 Map 的 Value 泛型类型
                    Map<String, Object> resultMap = new HashMap<>();
                    for (Map.Entry<String, Object> entry : mongoMap.entrySet()) {
                        String key1 = entry.getKey();
                        Object value = entry.getValue();

                        if (SCALAR_TYPES.contains(valueType)) {
                            // 如果是基本数据类型，直接放入结果 Map
                            resultMap.put(key1, value);
                        }
                        else {
                            // 如果是复杂类型，递归解析
                            Object nestedEntity = mapToEntity((Document) value, valueType);
                            resultMap.put(key1, nestedEntity);
                        }
                    }
                    field.set(entity, resultMap);
                }
                else if (!SCALAR_TYPES.contains(field.getType())) {
                    if (fieldValue instanceof Document) {
                        // 递归处理嵌套对象
                        Object nestedEntity = mapToEntity((Document) fieldValue, field.getType());
                        field.set(entity, nestedEntity);
                    }
                    else if (fieldValue instanceof Map) {
                        // 如果字段值是 Map，则将其转换为 Document
                        Document nestedDoc = new Document((Map<String, Object>) fieldValue);
                        Object nestedEntity = mapToEntity(nestedDoc, field.getType());
                        field.set(entity, nestedEntity);
                    }
                }
                else {
                    // 否则，进行普通类型转换
                    Object convertedValue = convertValue(fieldValue, field.getType());
                    field.set(entity, "id".equals(fieldName) ? convertedValue.toString() : convertedValue);
                }
            }
            return entity;
        }
        catch (Exception e) {
            LoggerUtil.error(e);
            throw new DaoException(DAO_PROPERTY_IS_EMPTY);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param clazz
     * @param keyIsFieldName
     * @return <br>
     */
    public static Map<String, Object> parseEntityName(final Class<?> clazz, final boolean keyIsFieldName) {
        // 解析实体类，提取带有@Column注解的name 和其对应的 类的属性名
        Map<String, Object> columnValues = new HashMap<>();

        // 获取类的所有声明字段，包括私有字段
        for (Field field : clazz.getDeclaredFields()) {
            // 检查字段是否有@Column注解
            if (field.isAnnotationPresent(Column.class)) {
                Column columnAnnotation = field.getAnnotation(Column.class);

                // 获取@Column注解的name属性
                String columnName = field.isAnnotationPresent(Id.class) ? "_id" : columnAnnotation.name();
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
     * 参数解析对$ 获取其内部值
     * 
     * @param value
     * @param dataMap
     * @return Object
     */
    public static Object parseValue(final String value, final Map<String, Object> dataMap) {
        if (value == null || dataMap == null || dataMap.isEmpty()) {
            return value; // 返回原始值（如果输入无效）
        }
        // 去除外层 $ 或 ${} 提取参数名
        String parameter = value;
        if (parameter.startsWith("${") && parameter.endsWith("}")) {
            // 处理 ${parameter} 格式
            parameter = parameter.substring(2, parameter.length() - 1);
        }
        else if (parameter.startsWith("$")) {
            // 处理 $parameter 格式
            parameter = parameter.substring(1);
        }
        if (parameter == value) {
            return value;
        }

        // 对参数名进行 . 切割
        String[] parts = parameter.split("\\.");

        // 从 dataMap 中逐层获取值
        Object result = dataMap.get(parts[0]);
        for (int i = 1; i < parts.length && result != null; i++) {
            if (result instanceof Map) {
                result = ((Map<?, ?>) result).get(parts[i]); // 获取下一层级的值
            }
            else {
                JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(result));
                result = jsonObject.get((parts[i]));
            }
        }
        return result != null ? result : value; // 如果未找到值，返回原始值
    }

    /**
     * 修改传入的 countCommand 对象，移除 $skip 和 $limit 操作，并添加计数阶段。
     * 
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

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param doc
     * @param dataMap <br>
     */
    public static void replacePlaceholders(final Document doc, final Map<String, Object> dataMap) {
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
                        list.set(i, parseValue((String) value, dataMap));
                    }
                }
            }
            else if (value instanceof String) {
                // 检查是否是需要替换的占位符
                doc.put(key, parseValue((String) value, dataMap));
            }
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param parseEntityNameMap
     * @param predicate
     * @return <br>
     */
    public static Criteria toPredicate(final Map<String, Object> parseEntityNameMap, final TempPredicate predicate) {
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

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param projectOperation
     * @param parseEntityNameMap
     * @param tempSelection
     * @return <br>
     */
    public static ProjectionOperation toSelection(final ProjectionOperation projectOperation,
        final Map<String, Object> parseEntityNameMap, final TempSelection tempSelection) {
        // 分割字段名称以支持嵌套字段
        String fieldName = getFieldName(tempSelection.getField(), parseEntityNameMap);
        String alias = tempSelection.getAlias();
        switch (tempSelection.getOperator()) {
            case DIFF:
                if (tempSelection.getField2() != null) {
                    String secondFieldName = parseEntityNameMap
                        .getOrDefault(tempSelection.getField2(), tempSelection.getField2()).toString();
                    return projectOperation.andExpression(String.format("%s - %s", fieldName, secondFieldName))
                        .as(alias);
                }
                else {
                    throw new DaoException(ErrorCodeDef.PARAM_ERROR, "DIFF requires two fields.");
                }
            default:
                // 普通字段选择使用 $project
                return projectOperation.and(fieldName).as(alias != null ? alias : fieldName);
        }
    }
}
