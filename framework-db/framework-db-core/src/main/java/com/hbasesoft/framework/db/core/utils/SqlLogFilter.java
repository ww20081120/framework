/**
 * 
 */
package com.hbasesoft.framework.db.core.utils;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Types;
import java.util.ServiceLoader;

import org.apache.commons.lang.StringUtils;

import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.proxy.jdbc.CallableStatementProxy;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.proxy.jdbc.JdbcParameter;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.common.utils.logger.TransLoggerService;
import com.hbasesoft.framework.common.utils.logger.TransManager;

/**
 * <Description> <br>
 * 
 * @author yang.zhipeng <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月1日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.log.core.filter <br>
 */
public class SqlLogFilter extends FilterEventAdapter {

    /**
     * transLoggerServices
     */
    private ServiceLoader<TransLoggerService> transLoggerServices;

    /**
     * dataSource
     */
    protected DataSourceProxy dataSource;

    /**
     * SqlLogFilter
     */
    public SqlLogFilter() {
    }

    @Override
    public void init(DataSourceProxy dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param message <br>
     */
    protected void connectionLog(String message) {
        LoggerUtil.sqlLog(message);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param message <br>
     */
    protected void statementLog(String message) {
        LoggerUtil.sqlLog(message);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param message <br>
     * @param error <br>
     */
    protected void statementLogError(String message, Throwable error) {
        LoggerUtil.sqlLog(message, error);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param message <br>
     */
    protected void resultSetLog(String message) {
        // LogUtil.sqlInfoLog(message);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param message <br>
     * @param error <br>
     */
    protected void resultSetLogError(String message, Throwable error) {
        LoggerUtil.sqlLog(message, error);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param connection <br>
     */
    public void connection_connectAfter(ConnectionProxy connection) {
        connectionLog("{conn-" + connection.getId() + "} connected");
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param chain <br>
     * @param connection <br>
     * @return <br>
     * @throws SQLException <br>
     */
    @Override
    public Savepoint connection_setSavepoint(FilterChain chain, ConnectionProxy connection) throws SQLException {
        Savepoint savepoint = chain.connection_setSavepoint(connection);
        return savepoint;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param chain <br>
     * @param connection <br>
     * @param name <br>
     * @return <br>
     * @throws SQLException <br>
     */
    @Override
    public Savepoint connection_setSavepoint(FilterChain chain, ConnectionProxy connection, String name)
        throws SQLException {
        Savepoint savepoint = chain.connection_setSavepoint(connection, name);
        return savepoint;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param chain <br>
     * @param connection <br>
     * @throws SQLException <br>
     */
    @Override
    public void connection_rollback(FilterChain chain, ConnectionProxy connection) throws SQLException {
        super.connection_rollback(chain, connection);
        connectionLog("{conn " + connection.getId() + "} rollback");
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param chain <br>
     * @param connection <br>
     * @param savePoint <br>
     * @throws SQLException <br>
     */
    @Override
    public void connection_rollback(FilterChain chain, ConnectionProxy connection, Savepoint savePoint)
        throws SQLException {
        super.connection_rollback(chain, connection, savePoint);
        connectionLog("{conn " + connection.getId() + "} rollback -> " + savePoint.getSavepointId());
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param chain <br>
     * @param connection <br>
     * @throws SQLException <br>
     */
    @Override
    public void connection_commit(FilterChain chain, ConnectionProxy connection) throws SQLException {
        super.connection_commit(chain, connection);
        connectionLog("{conn-" + connection.getId() + "} commited");
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param chain <br>
     * @param connection <br>
     * @param autoCommit <br>
     * @throws SQLException <br>
     */
    @Override
    public void connection_setAutoCommit(FilterChain chain, ConnectionProxy connection, boolean autoCommit)
        throws SQLException {
        connectionLog("{conn-" + connection.getId() + "} setAutoCommit " + autoCommit);
        chain.connection_setAutoCommit(connection, autoCommit);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param chain <br>
     * @param connection <br>
     * @throws SQLException <br>
     */
    @Override
    public void connection_close(FilterChain chain, ConnectionProxy connection) throws SQLException {
        super.connection_close(chain, connection);
        connectionLog("{conn-" + connection.getId() + "} closed");
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param chain <br>
     * @param statement <br>
     * @throws SQLException <br>
     */
    @Override
    public void statement_close(FilterChain chain, StatementProxy statement) throws SQLException {
        super.statement_close(chain, statement);
        statementLog("{conn-" + statement.getConnectionProxy().getId() + ", " + stmtId(statement) + "} closed");
    }

    @Override
    protected void statementExecuteBefore(StatementProxy statement, String sql) {
        statement.setLastExecuteStartNano();
        if (statement instanceof PreparedStatementProxy) {
            logParameter((PreparedStatementProxy) statement);
        }
    }

    @Override
    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean firstResult) {
        statement.setLastExecuteTimeNano();
        double nanos = statement.getLastExecuteTimeNano();
        double millis = nanos / (1000 * 1000);

        String sqlMsg = "{conn-" + statement.getConnectionProxy().getId() + ", " + stmtId(statement) + "} executed. "
            + millis + " millis. " + getExcuteSql((PreparedStatementProxy) statement);
        statementLog(sqlMsg);
        saveMsg2Cache(sqlMsg);
    }

    @Override
    protected void statementExecuteBatchBefore(StatementProxy statement) {
        statement.setLastExecuteStartNano();
    }

    @Override
    protected void statementExecuteBatchAfter(StatementProxy statement, int[] result) {
        statement.setLastExecuteTimeNano();
        double nanos = statement.getLastExecuteTimeNano();
        double millis = nanos / (1000 * 1000);

        String sql;
        if (statement instanceof PreparedStatementProxy) {
            sql = ((PreparedStatementProxy) statement).getSql();
        }
        else {
            sql = statement.getBatchSql();
        }

        String sqlMsg = "{conn-" + statement.getConnectionProxy().getId() + ", " + stmtId(statement)
            + "} batch executed. " + millis + " millis. " + sql;
        statementLog(sqlMsg);
        saveMsg2Cache(sqlMsg);
    }

    @Override
    protected void statementExecuteQueryBefore(StatementProxy statement, String sql) {
        statement.setLastExecuteStartNano();
        if (statement instanceof PreparedStatementProxy) {
            logParameter((PreparedStatementProxy) statement);
        }
    }

    @Override
    protected void statementExecuteQueryAfter(StatementProxy statement, String sql, ResultSetProxy resultSet) {
        statement.setLastExecuteTimeNano();
        double nanos = statement.getLastExecuteTimeNano();
        double millis = nanos / (1000 * 1000);

        String sqlMsg = "{conn-" + statement.getConnectionProxy().getId() + ", " + stmtId(statement) + ", rs-"
            + resultSet.getId() + "} query executed. " + millis + " millis. "
            + getExcuteSql((PreparedStatementProxy) statement);
        statementLog(sqlMsg);
        saveMsg2Cache(sqlMsg);
    }

    @Override
    protected void statementExecuteUpdateBefore(StatementProxy statement, String sql) {
        statement.setLastExecuteStartNano();
        if (statement instanceof PreparedStatementProxy) {
            logParameter((PreparedStatementProxy) statement);
        }
    }

    @Override
    protected void statementExecuteUpdateAfter(StatementProxy statement, String sql, int updateCount) {
        statement.setLastExecuteTimeNano();
        double nanos = statement.getLastExecuteTimeNano();
        double millis = nanos / (1000 * 1000);
        String sqlMsg = "{conn-" + statement.getConnectionProxy().getId() + ", " + stmtId(statement)
            + "} update executed. effort " + updateCount + ". " + millis + " millis. "
            + getExcuteSql((PreparedStatementProxy) statement);

        statementLog(sqlMsg);
        saveMsg2Cache(sqlMsg);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param chain <br>
     * @param resultSet <br>
     * @throws SQLException <br>
     */
    @Override
    public void resultSet_close(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        chain.resultSet_close(resultSet);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param chain <br>
     * @param resultSet <br>
     * @return <br>
     * @throws SQLException <br>
     */
    @Override
    public boolean resultSet_next(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        return super.resultSet_next(chain, resultSet);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param chain <br>
     * @param statement <br>
     * @param parameterIndex <br>
     * @return <br>
     * @throws SQLException <br>
     */
    @Override
    public Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
        throws SQLException {
        Object obj = chain.callableStatement_getObject(statement, parameterIndex);

        if (obj instanceof ResultSetProxy) {
            resultSetOpenAfter((ResultSetProxy) obj);
        }

        return obj;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param chain <br>
     * @param statement <br>
     * @param parameterIndex <br>
     * @param map <br>
     * @return <br>
     * @throws SQLException <br>
     */
    @Override
    public Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement, int parameterIndex,
        java.util.Map<String, Class<?>> map) throws SQLException {
        Object obj = chain.callableStatement_getObject(statement, parameterIndex, map);

        if (obj instanceof ResultSetProxy) {
            resultSetOpenAfter((ResultSetProxy) obj);
        }

        return obj;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param chain <br>
     * @param statement <br>
     * @param parameterName <br>
     * @return <br>
     * @throws SQLException <br>
     */
    @Override
    public Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement, String parameterName)
        throws SQLException {
        Object obj = chain.callableStatement_getObject(statement, parameterName);

        if (obj instanceof ResultSetProxy) {
            resultSetOpenAfter((ResultSetProxy) obj);
        }

        return obj;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param chain <br>
     * @param statement <br>
     * @param parameterName <br>
     * @param map <br>
     * @return <br>
     * @throws SQLException <br>
     */
    @Override
    public Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement, String parameterName,
        java.util.Map<String, Class<?>> map) throws SQLException {
        Object obj = chain.callableStatement_getObject(statement, parameterName, map);

        if (obj instanceof ResultSetProxy) {
            resultSetOpenAfter((ResultSetProxy) obj);
        }

        return obj;
    }

    @Override
    protected void resultSetOpenAfter(ResultSetProxy resultSet) {
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param statement <br>
     */
    protected void statementCreateAfter(StatementProxy statement) {
        statementLog("{conn-" + statement.getConnectionProxy().getId() + ", stmt-" + statement.getId() + "} created");
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param statement <br>
     */
    protected void statementPrepareAfter(PreparedStatementProxy statement) {
        statementLog(
            "{conn-" + statement.getConnectionProxy().getId() + ", pstmt-" + statement.getId() + "} created. ");
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param statement <br>
     */
    protected void statementPrepareCallAfter(CallableStatementProxy statement) {
        statementLog(
            "{conn-" + statement.getConnectionProxy().getId() + ", cstmt-" + statement.getId() + "} created. ");
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param statement <br>
     * @param sql <br>
     * @param error <br>
     */
    @Override
    protected void statement_executeErrorAfter(StatementProxy statement, String sql, Throwable error) {
        String sqlMsg = "{conn-" + statement.getConnectionProxy().getId() + ", " + stmtId(statement)
            + "} execute error. " + getExcuteSql((PreparedStatementProxy) statement);
        statementLogError(sqlMsg, error);
        saveMsg2Cache(sqlMsg);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param statement <br>
     * @return <br>
     */
    private String stmtId(StatementProxy statement) {
        StringBuffer buf = new StringBuffer();
        if (statement instanceof CallableStatementProxy) {
            buf.append("cstmt-");
        }
        else if (statement instanceof PreparedStatementProxy) {
            buf.append("pstmt-");
        }
        else {
            buf.append("stmt-");
        }
        buf.append(statement.getId());

        return buf.toString();
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param statement <br>
     */
    protected void logParameter(PreparedStatementProxy statement) {
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param chain <br>
     * @param conn <br>
     * @throws SQLException <br>
     */
    @Override
    public void dataSource_releaseConnection(FilterChain chain, DruidPooledConnection conn) throws SQLException {
        long connectionId = -1;

        if (conn.getConnectionHolder() != null) {
            ConnectionProxy connection = (ConnectionProxy) conn.getConnectionHolder().getConnection();
            connectionId = connection.getId();
        }

        chain.dataSource_recycle(conn);
        connectionLog("{conn-" + connectionId + "} pool-recycle");
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param chain <br>
     * @param dataSource <br>
     * @param maxWaitMillis <br>
     * @return <br>
     * @throws SQLException <br>
     */
    @Override
    public DruidPooledConnection dataSource_getConnection(FilterChain chain, DruidDataSource dataSource,
        long maxWaitMillis) throws SQLException {
        DruidPooledConnection conn = chain.dataSource_connect(dataSource, maxWaitMillis);
        ConnectionProxy connection = (ConnectionProxy) conn.getConnectionHolder().getConnection();
        connectionLog("{conn-" + connection.getId() + "} pool-connect");

        return conn;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param chain <br>
     * @param statement <br>
     * @throws SQLException <br>
     */
    @Override
    public void preparedStatement_clearParameters(FilterChain chain, PreparedStatementProxy statement)
        throws SQLException {
        chain.preparedStatement_clearParameters(statement);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param statement <br>
     * @return <br>
     */
    private String getExcuteSql(PreparedStatementProxy statement) {
        String sqlStr = statement.getSql();
        for (JdbcParameter parameter : statement.getParameters().values()) {
            int sqlType = parameter.getSqlType();
            Object value = parameter.getValue();
            String tempValue = Types.NULL == sqlType ? "NULL" : String.valueOf(value);
            sqlStr = StringUtils.replaceOnce(sqlStr, "?", tempValue);
        }

        return sqlStr;
    }

    /**
     * 保存日志信息岛缓存
     * 
     * @param msg <br>
     */
    private void saveMsg2Cache(String msg) {
        String statckId = TransManager.getInstance().getStackId();
        for (TransLoggerService service : getTransLoggerServices()) {
            service.sql(statckId, msg);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    private ServiceLoader<TransLoggerService> getTransLoggerServices() {
        if (transLoggerServices == null) {
            transLoggerServices = ServiceLoader.load(TransLoggerService.class);
        }
        return transLoggerServices;
    }
}
