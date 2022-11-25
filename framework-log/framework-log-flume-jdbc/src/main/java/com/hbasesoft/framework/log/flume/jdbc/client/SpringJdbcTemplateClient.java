/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.flume.jdbc.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.alibaba.fastjson.JSONObject;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.InitializationException;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.date.DateUtil;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.db.core.utils.DataSourceUtil;
import com.hbasesoft.framework.log.flume.core.EventSerializer;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Aug 18, 2022 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.log.flume.jdbc.client <br>
 */
public class SpringJdbcTemplateClient implements JdbcClient {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final EventSerializer serializer;

    private List<Map<String, Object>> datas;

    private final String insertSql;

    private String idKey;

    private String createTimeKey;

    /** 
     *  
     */
    public SpringJdbcTemplateClient(String datasourceCode, String tableName, EventSerializer serializer) {
        DataSource dataSource = DataSourceUtil.getDataSource(datasourceCode);
        Assert.notNull(dataSource, ErrorCodeDef.DB_DATASOURCE_NOT_SET, datasourceCode);
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.serializer = serializer;
        datas = new ArrayList<>();
        insertSql = getInsertSql(dataSource, tableName);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param tableName
     * @return <br>
     */
    private String getInsertSql(DataSource dataSource, String tableName) {
        StringBuilder builder = new StringBuilder();
        builder.append("insert into ").append(tableName).append(" (");

        StringBuilder valueBuilder = new StringBuilder();

        try (Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement("select * from " + tableName + " where 1 = 2");) {
            ResultSetMetaData metaData = ps.executeQuery().getMetaData();

            for (int i = 0, size = metaData.getColumnCount(); i < size; i++) {
                String cmt = metaData.getColumnLabel(i + 1);
                if ("id".equalsIgnoreCase(cmt)) {
                    idKey = cmt;
                }
                else if ("create_time".equalsIgnoreCase(cmt)) {
                    createTimeKey = cmt;
                }
                if (i != 0) {
                    builder.append(", ");
                    valueBuilder.append(", ");
                }
                builder.append(cmt);
                valueBuilder.append(':').append(cmt);
            }
            builder.append(") values (").append(valueBuilder.toString()).append(')');
        }
        catch (Exception e) {
            LoggerUtil.error(e);
            throw new InitializationException(e);
        }
        return builder.toString();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param event
     * @throws Exception <br>
     */
    @Override
    public void addEvent(Event event) throws Exception {
        String content = serializer.getContentBuilder(event);
        JSONObject obj = JSONObject.parseObject(content);
        if (StringUtils.isNotEmpty(idKey) && !obj.containsKey(idKey)) {
            obj.put(idKey, CommonUtil.getTransactionID());
        }

        if (StringUtils.isNotEmpty(createTimeKey)) {
            if (obj.containsKey(createTimeKey)) {
                obj.put(createTimeKey, DateUtil.string2Date(obj.getString(createTimeKey)));
            }
            else {
                obj.put(createTimeKey, new Date());
            }
        }

        datas.add(obj);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @throws Exception <br>
     */
    @SuppressWarnings("unchecked")
    @Override
    public void execute() throws Exception {
        if (CollectionUtils.isEmpty(datas)) {
            return;
        }

        Map<String, Object>[] tempDatas;
        synchronized (datas) {
            tempDatas = datas.toArray(new Map[0]);
            datas.clear();
        }

        SqlParameterSource[] sources = SqlParameterSourceUtils.createBatch(tempDatas);
        ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(insertSql);
        SqlParameterSource paramSource = sources[0];

        String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, paramSource);
        List<SqlParameter> declaredParameters = NamedParameterUtils.buildSqlParameterList(parsedSql, paramSource);
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(sqlToUse, declaredParameters);

        try {
            jdbcTemplate.getJdbcOperations().batchUpdate(pscf.getSql(), new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Object[] values = NamedParameterUtils.buildValueArray(parsedSql, sources[i], null);
                    pscf.newPreparedStatementSetter(values).setValues(ps);
                }

                @Override
                public int getBatchSize() {
                    return sources.length;
                }
            });
        }
        catch (Exception e) {
            LoggerUtil.error(e);
            throw e;
        }

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param context <br>
     */
    @Override
    public void configure(Context context) {
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Override
    public void close() {
    }
}
