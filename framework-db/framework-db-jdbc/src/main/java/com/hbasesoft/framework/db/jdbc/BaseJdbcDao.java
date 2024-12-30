/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.jdbc;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;



import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.sql.DataSource;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.UtilException;
import com.hbasesoft.framework.db.core.BaseEntity;
import com.hbasesoft.framework.db.core.criteria.AbstractQueryWrapper;
import com.hbasesoft.framework.db.core.criteria.AbstractWrapper;
import com.hbasesoft.framework.db.core.criteria.DeleteWrapper;
import com.hbasesoft.framework.db.core.criteria.LambdaQueryWrapper;
import com.hbasesoft.framework.db.core.criteria.QueryWrapper;
import com.hbasesoft.framework.db.core.criteria.LambdaUpdateWrapper;
import com.hbasesoft.framework.db.core.criteria.UpdateWrapper;
import com.hbasesoft.framework.db.core.criteria.TempPredicate;
import com.hbasesoft.framework.db.core.criteria.LambdaDeleteWrapper;
import com.hbasesoft.framework.db.core.criteria.TempSelection;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.db.core.DaoConstants;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.DynamicDataSourceManager;
import com.hbasesoft.framework.db.core.config.DaoTypeDef;
import com.hbasesoft.framework.db.core.config.DataParam;
import com.hbasesoft.framework.db.core.utils.PagerList;
import com.hbasesoft.framework.db.core.utils.SQlCheckUtil;
import com.hbasesoft.framework.db.orm.util.DataSourceUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @since V1.0<br>
 * @param <T>
 * @CreateDate Feb 26, 2022 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.jdbc <br>
 */

public class BaseJdbcDao<T extends BaseEntity> implements IBaseDao4Jdbc<T> {
    /**
     *
     */
    private static final int INT = 10;

    /**
     *
     */
    private static final int INT1 = 1000;

    /**
     *  定义一个静态的不可变集合，包含所有标量类型
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
    private static final int PAGE_SIZE = 10;

    /**
     * logger
     */
    private static Logger logger = new Logger(BaseJdbcDao.class);

    /**
     * entity class
     */
    private Class<?> entityClazz;

    /**
     *
     */
    private static Map<String, NamedParameterJdbcTemplate> jdbcTemplate = new HashMap<>();

