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
import com.hbasesoft.framework.common.GlobalConstants;
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
    private DataSourceProxy dataSource;

    /**
     * SqlLogFilter
     */
    public SqlLogFilter() {
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param ds <br>
     */
    @Override
    public void init(final DataSourceProxy ds) {
        this.dataSource = ds;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param message <br>
     */
    protected void connectionLog(final String message) {
        LoggerUtil.sqlLog(message);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param message <br>
     */
    protected void statementLog(final String message) {
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
    protected void statementLogError(final String message, final Throwable error) {
        LoggerUtil.sqlLog(message, error);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param message <br>
     */
    protected void resultSetLog(final String message) {
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
    protected void resultSetLogError(final String message, final Throwable error) {
        LoggerUtil.sqlLog(message, error);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param connection <br>
     */
    public void connectionConnectAfter(final ConnectionProxy connection) {
        if (connection != null) {
            connectionLog("{conn-" + connection.getId() + "} connected");
        }
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
    public Savepoint connection_setSavepoint(final FilterChain chain, final ConnectionProxy connection)
        throws SQLException {
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
    public Savepoint connection_setSavepoint(final FilterChain chain, final ConnectionProxy connection,
        final String name) throws SQLException {
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
    public void connection_rollback(final FilterChain chain, final ConnectionProxy connection) throws SQLException {
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
    public void connection_rollback(final FilterChain chain, final ConnectionProxy connection,
        final Savepoint savePoint) throws SQLException {
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
    public void connection_commit(final FilterChain chain, final ConnectionProxy connection) throws SQLException {
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
    public void connection_setAutoCommit(final FilterChain chain, final ConnectionProxy connection,
        final boolean autoCommit) throws SQLException {
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
    public void connection_close(final FilterChain chain, final ConnectionProxy connection) throws SQLException {
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
    public void statement_close(final FilterChain chain, final StatementProxy statement) throws SQLException {
        super.statement_close(chain, statement);
        statementLog("{conn-" + statement.getConnectionProxy().getId() + ", " + stmtId(statement) + "} closed");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param statement
     * @param sql <br>
     */
    @Override
    protected void statementExecuteBefore(final StatementProxy statement, final String sql) {
        statement.setLastExecuteStartNano();
        if (statement instanceof PreparedStatementProxy) {
            logParameter((PreparedStatementProxy) statement);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param statement
     * @param sql
     * @param firstResult <br>
     */
    @Override
    protected void statementExecuteAfter(final StatementProxy statement, final String sql, final boolean firstResult) {
        statement.setLastExecuteTimeNano();
        double nanos = statement.getLastExecuteTimeNano();
        double millis = nanos / (GlobalConstants.SECONDS * GlobalConstants.SECONDS);

        String sqlMsg = "{conn-" + statement.getConnectionProxy().getId() + ", " + stmtId(statement) + "} executed. "
            + millis + " millis. " + getExcuteSql(statement);
        statementLog(sqlMsg);
        saveMsg2Cache(sqlMsg);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param statement <br>
     */
    @Override
    protected void statementExecuteBatchBefore(final StatementProxy statement) {
        statement.setLastExecuteStartNano();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param statement
     * @param result <br>
     */
    @Override
    protected void statementExecuteBatchAfter(final StatementProxy statement, final int[] result) {
        statement.setLastExecuteTimeNano();
        double nanos = statement.getLastExecuteTimeNano();
        double millis = nanos / (GlobalConstants.SECONDS * GlobalConstants.SECONDS);

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

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param statement
     * @param sql <br>
     */
    @Override
    protected void statementExecuteQueryBefore(final StatementProxy statement, final String sql) {
        statement.setLastExecuteStartNano();
        if (statement instanceof PreparedStatementProxy) {
            logParameter((PreparedStatementProxy) statement);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param statement
     * @param sql
     * @param resultSet <br>
     */
    @Override
    protected void statementExecuteQueryAfter(final StatementProxy statement, final String sql,
        final ResultSetProxy resultSet) {
        statement.setLastExecuteTimeNano();
        double nanos = statement.getLastExecuteTimeNano();
        double millis = nanos / (GlobalConstants.SECONDS * GlobalConstants.SECONDS);

        String sqlMsg = "{conn-" + statement.getConnectionProxy().getId() + ", " + stmtId(statement) + ", rs-"
            + resultSet.getId() + "} query executed. " + millis + " millis. " + getExcuteSql(statement);
        statementLog(sqlMsg);
        saveMsg2Cache(sqlMsg);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param statement
     * @param sql <br>
     */
    @Override
    protected void statementExecuteUpdateBefore(final StatementProxy statement, final String sql) {
        statement.setLastExecuteStartNano();
        if (statement instanceof PreparedStatementProxy) {
            logParameter((PreparedStatementProxy) statement);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param statement
     * @param sql
     * @param updateCount <br>
     */
    @Override
    protected void statementExecuteUpdateAfter(final StatementProxy statement, final String sql,
        final int updateCount) {
        statement.setLastExecuteTimeNano();
        double nanos = statement.getLastExecuteTimeNano();
        double millis = nanos / (GlobalConstants.SECONDS * GlobalConstants.SECONDS);
        String sqlMsg = "{conn-" + statement.getConnectionProxy().getId() + ", " + stmtId(statement)
            + "} update executed. effort " + updateCount + ". " + millis + " millis. " + getExcuteSql(statement);

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
    public void resultSet_close(final FilterChain chain, final ResultSetProxy resultSet) throws SQLException {
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
    public boolean resultSet_next(final FilterChain chain, final ResultSetProxy resultSet) throws SQLException {
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
    public Object callableStatement_getObject(final FilterChain chain, final CallableStatementProxy statement,
        final int parameterIndex) throws SQLException {
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
    public Object callableStatement_getObject(final FilterChain chain, final CallableStatementProxy statement,
        final int parameterIndex, final java.util.Map<String, Class<?>> map) throws SQLException {
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
    public Object callableStatement_getObject(final FilterChain chain, final CallableStatementProxy statement,
        final String parameterName) throws SQLException {
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
    public Object callableStatement_getObject(final FilterChain chain, final CallableStatementProxy statement,
        final String parameterName, final java.util.Map<String, Class<?>> map) throws SQLException {
        Object obj = chain.callableStatement_getObject(statement, parameterName, map);

        if (obj instanceof ResultSetProxy) {
            resultSetOpenAfter((ResultSetProxy) obj);
        }

        return obj;
    }

    @Override
    protected void resultSetOpenAfter(final ResultSetProxy resultSet) {
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param statement <br>
     */
    protected void statementCreateAfter(final StatementProxy statement) {
        statementLog("{conn-" + statement.getConnectionProxy().getId() + ", stmt-" + statement.getId() + "} created");
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param statement <br>
     */
    protected void statementPrepareAfter(final PreparedStatementProxy statement) {
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
    protected void statementPrepareCallAfter(final CallableStatementProxy statement) {
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
    protected void statement_executeErrorAfter(final StatementProxy statement, final String sql,
        final Throwable error) {
        String sqlMsg = "{conn-" + statement.getConnectionProxy().getId() + ", " + stmtId(statement)
            + "} execute error. " + getExcuteSql(statement);
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
    private String stmtId(final StatementProxy statement) {
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
    protected void logParameter(final PreparedStatementProxy statement) {
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
    public void dataSource_releaseConnection(final FilterChain chain, final DruidPooledConnection conn)
        throws SQLException {
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
     * @author 王伟<br>
     * @taskId <br>
     * @param chain
     * @param ds
     * @param maxWaitMillis
     * @return DruidPooledConnection
     * @throws SQLException <br>
     */
    @Override
    public DruidPooledConnection dataSource_getConnection(final FilterChain chain, final DruidDataSource ds,
        final long maxWaitMillis) throws SQLException {
        DruidPooledConnection conn = chain.dataSource_connect(ds, maxWaitMillis);
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
    public void preparedStatement_clearParameters(final FilterChain chain, final PreparedStatementProxy statement)
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
    private String getExcuteSql(final StatementProxy statement) {
        String sqlStr = statement.getBatchSql();
        if (statement instanceof PreparedStatementProxy) {
            for (JdbcParameter parameter : statement.getParameters().values()) {
                int sqlType = parameter.getSqlType();
                Object value = parameter.getValue();
                String tempValue;
                switch (sqlType) {
                    case Types.NULL:
                        tempValue = "NULL";
                        break;
                    case Types.CLOB:
                    case Types.CHAR:
                    case Types.VARCHAR:
                        tempValue = new StringBuilder().append('\'').append(value).append('\'').toString();
                        break;
                    default:
                        tempValue = String.valueOf(value);
                        break;
                }
                sqlStr = StringUtils.replaceOnce(sqlStr, "?", tempValue);
            }
        }

        return sqlStr;
    }

    /**
     * 保存日志信息岛缓存
     * 
     * @param msg <br>
     */
    private void saveMsg2Cache(final String msg) {
        String statckId = TransManager.getInstance().getStackId();

        ServiceLoader<TransLoggerService> services = getTransLoggerServices();
        if (services != null) {
            for (TransLoggerService service : services) {
                service.sql(statckId, msg);
            }
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
