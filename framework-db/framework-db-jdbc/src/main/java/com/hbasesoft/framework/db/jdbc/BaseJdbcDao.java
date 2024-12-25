/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.jdbc;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.collections4.CollectionUtils;
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
import com.hbasesoft.framework.db.core.BaseEntity;
import com.hbasesoft.framework.db.core.DaoConstants;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.DynamicDataSourceManager;
import com.hbasesoft.framework.db.core.config.DaoTypeDef;
import com.hbasesoft.framework.db.core.config.DataParam;
import com.hbasesoft.framework.db.core.criteria.LambdaDeleteWrapper;
import com.hbasesoft.framework.db.core.criteria.LambdaQueryWrapper;
import com.hbasesoft.framework.db.core.criteria.LambdaUpdateWrapper;
import com.hbasesoft.framework.db.core.criteria.QueryWrapper;
import com.hbasesoft.framework.db.core.criteria.UpdateWrapper;
import com.hbasesoft.framework.db.core.utils.PagerList;
import com.hbasesoft.framework.db.core.utils.SQlCheckUtil;
import com.hbasesoft.framework.db.orm.util.DataSourceUtil;

/**
 * <Description> <br>
 * 
 * @param <T> T
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Feb 26, 2022 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.jdbc <br>
 */
public class BaseJdbcDao<T extends BaseEntity> implements IBaseDao4Jdbc<T> {

    /** */
    private static final int INT = 10;

    /** */
    private static final int INT1 = 1000;

    /** */
    private static final int PAGE_SIZE = 10;

    /**
     * logger
     */
    private static Logger logger = new Logger(BaseJdbcDao.class);

    /** entity class */
    private Class<?> entityClazz;

    /** */
    private static Map<String, NamedParameterJdbcTemplate> jdbcTemplate = new HashMap<>();

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param sql
     * @param param
     * @return
     * @throws DaoException <br>
     */
    @SuppressWarnings({
        "unchecked", "rawtypes"
    })
    @Override
    public Object query(final String sql, final DataParam param) throws DaoException {
        try {
            SQlCheckUtil.checkSql(sql);

            // Redis缓存序列化时不能有void返回类型，特殊处理一下
            if (param.getReturnType() == null) {
                param.setReturnType(void.class);
            }

            Map<String, Object> dataMap = param.getParamMap();

            String lowerCaseSql = sql.toLowerCase();

            RowMapper<?> rowMap = null;
            // step2:设置返回值类型
            final Object callBack = param.getCallback();
            if (callBack != null && callBack instanceof RowMapper) {
                RowMapper rm = (RowMapper) callBack;
                rowMap = rm;
            }
            else if (param.getBeanType().equals(Map.class)) {
                rowMap = new ColumnMapRowMapper();
            }
            else {
                Class<?> beanType = param.getBeanType();
                if (Serializable.class.equals(beanType)) {
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
     * @author 王伟<br>
     * @taskId <br>
     * @param sql
     * @param param
     * @return
     * @throws DaoException <br>
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
     * @author 王伟<br>
     * @taskId <br>
     * @param sqls
     * @param param
     * @return
     * @throws DaoException <br>
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
     * @author 王伟<br>
     * @taskId <br>
     * @param sql
     * @param objcts
     * @param commitNumber
     * @return int[]
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

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entityClazz <br>
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

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification <br>
     */
    @Override
    public void delete(final DeleteSpecification<T> specification) {
        // TODO Auto-generated method stub

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
        // TODO Auto-generated method stub

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
        // TODO Auto-generated method stub

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
        // TODO Auto-generated method stub

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
        // TODO Auto-generated method stub

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
        // TODO Auto-generated method stub

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
        // TODO Auto-generated method stub

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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub

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
        // TODO Auto-generated method stub

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
        // TODO Auto-generated method stub

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification <br>
     */
    @Override
    public void update(final UpdateSpecification<T> specification) {
        // TODO Auto-generated method stub

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
        // TODO Auto-generated method stub

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
        // TODO Auto-generated method stub

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
        // TODO Auto-generated method stub

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
        // TODO Auto-generated method stub

    }

}
