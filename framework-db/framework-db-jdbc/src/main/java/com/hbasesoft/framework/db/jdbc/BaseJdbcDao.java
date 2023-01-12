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

import org.apache.commons.collections.CollectionUtils;
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
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.DynamicDataSourceManager;
import com.hbasesoft.framework.db.core.annotation.Param;
import com.hbasesoft.framework.db.core.config.DataParam;
import com.hbasesoft.framework.db.core.executor.ISqlExcutor;
import com.hbasesoft.framework.db.core.utils.DataSourceUtil;
import com.hbasesoft.framework.db.core.utils.PagerList;
import com.hbasesoft.framework.db.core.utils.SQlCheckUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Feb 26, 2022 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.jdbc <br>
 */
public class BaseJdbcDao implements ISqlExcutor {

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
    public Object query(String sql, DataParam param) throws DaoException {
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
                rowMap = (RowMapper<?>) callBack;
            }
            else if (param.getBeanType().equals(Map.class)) {
                new ColumnMapRowMapper();
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
            if (dataMap.containsKey(Param.PAGE_INDEX)) {
                int pageIndex = Integer.parseInt(dataMap.get(Param.PAGE_INDEX).toString());
                if (pageIndex <= 0) {
                    pageIndex = 1;
                }

                int pageSize = dataMap.containsKey(Param.PAGE_SIZE)
                    ? Integer.parseInt(dataMap.get(Param.PAGE_SIZE).toString())
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
    public int excuteSql(String sql, DataParam param) throws DaoException {
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
     * @param sql
     * @param param
     * @return
     * @throws DaoException <br>
     */
    @Override
    public int[] batchExcuteSql(String[] sqls, DataParam param) throws DaoException {
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
     * @throws DaoException <br>
     */
    public int[] batchExecute(final String sql, final Collection<?> objcts, final int commitNumber) {
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
            public void setValues(PreparedStatement ps, int i) throws SQLException {
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
            String code = DynamicDataSourceManager.getDataSourceCode();
            NamedParameterJdbcTemplate template = jdbcTemplate.get(code);
            if (template == null) {
                DataSource dataSource = DataSourceUtil.getDataSource(code);
                Assert.notNull(dataSource, ErrorCodeDef.DB_DATASOURCE_NOT_SET, code);
                template = new NamedParameterJdbcTemplate(dataSource);
            }
            return template;
        }
    }

}