    /**
     * Description: <br>
     *
     * @param sql
     * @param param
     * @return
     * @throws DaoException <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    @Override
    public Object query(final String sql, final DataParam param) throws DaoException {
        try {
            SQlCheckUtil.checkSql(sql);

            // Redis缓存序列化时不能有void返回类型，特殊处理一下
            if (param.getReturnType() == null) { // @sql返回值类型
                param.setReturnType(void.class);
            }

            Map<String, Object> dataMap = param.getParamMap();

            String lowerCaseSql = sql.toLowerCase();

            RowMapper<?> rowMap = null;
            // step2:设置返回值类型
            final Object callBack = param.getCallback(); // 自定义映射
            if (callBack != null && callBack instanceof RowMapper) {
                RowMapper rm = (RowMapper) callBack;
                rowMap = rm;
            }
            else if (param.getBeanType().equals(Map.class)) { //返回值类型的具体bean
                rowMap = new ColumnMapRowMapper();
            }
            else {
                Class<?> beanType = param.getBeanType();
                if (SCALAR_TYPES.contains(beanType)) {
                    // 处理标量查询
                    return getJdbcTemplate().queryForObject(sql, dataMap, beanType);
                }
                else if (Serializable.class.equals(beanType)) {
                    beanType = param.getReturnType();
                    if (entityClazz != null && List.class.isAssignableFrom(param.getReturnType())) {
                        beanType = entityClazz;
                    }
                }
                rowMap = new BeanPropertyRowMapper(beanType);
            }

            // 处理分页
            if (dataMap.containsKey(DaoConstants.PAGE_INDEX)) {
                int pageIndex = Integer.parseInt(dataMap.get(DaoConstants.PAGE_INDEX).toString());
                if (pageIndex <= 0) {
                    pageIndex = 1;
                }

                int pageSize = dataMap.containsKey(DaoConstants.PAGE_SIZE)
                        ? Integer.parseInt(dataMap.get(DaoConstants.PAGE_SIZE).toString())
                        : INT;
                if (pageSize <= 0 || pageSize > INT1) {
                    pageSize = PAGE_SIZE;
                }

                int orderByIndex = lowerCaseSql.lastIndexOf(" order by ");

                StringBuilder pageScript = new StringBuilder().append("SELECT COUNT(1) FROM (")
                        .append(orderByIndex != -1 ? sql.substring(0, orderByIndex) : sql).append(") QUERY_DATA__");
                long total = getJdbcTemplate().queryForObject(pageScript.toString(), dataMap, Long.class);

                PagerList list = new PagerList();
                list.setPageIndex(pageIndex);
                list.setPageSize(pageSize);
                list.setTotalCount(total);

                if (total > 0 && (pageIndex - 1) * pageSize < total) {
                    List<?> datas = getJdbcTemplate().query(new StringBuilder().append(sql).append(" limit ")
                        .append((pageIndex - 1) * pageSize).append(',').append(pageSize).toString(), dataMap, rowMap);
                    list.addAll(datas);
                }
                return list;
            }
            return getJdbcTemplate().query(sql, dataMap, rowMap);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DaoException(ErrorCodeDef.QUERY_ERROR, e);
        }
    }

    /**
     * Description: <br>
     *
     * @param sql
     * @param param
     * @return
     * @throws DaoException <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public int excuteSql(final String sql, final DataParam param) throws DaoException {
        try {
            SQlCheckUtil.checkSql(sql);
            return getJdbcTemplate().update(sql, param.getParamMap());
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
     * @return
     * @throws DaoException <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public int[] batchExcuteSql(final String[] sqls, final DataParam param) throws DaoException {
        try {

            int[] result = new int[sqls.length];
            NamedParameterJdbcTemplate template = getJdbcTemplate();
            for (int i = 0; i < sqls.length; i++) {
                String sql = sqls[i];
                SQlCheckUtil.checkSql(sql);
                result[i] = template.update(sql, param.getParamMap());
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
     * @param sql
     * @param objcts
     * @param commitNumber
     * @return int[]
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public int[] batchExecute(final String sql, final Collection<T> objcts, final int commitNumber) {
        if (CollectionUtils.isEmpty(objcts)) {
            return new int[0];
        }

        SqlParameterSource[] datas = SqlParameterSourceUtils.createBatch(objcts);
        ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(sql);
        SqlParameterSource paramSource = datas[0];

        String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, paramSource);
        List<SqlParameter> declaredParameters = NamedParameterUtils.buildSqlParameterList(parsedSql, paramSource);
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(sqlToUse, declaredParameters);

        return getJdbcTemplate().getJdbcOperations().batchUpdate(pscf.getSql(), new BatchPreparedStatementSetter() {
            @Override
            public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                Object[] values = NamedParameterUtils.buildValueArray(parsedSql, datas[i], null);
                pscf.newPreparedStatementSetter(values).setValues(ps);
            }

            @Override
            public int getBatchSize() {
                return commitNumber;
            }
        });

    }

    @Override
    public int updateBySql(final String sql) throws DaoException {
        DataParam param = new DataParam();
        param.setParamMap(new HashMap<String, Object>());
        int excuted = excuteSql(sql, param);
        return excuted;
    }

    /**
     * Description: <br>
     *
     * @param entityClazz <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    public void setEntityClazz(final Class<?> entityClazz) {
        this.entityClazz = entityClazz;
    }

    private NamedParameterJdbcTemplate getJdbcTemplate() {
        synchronized (jdbcTemplate) {
            String code = DynamicDataSourceManager.getInstance(DaoTypeDef.db).getDataSourceCode();
            NamedParameterJdbcTemplate template = jdbcTemplate.get(code);
            if (template == null) {
                DataSource dataSource = DataSourceUtil.getDataSource(code);
                Assert.notNull(dataSource, ErrorCodeDef.DB_DATASOURCE_NOT_SET, code);
                template = new NamedParameterJdbcTemplate(dataSource);
            }
            return template;
        }
    }


    @Override
    public void delete(final DeleteSpecification<T> specification) {
        deleteBySpecification(specification.toSpecification(new DeleteWrapper<>()));
    }

    private void deleteBySpecification(final AbstractWrapper<?> wrapper) {
        Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
        if (!entityClazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }
        Map<String, Object> parseEntityNameMap = parseEntityName(entityClazz, true);
        StringBuilder sqlBuilder = new StringBuilder().append("DELETE FROM ").append(getTableName(entityClazz));
        DataParam param = new DataParam();
        toPredicate(wrapper, parseEntityNameMap, sqlBuilder, param);
        // 如果没有条件直接抛出异常
        Assert.notEmpty(param.getParamMap(), ErrorCodeDef.PARAM_NOT_NULL, "删除的条件");
        //执行 sql
        excuteSql(sqlBuilder.toString(), param);
    }

    @Override
    public void delete(final T entity) {
        // 解析实体类并获取表结构信息
        Map<String, Object> columnValues = parseEntity(entity, true);
        if (MapUtils.isEmpty(columnValues)) {
            throw new DaoException(ErrorCodeDef.ANALYSIS_ENTITY_ERROR);
        }

        DataParam param = new DataParam();
        param.setParamMap(columnValues);

        // 使用 StringBuilder 构建 DELETE SQL 语句
        StringBuilder sqlBuilder = new StringBuilder("DELETE FROM ");
        sqlBuilder.append(getTableName(entity.getClass())).append(" WHERE ");
        StringBuilder whereClause = new StringBuilder();
        for (String col : columnValues.keySet()) {
            // 跳过主键字段，因为它不是要更新的数据部分
            if (StringUtils.equals("ENTITY_ID", col)) {
                continue;
            }
            if (whereClause.length() > 0) {
                whereClause.append(" AND ");
            }
            whereClause.append(col).append(" = :").append(col); // 使用命名参数
        }
        sqlBuilder.append(whereClause);

        // 执行 SQL 语句
        excuteSql(sqlBuilder.toString(), param);
    }

    @Override
    public void deleteBatch(final Collection<T> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }
        //获取主键
        Field primaryKeyField = findPrimaryKeyField(entities.iterator().next().getClass());
        if (primaryKeyField == null) {
            throw new IllegalStateException(
                    "Could not find a primary key field annotated with @Id in the given entity type.");
        }
        String entitieName = "";
        Column columnAnnotation = primaryKeyField.getAnnotation(Column.class);
        if (columnAnnotation != null && !columnAnnotation.name().isEmpty()) {
            entitieName =  columnAnnotation.name();
        }
        else {
            entitieName = primaryKeyField.getName();
        }
        // 使用 StringBuilder 构建 DELETE SQL 语句
        StringBuilder sqlBuilder = new StringBuilder("DELETE FROM ");
        sqlBuilder.append(getTableName(entities.iterator().next().getClass()))
                .append(" WHERE ").append(entitieName).append("  = :")
                .append(primaryKeyField.getName());
        //最终执行sql
        batchExecute(sqlBuilder.toString(), entities, entities.size());


    }

    @Override
    public void deleteById(final Serializable id) {
        // 确保实体类上有@Entity注解
        Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
        if (!entityClazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }
        Field primaryKeyField = findPrimaryKeyField(entityClazz);
        if (primaryKeyField == null) {
            throw new IllegalStateException(
                    "Could not find a primary key field annotated with @Id in the given entity type.");
        }
        // 构建HQL或Criteria删除语句
        StringBuilder sqlBuilder = new StringBuilder().append("DELETE FROM ")
                .append(getTableName(entityClazz)).append(" WHERE ")
                .append(primaryKeyField.getName()).append(" = :id");

        // 设置参数
        DataParam param = new DataParam();
        param.setParamMap(Map.of("id", id));

        // 执行 SQL
        excuteSql(sqlBuilder.toString(), param);
    }

    @Override
    public void deleteByIds(final Collection<? extends Serializable> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {

            // 确保实体类上有@Entity注解
            Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
            if (!entityClazz.isAnnotationPresent(Entity.class)) {
                throw new IllegalArgumentException("The provided class is not an Entity.");
            }
            Field primaryKeyField = findPrimaryKeyField(entityClazz);
            if (primaryKeyField == null) {
                throw new IllegalStateException(
                 "Could not find a primary key field annotated with @Id in the given entity type.");
            }

            // 构建HQL或Criteria删除语句
            StringBuilder sqlBuilder = new StringBuilder().append("DELETE FROM ")
                    .append(getTableName(entityClazz)).append(" WHERE ")
                    .append(primaryKeyField.getName()).append(" IN (:ids)");
            // 设置参数
            DataParam param = new DataParam();
            param.setParamMap(Map.of("ids", ids));

            // 执行 SQL
            excuteSql(sqlBuilder.toString(), param);
        }
    }

    @Override
    public void deleteByLambda(final LambdaDeleteSpecification<T> specification) {
        deleteBySpecification(specification.toSpecification(new LambdaDeleteWrapper<>()));

    }

    @Override
    public void deleteByLambda(final LambdaDeleteWrapper<T> wrapper) {
        deleteBySpecification(wrapper);
    }

    /**
     *
     * @param specification
     * @return T
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
        return (T) getBySpecification(specification, entityClazz);
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

        StringBuilder sqlBuilder = new StringBuilder().append("SELECT ");
        DataParam param = new DataParam();
        param.setBeanType(clazz);

        toQueryPredicate(sqlBuilder, parseEntityNameMap, specification, param);

        Object queriedObject = query(sqlBuilder.toString(), param);
        if (SCALAR_TYPES.contains(clazz)) {
            return (M) queriedObject;
        }
        List<M> queried = (List<M>) queriedObject;
        if (CollectionUtils.isEmpty(queried)) {
            return null;
        }
        return queried.get(0);
    }

    private void toQueryPredicate(final StringBuilder sqlBuilder, final Map<String, Object> parseEntityNameMap,
                                  final AbstractQueryWrapper<T> specification, final DataParam param) {
        if (specification.getSelectionList().isEmpty()) {
            sqlBuilder.append(parseEntityNameMap.values().stream()
                    .map(Object::toString) // 将每个对象转换为字符串
                    .collect(Collectors.joining(", "))); // 使用逗号和空格连接
        }
        else {
            for (int i = 0; i < specification.getSelectionList().size(); ++i) {
                if (i > 0) {
                    sqlBuilder.append(", ");
                }
                toSelection(sqlBuilder, parseEntityNameMap, specification.getSelectionList().get(i));
            }
        }
        sqlBuilder.append(" FROM ").append(getTableName(entityClazz)).append(" ");
        toPredicate(specification, parseEntityNameMap, sqlBuilder, param);
        if (MapUtils.isEmpty(param.getParamMap())) {
            param.setParamMap(new HashMap<String, Object>());
        }
        // 处理分组
        if (!specification.getGroupList().isEmpty()) {
            sqlBuilder.append(" GROUP BY ");
            String groupByFields = specification.getGroupList().stream()
                    .map(group -> parseEntityNameMap.getOrDefault(group, group))
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            sqlBuilder.append(groupByFields).append(" ");
        }

        // 构建 ORDER BY 子句
        if (!specification.getOrderByList().isEmpty()) {
            sqlBuilder.append(" ORDER BY ");
            String orderByClauses = specification.getOrderByList().stream()
                    .map(order -> {
                        String columnName = parseEntityNameMap.getOrDefault(order.getProperty(),
                                order.getProperty()).toString();
                        return columnName + (order.isDesc() ? " DESC" : " ASC");
                    })
                    .collect(Collectors.joining(", "));
            sqlBuilder.append(orderByClauses);
        }

    }

    private void toSelection(final StringBuilder sqlBuilder, final Map<String, Object> parseEntityNameMap,
                             final TempSelection tempSelection) {
        String columnName = parseEntityNameMap.getOrDefault(tempSelection.getField(),
                tempSelection.getField()).toString();

        switch (tempSelection.getOperator()) {
            case FIELD:
                sqlBuilder.append(columnName);
                break;
            case SUM:
                sqlBuilder.append("SUM(").append(columnName).append(")");
                break;
            case AVG:
                sqlBuilder.append("AVG(").append(columnName).append(")");
                break;
            case COUNT:
                sqlBuilder.append("COUNT(").append(columnName).append(")");
                break;
            case MAX:
                sqlBuilder.append("MAX(").append(columnName).append(")");
                break;
            case MIN:
                sqlBuilder.append("MIN(").append(columnName).append(")");
                break;
            case DIFF:
                if (tempSelection.getField2() != null) {
                    String secondColumnName = parseEntityNameMap.getOrDefault(tempSelection.getField2(),
                            tempSelection.getField2()).toString();
                    sqlBuilder.append("(").append(columnName).append(" - ").append(secondColumnName).append(")");
                }
                else {
                    throw new DaoException(ErrorCodeDef.PARAM_ERROR, "DIFF requires two fields.");
                }
                break;
            case SUMMING:
                if (tempSelection.getField2() != null) {
                    String secondColumnName = parseEntityNameMap.getOrDefault(tempSelection.getField2(),
                            tempSelection.getField2()).toString();
                    sqlBuilder.append("SUM(").append(columnName).append(" + ").append(secondColumnName).append(")");
                }
                else {
                    throw new DaoException(ErrorCodeDef.PARAM_ERROR, "SUMMING requires two fields.");
                }
                break;
            default:
                throw new DaoException(ErrorCodeDef.PARAM_ERROR, tempSelection.getOperator().name());
        }
        // 添加别名（如果有）
        if (StringUtils.isNotEmpty(tempSelection.getAlias())) {
            sqlBuilder.append(" AS ").append(tempSelection.getAlias());
        }

    }


    @Override
    public <M> M get(final QuerySpecification<T> specification, final Class<M> clazz) {
        return getBySpecification(specification.toSpecification(new QueryWrapper<>()), clazz);
    }

    @Override
    public T get(final QueryWrapper<T> wrapper) {
        return getBySpecification(wrapper);
    }

    @Override
    public <M> M get(final QueryWrapper<T> wrapper, final Class<M> clazz) {
        return getBySpecification(wrapper, clazz);
    }

    @Override
    public T get(final Serializable id) {
        // 确保实体类上有@Entity注解
        Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
        if (!entityClazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }
        Field primaryKeyField = findPrimaryKeyField(entityClazz);
        if (primaryKeyField == null) {
            throw new IllegalStateException(
                    "Could not find a primary key field annotated with @Id in the given entity type.");
        }
        Map<String, Object> stringObjectMap = parseEntityName(entityClazz, false);
        // 构建HQL或Criteria删除语句
        StringBuilder sqlBuilder = new StringBuilder().append("SELECT ")
                .append(String.join(", ", stringObjectMap.keySet()))
                .append(" FROM ").append(getTableName(entityClazz)).append(" WHERE ")
                .append(primaryKeyField.getName()).append(" = :id");

        // 设置参数
        DataParam param = new DataParam();
        param.setParamMap(Map.of("id", id));
        param.setBeanType(entityClazz);
        // 执行 SQL
        List<T> queried = (List<T>) query(sqlBuilder.toString(), param);
        if (CollectionUtils.isEmpty(queried)) {
            return null;
        }
        return queried.get(0);
    }

    @Override
    public <M> M getByLambda(final LambdaQuerySpecification<T, M> specification, final Class<M> clazz) {
        return getBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()), clazz);
    }

    @Override
    public T getByLambda(final LambdaQuerySpecification<T, T> specification) {
        return getBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()));
    }

    @Override
    public <M> M getByLambda(final LambdaQueryWrapper<T, M> wrapper, final Class<M> clazz) {
        return getBySpecification(wrapper, clazz);
    }

    @Override
    public T getByLambda(final LambdaQueryWrapper<T, T> wrapper) {
        return getBySpecification(wrapper);
    }

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
        return (List<T>) queryBySpecification(specification, entityClazz);
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

        StringBuilder sqlBuilder = new StringBuilder().append("SELECT ");
        DataParam param = new DataParam();
        param.setBeanType(clazz);
        toQueryPredicate(sqlBuilder, parseEntityNameMap, wrapper, param);
        sqlBuilder.append(" limit ").append(INT1);
        List<M> query = (List<M>) query(sqlBuilder.toString(), param);
        return query;
    }

    private PagerList<T> queryPagerBySpecification(final AbstractQueryWrapper<T> specification,
                                                   final int pageIndex, final int pageSize) {
        return (PagerList<T>) queryPagerBySpecification(specification, pageIndex, pageSize, entityClazz);
    }

    /**
     *
     * @param wrapper
     * @param pi
     * @param pageSize
     * @param clazz
     * @return <M> PagerList<M>
     * @param <M>
     */
    public <M> PagerList<M> queryPagerBySpecification(final AbstractQueryWrapper<T> wrapper, final int pi,
                                                      final int pageSize, final Class<M> clazz) {
        Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
        if (!entityClazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }
        Assert.notNull(wrapper, ErrorCodeDef.PARAM_NOT_NULL, "查询条件");
        Map<String, Object> parseEntityNameMap = parseEntityName(entityClazz, true);

        StringBuilder sqlBuilder = new StringBuilder().append("SELECT ");
        DataParam param = new DataParam();
        param.setBeanType(clazz);
        toQueryPredicate(sqlBuilder, parseEntityNameMap, wrapper, param);
        param.getParamMap().put(DaoConstants.PAGE_INDEX, pi);
        param.getParamMap().put(DaoConstants.PAGE_SIZE, pageSize);
        PagerList query = (PagerList) query(sqlBuilder.toString(), param);
        return query;
    }

