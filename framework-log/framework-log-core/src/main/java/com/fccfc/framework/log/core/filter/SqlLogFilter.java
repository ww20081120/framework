/**
 * 
 */
package com.fccfc.framework.log.core.filter;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Types;

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
import com.fccfc.framework.cache.core.CacheException;
import com.fccfc.framework.common.utils.logger.Logger;
import com.fccfc.framework.log.core.TransManager;

/**
 * @author Administrator
 */
public class SqlLogFilter extends FilterEventAdapter {

    private static Logger logger = new Logger(SqlLogFilter.class);

    protected DataSourceProxy dataSource;

    public SqlLogFilter() {
    }

    @Override
    public void init(DataSourceProxy dataSource) {
        this.dataSource = dataSource;
    }

    protected void connectionLog(String message) {
        Logger.sqlInfoLog(message);
    }

    protected void statementLog(String message) {
        Logger.sqlInfoLog(message);
    }

    protected void statementLogError(String message, Throwable error) {
        Logger.sqlErrorLog(message, error);
    }

    protected void resultSetLog(String message) {
        // LogUtil.sqlInfoLog(message);
    }

    protected void resultSetLogError(String message, Throwable error) {
        Logger.sqlErrorLog(message, error);
    }

    public void connection_connectAfter(ConnectionProxy connection) {
        connectionLog("{conn-" + connection.getId() + "} connected");
    }

    @Override
    public Savepoint connection_setSavepoint(FilterChain chain, ConnectionProxy connection) throws SQLException {
        Savepoint savepoint = chain.connection_setSavepoint(connection);
        return savepoint;
    }

    @Override
    public Savepoint connection_setSavepoint(FilterChain chain, ConnectionProxy connection, String name)
        throws SQLException {
        Savepoint savepoint = chain.connection_setSavepoint(connection, name);
        return savepoint;
    }

    @Override
    public void connection_rollback(FilterChain chain, ConnectionProxy connection) throws SQLException {
        super.connection_rollback(chain, connection);
        connectionLog("{conn " + connection.getId() + "} rollback");
    }

    @Override
    public void connection_rollback(FilterChain chain, ConnectionProxy connection, Savepoint savePoint)
        throws SQLException {
        super.connection_rollback(chain, connection, savePoint);
        connectionLog("{conn " + connection.getId() + "} rollback -> " + savePoint.getSavepointId());
    }

    @Override
    public void connection_commit(FilterChain chain, ConnectionProxy connection) throws SQLException {
        super.connection_commit(chain, connection);
        connectionLog("{conn-" + connection.getId() + "} commited");
    }

    @Override
    public void connection_setAutoCommit(FilterChain chain, ConnectionProxy connection, boolean autoCommit)
        throws SQLException {
        connectionLog("{conn-" + connection.getId() + "} setAutoCommit " + autoCommit);
        chain.connection_setAutoCommit(connection, autoCommit);
    }

    @Override
    public void connection_close(FilterChain chain, ConnectionProxy connection) throws SQLException {
        super.connection_close(chain, connection);
        connectionLog("{conn-" + connection.getId() + "} closed");
    }

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

    @Override
    public void resultSet_close(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        chain.resultSet_close(resultSet);
    }

    @Override
    public boolean resultSet_next(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        return super.resultSet_next(chain, resultSet);
    }

    @Override
    public Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
        throws SQLException {
        Object obj = chain.callableStatement_getObject(statement, parameterIndex);

        if (obj instanceof ResultSetProxy) {
            resultSetOpenAfter((ResultSetProxy) obj);
        }

        return obj;
    }

    @Override
    public Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement, int parameterIndex,
        java.util.Map<String, Class<?>> map) throws SQLException {
        Object obj = chain.callableStatement_getObject(statement, parameterIndex, map);

        if (obj instanceof ResultSetProxy) {
            resultSetOpenAfter((ResultSetProxy) obj);
        }

        return obj;
    }

    @Override
    public Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement, String parameterName)
        throws SQLException {
        Object obj = chain.callableStatement_getObject(statement, parameterName);

        if (obj instanceof ResultSetProxy) {
            resultSetOpenAfter((ResultSetProxy) obj);
        }

        return obj;
    }

    @Override
    public Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement,
        String parameterName, java.util.Map<String, Class<?>> map) throws SQLException {
        Object obj = chain.callableStatement_getObject(statement, parameterName, map);

        if (obj instanceof ResultSetProxy) {
            resultSetOpenAfter((ResultSetProxy) obj);
        }

        return obj;
    }

    @Override
    protected void resultSetOpenAfter(ResultSetProxy resultSet) {
    }

    protected void statementCreateAfter(StatementProxy statement) {
        statementLog("{conn-" + statement.getConnectionProxy().getId() + ", stmt-" + statement.getId() + "} created");
    }

    protected void statementPrepareAfter(PreparedStatementProxy statement) {
        statementLog("{conn-" + statement.getConnectionProxy().getId() + ", pstmt-" + statement.getId() + "} created. ");
    }

    protected void statementPrepareCallAfter(CallableStatementProxy statement) {
        statementLog("{conn-" + statement.getConnectionProxy().getId() + ", cstmt-" + statement.getId() + "} created. ");
    }

    @Override
    protected void statement_executeErrorAfter(StatementProxy statement, String sql, Throwable error) {
        String sqlMsg = "{conn-" + statement.getConnectionProxy().getId() + ", " + stmtId(statement)
            + "} execute error. " + getExcuteSql((PreparedStatementProxy) statement);
        statementLogError(sqlMsg, error);
        saveMsg2Cache(sqlMsg);
    }

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

    protected void logParameter(PreparedStatementProxy statement) {
    }

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

    @Override
    public DruidPooledConnection dataSource_getConnection(FilterChain chain, DruidDataSource dataSource,
        long maxWaitMillis) throws SQLException {
        DruidPooledConnection conn = chain.dataSource_connect(dataSource, maxWaitMillis);
        ConnectionProxy connection = (ConnectionProxy) conn.getConnectionHolder().getConnection();
        connectionLog("{conn-" + connection.getId() + "} pool-connect");

        return conn;
    }

    @Override
    public void preparedStatement_clearParameters(FilterChain chain, PreparedStatementProxy statement)
        throws SQLException {
        chain.preparedStatement_clearParameters(statement);
    }

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
     * @param msg
     */
    private void saveMsg2Cache(String msg) {
        try {
            TransManager.getInstance().addSqlLog(msg);
        }
        catch (CacheException e) {
            logger.warn(e.getMessage(), e);
        }
    }
}
