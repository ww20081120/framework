/**
 * 
 */
package com.fccfc.framework.db.core.annotation.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ognl.Ognl;
import ognl.OgnlException;

import org.apache.commons.lang.StringUtils;

import com.fccfc.framework.cache.core.CacheHelper;
import com.fccfc.framework.common.ErrorCodeDef;
import com.fccfc.framework.common.GlobalConstants;
import com.fccfc.framework.common.InitializationException;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.common.utils.UtilException;
import com.fccfc.framework.common.utils.bean.BeanUtil;
import com.fccfc.framework.common.utils.engine.VelocityParseFactory;
import com.fccfc.framework.common.utils.logger.Logger;
import com.fccfc.framework.db.core.DaoConstants;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.config.DataParam;
import com.fccfc.framework.db.core.config.ParamMetadata;
import com.fccfc.framework.db.core.executor.ISqlExcutor;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年10月27日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.dao.annotation.handler <br>
 */
public class DaoHandler extends AbstractAnnotationHandler implements InvocationHandler {

    // 表示以：开头的合法变量名
    private static final String REG_EX = ":[ tnx0Bfr]*[_a-zA-Z][_a-zA-Z.0-9]*";

    private static Logger logger = new Logger(DaoHandler.class);

    private static Pattern pat = Pattern.compile(REG_EX);

    private ISqlExcutor sqlExcutor;

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Step1:判断是否是抽象方法，如果是非抽象方法，则不执行代理拦截器
        if (proxy != null && !BeanUtil.isAbstract(method)) {
            return method.invoke(proxy, args);
        }

        // Step2:判断是否是genericBaseDao方法，如果是则直接执行
        Method m = getBaseDaoExcutor(method);
        if (m != null) {
            return m.invoke(super.getDaoConfig().getBaseDao(), args);
        }

        // Step3: 装载SQL模板和所需参数
        String templateSql = cacheSqlTemplate(method);

        // 执行sql的参数
        DataParam dataParam = new DataParam();

        // SQL模板参数
        Map<String, Object> sqlParamsMap = loadDaoMetaData(method, args, dataParam);

        // Step4:解析SQL模板，返回可执行SQL
        String executeSql = parseSqlTemplate(method, templateSql, sqlParamsMap);

        // Step5:组装sql参数
        installPlaceholderSqlParam(dataParam, executeSql, sqlParamsMap);

        // Step5: 执行Sql返回结果
        return excuteSql(executeSql, dataParam);
    }

    /**
     * 组装占位符参数 -> Map
     * 
     * @param dataParam
     * @param executeSql
     * @return
     * @throws DaoException
     * @throws OgnlException
     */
    private void installPlaceholderSqlParam(DataParam dataParam, String executeSql, Map<String, Object> sqlParamsMap)
        throws DaoException {
        Map<String, Object> map = new HashMap<String, Object>();

        Matcher m = pat.matcher(executeSql);
        try {
            while (m.find()) {
                logger.debug(" Match [" + m.group() + "] at positions " + m.start() + "-" + (m.end() - 1));
                String ognl_key = m.group().replace(":", "").trim();
                map.put(ognl_key, Ognl.getValue(ognl_key, sqlParamsMap));
            }
        }
        catch (Exception e) {
            throw new DaoException(ErrorCodeDef.OGNL_ERROR_10009, e);
        }
        dataParam.setParamMap(map);
    }

    /**
     * 获取所需参数
     * 
     * @param method
     * @param args
     * @param dataParam
     * @return
     * @throws DaoException
     */
    private Map<String, Object> loadDaoMetaData(Method method, Object[] args, DataParam dataParam) throws DaoException {
        Map<String, Object> paramMap;
        ParamMetadata metadata;
        try {
            metadata = super.cacheSqlParamMetadata(method);
        }
        catch (InitializationException e) {
            throw new DaoException(e);
        }
        dataParam.setBeanType(metadata.getBeanType());
        dataParam.setReturnType(metadata.getReturnType());
        dataParam.setDbId(metadata.getDbId());

        String[] paramNames = metadata.getParamNames();
        paramMap = new HashMap<String, Object>();
        if (CommonUtil.isNotEmpty(args)) {
            for (int i = 0; i < args.length; i++) {
                if (i == metadata.getIndexPosition()) {
                    dataParam.setPageIndex(Integer.valueOf(args[i].toString()));
                }
                else if (i == metadata.getSizePosition()) {
                    dataParam.setPageSize(Integer.valueOf(args[i].toString()));
                }
                else if (i == metadata.getCallBackPosition()) {
                    dataParam.setCallback(args[i]);
                }
                else {
                    paramMap.put(paramNames[i], args[i]);
                }
            }
        }

        return paramMap;
    }

    /**
     * @param method
     * @param sql
     * @param param
     * @return
     * @throws DaoException
     */
    private Object excuteSql(String sql, DataParam param) throws DaoException {
        Object result = null;
        // 区分查询方法与执行方法
        if (sql.toLowerCase().startsWith(DaoConstants.SQL_SELECT_PREFIX)) {
            result = sqlExcutor.query(sql, param);
        }
        else if (sql.indexOf(GlobalConstants.SQL_SPLITOR) != -1) {
            result = sqlExcutor.batchExcuteSql(StringUtils.split(sql, GlobalConstants.SQL_SPLITOR), param);
        }
        else {
            result = sqlExcutor.excuteSql(sql, param);
        }
        return result;
    }

    /**
     * @param method
     * @param templateSql
     * @param sqlParamsMap
     * @return
     * @throws UtilException
     */
    private String parseSqlTemplate(Method method, String templateSql, Map<String, Object> sqlParamsMap)
        throws UtilException {
        String key = CacheHelper.buildKey(method.getDeclaringClass().getName(), BeanUtil.getMethodSignature(method));
        String executeSql = VelocityParseFactory.parse(key, templateSql, sqlParamsMap);

        // 除去无效字段，不然批量处理可能报错
        return executeSql.replaceAll("\\n", " ").replaceAll("\\t", " ").replaceAll("\\s{1,}", " ").trim();
    }

    public void setSqlExcutor(ISqlExcutor sqlExcutor) {
        this.sqlExcutor = sqlExcutor;
    }
}