    @Override
    public <M> List<M> query(final QuerySpecification<T> specification, final Class<M> clazz) {
        return queryBySpecification(specification.toSpecification(new QueryWrapper<>()), clazz);
    }

    @Override
    public List<T> query(final QueryWrapper<T> wrapper) {
        return queryBySpecification(wrapper);
    }

    @Override
    public <M> List<M> query(final QueryWrapper<T> wrapper, final Class<M> clazz) {
        return queryBySpecification(wrapper, clazz);
    }

    @Override
    public List<T> queryAll() {
        // 确保实体类上有@Entity注解
        Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
        if (!entityClazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }

        List<T> resultList = new ArrayList<>();
        String tableName = getTableName(entityClazz); // 需要实现此方法来获取表名
        Map<String, Object> parseEntityNameMap = parseEntityName(entityClazz, false);

        // 构建HQL或Criteria查询语句
        StringBuilder sqlBuilder = new StringBuilder().append("SELECT ")
                .append(String.join(", ", parseEntityNameMap.keySet()))
                .append(" FROM ").append(tableName).append("").append(" LIMIT 1000");

        DataParam param = new DataParam();
        param.setBeanType(entityClazz);
        param.setParamMap(new HashMap<>());
        param.setReturnType(List.class);
        // 执行 SQL
        List<T> query = (List<T>) query(sqlBuilder.toString(), param);
        return query;
    }

    @Override
    public <M> List<M> queryByLambda(final LambdaQuerySpecification<T, M> specification, final Class<M> clazz) {
        return queryBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()), clazz);
    }

    @Override
    public List<T> queryByLambda(final LambdaQuerySpecification<T, T> specification) {
        return queryBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()));
    }

    @Override
    public <M> List<M> queryByLambda(final LambdaQueryWrapper<T, M> wrapper, final Class<M> clazz) {
        return queryBySpecification(wrapper, clazz);
    }

    @Override
    public List<T> queryByLambda(final LambdaQueryWrapper<T, T> wrapper) {
        return queryBySpecification(wrapper);
    }

    @Override
    public PagerList<T> queryPager(final QuerySpecification<T> specification, final int pageIndex, final int pageSize) {
        return queryPagerBySpecification(specification.toSpecification(new QueryWrapper<>()), pageIndex,
                pageSize);
    }


    @Override
    public <M> PagerList<M> queryPager(final QuerySpecification<T> specification,
                                       final int pageIndex, final int pageSize, final Class<M> clazz) {
        return queryPagerBySpecification(specification.toSpecification(new QueryWrapper<>()), pageIndex,
                pageSize, clazz);
    }

    @Override
    public PagerList<T> queryPager(final QueryWrapper<T> wrapper, final int pageIndex, final int pageSize) {
        return queryPagerBySpecification(wrapper, pageIndex, pageSize);
    }

    @Override
    public <M> PagerList<M> queryPager(final QueryWrapper<T> wrapper, final int pageIndex,
                                       final int pageSize, final Class<M> clazz) {
        return queryPagerBySpecification(wrapper, pageIndex, pageSize, clazz);
    }

    @Override
    public <M> PagerList<M> queryPagerByLambda(final LambdaQuerySpecification<T, M> specification,
                                               final int pageIndex, final int pageSize, final Class<M> clazz) {
        return queryPagerBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()), pageIndex,
                pageSize, clazz);
    }

    @Override
    public PagerList<T> queryPagerByLambda(final LambdaQuerySpecification<T, T> specification,
                                           final int pageIndex, final int pageSize) {
        return queryPagerBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()),
                pageIndex, pageSize);
    }

    @Override
    public <M> PagerList<M> queryPagerByLambda(final LambdaQueryWrapper<T, M> wrapper, final int pageIndex,
                                               final int pageSize, final Class<M> clazz) {
        return queryPagerBySpecification(wrapper, pageIndex, pageSize, clazz);
    }

    @Override
    public PagerList<T> queryPagerByLambda(final LambdaQueryWrapper<T, T> wrapper,
                                           final int pageIndex, final int pageSize) {
        return queryPagerBySpecification(wrapper, pageIndex, pageSize);
    }

    @Override
    public void save(final T entity) {
        // 解析实体类并获取表结构信息
        Map<String, Object> columnValues = parseEntity(entity, false);
        if (MapUtils.isEmpty(columnValues)) {
            throw new DaoException(ErrorCodeDef.ANALYSIS_ENTITY_ERROR);
        }
        DataParam param = new DataParam();
        param.setParamMap(columnValues);
        // 使用 StringBuilder 构建 INSERT SQL 语句
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ");
        sqlBuilder.append(getTableName(entity.getClass())).append(" (");
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        for (String col : columnValues.keySet()) {
            if (StringUtils.equals("ENTITY_ID", col)) {
                continue;
            }
            if (columns.length() > 0) {
                columns.append(", ");
                values.append(", ");
            }
            columns.append(col);
            values.append(":").append(col); // 使用命名参数
        }
        sqlBuilder.append(columns).append(") VALUES (").append(values).append(")");

        excuteSql(sqlBuilder.toString(), param);

    }

    @Override
    public void saveBatch(final List<T> entitys) {
        if (entitys.size() > GlobalConstants.DEFAULT_LINES) {
            throw new UtilException(ErrorCodeDef.TOO_MANY_OBJECTS);
        }
        if (CollectionUtils.isEmpty(entitys)) {
            return;
        }
        // 确保实体类上有@Entity注解
        if (!entityClazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }

        // 获取所有字段名并构建字段列表
        Map<String, Object> parseEntityMap = parseEntityName(entitys.getFirst(), true);


        // 使用 StringBuilder 构建 INSERT SQL 语句
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ");
        sqlBuilder.append(getTableName(entityClazz)).append(" (");
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        for (Map.Entry<String, Object> entry : parseEntityMap.entrySet()) {
            if (StringUtils.equals("ENTITY_ID", entry.getKey())
                    || Objects.isNull(entry.getValue())) {
                continue;
            }
            if (columns.length() > 0) {
                columns.append(", ");
                values.append(", ");
            }
            columns.append(entry.getKey());
            values.append(":").append(entry.getValue()); // 使用命名参数
        }
        sqlBuilder.append(columns).append(") VALUES (").append(values).append(")");
        //最终执行sql
        batchExecute(sqlBuilder.toString(), entitys, entitys.size());

    }

    @Override
    public void update(final T pojo) {
        // 解析实体类并获取表结构信息
        Map<String, Object> columnValues = parseEntity(pojo, false);
        if (MapUtils.isEmpty(columnValues)) {
            throw new DaoException(ErrorCodeDef.ANALYSIS_ENTITY_ERROR);
        }

        // 检查是否存在主键 ENTITY_ID，如果不存在则无法执行更新操作
        Object entityId = columnValues.get("ENTITY_ID");
        if (entityId == null) {
            throw new DaoException(ErrorCodeDef.ID_IS_NULL);
        }
        DataParam param = new DataParam();
        param.setParamMap(columnValues);

        // 使用 StringBuilder 构建 UPDATE SQL 语句
        StringBuilder sqlBuilder = new StringBuilder("UPDATE ");
        sqlBuilder.append(getTableName(pojo.getClass())).append(" SET ");
        StringBuilder setters = new StringBuilder();
        for (Map.Entry<String, Object> entry : columnValues.entrySet()) {
            // 跳过主键字段，因为它不是要更新的数据部分
            if (StringUtils.equals("ENTITY_ID", entry.getKey()) || Objects.equals(entry.getKey(), entityId)) {
                continue;
            }
            if (setters.length() > 0) {
                setters.append(", ");
            }
            setters.append(entry.getKey()).append(" = :").append(entry.getKey()); // 使用命名参数
        }

        sqlBuilder.append(setters).append(" WHERE ").append(entityId).append("= :").append(entityId);

        excuteSql(sqlBuilder.toString(), param);
    }

    /**
     *
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
    public void updateBySpecification(final LambdaUpdateWrapper<?> wrapper) {

        Assert.notEmpty(wrapper.getValueMap(), ErrorCodeDef.PARAM_NOT_NULL, "修改的内容");
        Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
        if (!entityClazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }
        //获取 解析实体类，提取带有@Column注解的字段及属性名称
        Map<String, Object> parseEntityNameMap = parseEntityName(entityClazz, true);
        Map<String, Object> paramSetMap = new HashMap<>();
        StringBuilder sqlBuilder = new StringBuilder().append("UPDATE ")
                .append(getTableName(entityClazz)).append(" SET ");
        boolean noFirst = false; //判断是否是第一次进入循环
        for (Map.Entry<String, Object> entry : wrapper.getValueMap().entrySet()) {
            if (noFirst) {
                sqlBuilder.append(" , ");
            }
            else {
                noFirst = true;
            }
            sqlBuilder.append(parseEntityNameMap.get(entry.getKey())).append(" = :")
                    .append(parseEntityNameMap.get(entry.getKey())).append("set");
            paramSetMap.put(parseEntityNameMap.get(entry.getKey()) + "set", entry.getValue());
        }
        DataParam param = new DataParam();
        param.setParamMap(paramSetMap);
        toPredicate(wrapper, parseEntityNameMap, sqlBuilder, param);
        // 如果没有条件直接抛出异常
        Assert.notEmpty(param.getParamMap(), ErrorCodeDef.PARAM_NOT_NULL, "修改的条件");
        //执行 sql
        excuteSql(sqlBuilder.toString(), param);
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
        Map<String, Object> paramSetMap = new HashMap<>();
        StringBuilder sqlBuilder = new StringBuilder().append("UPDATE ")
                .append(getTableName(entityClazz)).append(" SET ");
        boolean noFirst = false; //判断是否是第一次进入循环
        for (Map.Entry<String, Object> entry : wrapper.getValueMap().entrySet()) {
            if (noFirst) {
                sqlBuilder.append(" , ");
            }
            else {
                noFirst = true;
            }
            sqlBuilder.append(parseEntityNameMap.get(entry.getKey())).append(" = :")
                    .append(parseEntityNameMap.get(entry.getKey())).append("set");
            paramSetMap.put(parseEntityNameMap.get(entry.getKey()) + "set", entry.getValue());
        }
        DataParam param = new DataParam();
        param.setParamMap(paramSetMap);
        toPredicate(wrapper, parseEntityNameMap, sqlBuilder, param);
        // 如果没有条件直接抛出异常
        Assert.notEmpty(param.getParamMap(), ErrorCodeDef.PARAM_NOT_NULL, "修改的条件");
        //执行 sql
        excuteSql(sqlBuilder.toString(), param);
    }

    /**
     *
     * @param wrapper <br>
     */
    @Override
    public void update(final UpdateWrapper<T> wrapper) {
        updateBySpecification(wrapper);
    }

    /**
     *
     * @param entitys <br>
     */
    @Override
    public void updateBatch(final List<T> entitys) {
        if (entitys.size() > GlobalConstants.DEFAULT_LINES) {
            throw new UtilException(ErrorCodeDef.TOO_MANY_OBJECTS);
        }
        if (CollectionUtils.isEmpty(entitys)) {
            return;
        }
        // 确保实体类上有@Entity注解
        if (!entityClazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }

        // 获取所有字段名并构建字段列表
        Map<String, Object> parseEntityMap = parseEntityName(entitys.getFirst(), false);

        // 使用 StringBuilder 构建 INSERT SQL 语句
        StringBuilder sqlBuilder = new StringBuilder("UPDATE ");
        sqlBuilder.append(getTableName(entityClazz)).append(" SET ");
        StringBuilder setters = new StringBuilder();

        for (Map.Entry<String, Object> entry : parseEntityMap.entrySet()) {
            // 跳过主键字段，因为它不是要更新的数据部分
            if (StringUtils.equals("ENTITY_ID", entry.getKey())
                    || Objects.equals(entry.getKey(), parseEntityMap.get("ENTITY_ID"))) {
                continue;
            }
            if (setters.length() > 0) {
                setters.append(", ");
            }
            setters.append(entry.getKey()).append(" = :").append(entry.getValue()); // 使用命名参数
        }

        sqlBuilder.append(setters).append(" WHERE ").append(parseEntityMap.get("ENTITY_ID"))
                .append("= :").append(parseEntityMap.get("ENTITY_ID"));

        //最终执行sql
        batchExecute(sqlBuilder.toString(), entitys, entitys.size());
    }

    @Override
    public void updateByLambda(final LambdaUpdateSpecification<T> specification) {
        updateBySpecification(specification.toSpecification(new LambdaUpdateWrapper<>()));
    }

    @Override
    public void updateByLambda(final LambdaUpdateWrapper<T> wrapper) {
        updateBySpecification(wrapper);
    }

    /**
     * 根据@Entity注解获取表名，默认使用类名的小写形式
     *
     * @param entityClass
     * @return String
     */
    private String getTableName(final Class<?> entityClass) {
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
            Class<?> entityClass = entity.getClass();
            for (Field field : entityClass.getDeclaredFields()) {
                Column columnAnnotation = field.getAnnotation(Column.class);
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
            Field[] fields = entity.getClass().getDeclaredFields();
            for (Field field : fields) {
                // 检查字段是否有@Column注解
                if (field.isAnnotationPresent(Column.class)) {
                    Column columnAnnotation = field.getAnnotation(Column.class);
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
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            // 检查字段是否有@Column注解
            if (field.isAnnotationPresent(Column.class)) {
                Column columnAnnotation = field.getAnnotation(Column.class);
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
    private Field findPrimaryKeyField(final Class<?> clazz) {
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
     * Description: <br>
     *
     * @param wrapper
     * @param parseEntityNameMap
     * @param param
     * @param sqlBuilder
     * @author 王伟<br>
     * @taskId <br>
     */
    private void toPredicate(final AbstractWrapper<?> wrapper, final Map<String, Object> parseEntityNameMap,
                             final StringBuilder sqlBuilder, final DataParam param) {
        // 检查是否有任何条件
        boolean hasConditions = CollectionUtils.isNotEmpty(wrapper.getTempPredicates())
                || CollectionUtils.isNotEmpty(wrapper.getOrTempPredicates());
        // 如果没有条件和order by 直接返回
        if (!hasConditions) {
            return;
        }
        // 如果有任意一个条件，添加 WHERE 关键字
        if (hasConditions && !sqlBuilder.toString().contains("WHERE")) {
            sqlBuilder.append(" WHERE ");
        }
        Map<String, Object> paramMap = new HashMap<>();
        boolean whereAdded = false;

        // 组装 where 条件（AND）
        for (int i = 0; i < wrapper.getTempPredicates().size(); ++i) {
            TempPredicate tempPredicate = wrapper.getTempPredicates().get(i);
            if (Objects.nonNull(parseEntityNameMap.get(tempPredicate.getFieldName()))) {
                sqlBuilder.append(whereAdded ? " AND " : "");
                toPredicate(parseEntityNameMap, sqlBuilder, tempPredicate, paramMap);
                whereAdded = true;
            }
        }
        // 处理 or 的过滤条件
        for (List<TempPredicate> orTempPredicates : wrapper.getOrTempPredicates()) {
            if (CollectionUtils.isNotEmpty(orTempPredicates)) {
                sqlBuilder.append(whereAdded ? " OR (" : "(");
                boolean firstInOrGroup = true;
                for (TempPredicate orPredicate : orTempPredicates) {
                    if (Objects.nonNull(parseEntityNameMap.get(orPredicate.getFieldName()))) {
                        if (!firstInOrGroup) {
                            sqlBuilder.append(" OR ");
                        }
                        toPredicate(parseEntityNameMap, sqlBuilder, orPredicate, paramMap);
                        firstInOrGroup = false;
                        whereAdded = true;
                    }
                }
                sqlBuilder.append(")");
            }
        }
        // 获取当前的参数映射并合并新的参数
        Map<String, Object> currentParamMap = param.getParamMap();
        if (currentParamMap == null) {
            param.setParamMap(paramMap);
        }
        else {
            currentParamMap.putAll(paramMap);
            param.setParamMap(currentParamMap);
        }
    }

    /**
     * Description: TempPredicate 转换为 Predicate <br>
     *
     * @param parseEntityNameMap
     * @param sqlBuilder
     * @param predicate
     * @param paramMap
     * @author 王伟<br>
     * @taskId <br>
     */
    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    private void toPredicate(final Map<String, Object> parseEntityNameMap, final StringBuilder sqlBuilder,
                             final TempPredicate predicate, final Map<String, Object> paramMap) {
        String fieldName = (String) parseEntityNameMap.get(predicate.getFieldName());
        switch (predicate.getOperator()) {
            case EQ:
                sqlBuilder.append(fieldName).append(" = :").append(fieldName);
                paramMap.put(fieldName, predicate.getValue());
                break;
            case NE:
                sqlBuilder.append(fieldName).append(" <> :").append(fieldName);
                paramMap.put(fieldName, predicate.getValue());
                break;
            case GE:
                sqlBuilder.append(fieldName).append(" >= :").append(fieldName);
                paramMap.put(fieldName, predicate.getValue());
                break;
            case GREATER_THAN_OR_EQUAL_TO:
                sqlBuilder.append(fieldName).append(" >= :").append(fieldName);
                paramMap.put(fieldName, predicate.getValue());
                break;
            case GT:
                sqlBuilder.append(fieldName).append(" > :").append(fieldName);
                paramMap.put(fieldName, predicate.getValue());
                break;
            case GREATER_THAN:
                sqlBuilder.append(fieldName).append(" > :").append(fieldName);
                paramMap.put(fieldName, predicate.getValue());
                break;
            case LE:
                sqlBuilder.append(fieldName).append(" <= :").append(fieldName);
                paramMap.put(fieldName, predicate.getValue());
                break;
            case LESS_THAN_OR_EQUAL_TO:
                sqlBuilder.append(fieldName).append(" <= :").append(fieldName);
                paramMap.put(fieldName, predicate.getValue());
                break;
            case LT:
                sqlBuilder.append(fieldName).append(" < :").append(fieldName);
                paramMap.put(fieldName, predicate.getValue());
                break;
            case LESS_THAN:
                sqlBuilder.append(fieldName).append(" < :").append(fieldName);
                paramMap.put(fieldName, predicate.getValue());
                break;
            case IN:
                Collection<?> objects = (Collection<?>) predicate.getValue();
                sqlBuilder.append(fieldName).append(" IN (:").append(fieldName).append(")");
                paramMap.put(fieldName, objects);
                break;
            case NOTIN:
                objects = (Collection<?>) predicate.getValue();
                sqlBuilder.append(fieldName).append(" NOT IN (:").append(fieldName).append(")");
                paramMap.put(fieldName, objects);
                break;
            case LIKE:
                String value = (String) predicate.getValue();
                Assert.notEmpty(value, ErrorCodeDef.PARAM_NOT_NULL, predicate.getFieldName());
                sqlBuilder.append(fieldName).append(" LIKE (:").append(value).append(")");
                paramMap.put(fieldName, value);
                break;
            case NOTLIKE:
                value = (String) predicate.getValue();
                Assert.notEmpty(value, ErrorCodeDef.PARAM_NOT_NULL, predicate.getFieldName());
                sqlBuilder.append(fieldName).append(" NOT LIKE (:").append(fieldName).append(")");
                paramMap.put(fieldName, value);
                break;
            case ISNULL:
                sqlBuilder.append(fieldName).append(" IS NULL");
                break;
            case NOTNULL:
                sqlBuilder.append(fieldName).append(" IS NOT NULL");
                break;
            case BETWEEN:
                Comparable[] objs = (Comparable[]) predicate.getValue();
                sqlBuilder.append(fieldName).append(" BETWEEN (:").append(fieldName + "begin")
                        .append(" AND :").append(fieldName + "end").append(")");
                paramMap.put(fieldName + "begin", objs[0]);
                paramMap.put(fieldName + "end", objs[1]);
                break;
            default:
                break;
        }
    }
}
