/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core.annotation.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.InitializationException;
import com.hbasesoft.framework.common.annotation.Key;
import com.hbasesoft.framework.common.utils.UtilException;
import com.hbasesoft.framework.common.utils.bean.BeanUtil;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.db.core.BaseEntity;
import com.hbasesoft.framework.db.core.DaoConstants;
import com.hbasesoft.framework.db.core.annotation.Sql;
import com.hbasesoft.framework.db.core.config.DaoConfig;
import com.hbasesoft.framework.db.core.config.ParamMetadata;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月24日 <br>
 * @see com.hbasesoft.framework.dao.annotation.handler <br>
 */
public class AbstractAnnotationHandler {

    /** sql文件后缀 */
    private static final String SQL_SUFFIX = ".sql";

    /** sql 包名 */
    private static final String SQL_PACKAGE = "/sql";

    /**
     * logger
     */
    private static Logger logger = new Logger(AbstractAnnotationHandler.class);

    /** IGenericBaseDao 的方法签名 */
    private static Map<String, Method> genericBaseDaoMethodMap = new HashMap<String, Method>();

    /** dao config */
    private DaoConfig daoConfig;

    /**
     * 默认构造函数
     * 
     * @param daoConfig
     */
    public AbstractAnnotationHandler(final DaoConfig daoConfig) {
        this.daoConfig = daoConfig;
        if (MapUtils.isEmpty(genericBaseDaoMethodMap)) {
            Class<?> daoClazz = daoConfig.getBaseDaoType();
            if (daoClazz != null) {
                Method[] methods = daoClazz.getDeclaredMethods();
                for (Method m : methods) {
                    genericBaseDaoMethodMap.put(getMethodSignature(m), m);
                }

                // 获取类实现的接口
                Class<?>[] interfaces = daoClazz.getInterfaces();
                if (ArrayUtils.isNotEmpty(interfaces)) {
                    for (Class<?> iface : interfaces) {
                        for (Method method : iface.getMethods()) {
                            if (method.isDefault()) {
                                genericBaseDaoMethodMap.put(getMethodSignature(method), method);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取IGenericBaseDao执行方法
     * 
     * @param method <br>
     * @return <br>
     */
    protected Method getBaseDaoExcutor(final Method method) {
        Method result = null;
        String methodSignature = getMethodSignature(method);
        if (genericBaseDaoMethodMap.containsKey(methodSignature)) {
            result = genericBaseDaoMethodMap.get(methodSignature);
        }
        return result;
    }

    /**
     * 获取对象的方法签名
     * 
     * @param method <br>
     * @return <br>
     */
    private String getMethodSignature(final Method method) {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append(method.getName());
        sbuf.append('(');
        Class<?>[] paramTypes = method.getParameterTypes();
        if (ArrayUtils.isNotEmpty(paramTypes)) {
            for (Class<?> clazz : paramTypes) {
                sbuf.append(BaseEntity.class.isAssignableFrom(clazz) ? Object.class.getName() : clazz.getName())
                    .append(',');
            }
        }
        sbuf.append(')');
        return sbuf.toString();
    }

    /**
     * 检查sql路径
     * 
     * @param method <br>
     * @param path <br>
     * @return <br>
     * @throws InitializationException <br>
     */
    private String checkSqlPath(final Method method, final String path) throws InitializationException {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(path)) {
            sb.append(path);
        }
        else {
            sb.append(method.getDeclaringClass().getName().replace(".", GlobalConstants.PATH_SPLITOR))
                .append(GlobalConstants.UNDERLINE).append(method.getName()).append(SQL_SUFFIX);
        }

        URL sqlFileUrl = this.getClass().getClassLoader().getResource(sb.toString());
        if (sqlFileUrl == null) {
            String dbType = "." + daoConfig.getDbType();
            sb.insert(sb.lastIndexOf(SQL_SUFFIX), dbType);
            sqlFileUrl = this.getClass().getClassLoader().getResource(sb.toString());
            if (sqlFileUrl == null) {
                sb.insert(sb.lastIndexOf(GlobalConstants.PATH_SPLITOR), SQL_PACKAGE);
                sqlFileUrl = this.getClass().getClassLoader().getResource(sb.toString());
                if (sqlFileUrl == null) {
                    sb.replace(sb.lastIndexOf(dbType), sb.lastIndexOf(SQL_SUFFIX), GlobalConstants.BLANK);
                    sqlFileUrl = this.getClass().getClassLoader().getResource(sb.toString());
                    if (sqlFileUrl == null) {
                        throw new InitializationException(ErrorCodeDef.INIT_SQL_ERROR,
                            method.getDeclaringClass().getName(), method.getName());
                    }
                }
            }
        }

        BufferedReader reader = null;
        String sqlPath = sb.toString();
        try {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(sqlPath);
            reader = new BufferedReader(new InputStreamReader(in, GlobalConstants.DEFAULT_CHARSET));
            sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(StringUtils.trim(line)).append('\n');
            }
            return sb.toString();
        }
        catch (Exception e) {
            throw new InitializationException(ErrorCodeDef.CAN_NOT_FIND_SQL_FILE, sqlPath);
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    logger.error("", e);
                }
            }
        }
    }

    /**
     * ParamMetadata
     * 
     * @param method <br>
     * @return <br>
     * @throws InitializationException <br>
     */
    protected ParamMetadata cacheSqlParamMetadata(final Method method) throws InitializationException {
        String key = SqlCacheManager.buildKey(method.getDeclaringClass().getName(),
            BeanUtil.getMethodSignature(method));
        ParamMetadata metadata = null;
        try {
            metadata = SqlCacheManager.getParamMetadata(key);
            if (metadata == null) {
                Class<?>[] typeClazz = method.getParameterTypes();
                Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                metadata = new ParamMetadata(parameterAnnotations.length);

                // 设置返回转换类型
                if (method.isAnnotationPresent(Sql.class)) {
                    Sql sql = method.getAnnotation(Sql.class);
                    metadata.setBeanType(sql.bean());
                    metadata.setDbId(sql.dbId());
                }
                else {
                    metadata.setBeanType(Serializable.class);
                    metadata.setDbId(GlobalConstants.BLANK);
                }

                // 设置返回类型
                metadata.setReturnType((void.class).equals(method.getReturnType()) ? null : method.getReturnType());

                String[] paramNames = null;
                for (int i = 0; i < parameterAnnotations.length; i++) {
                    Annotation[] pmAnnotaions = parameterAnnotations[i];

                    // 如果类型为resultCallback
                    if (daoConfig.getCallBackType() != null
                        && daoConfig.getCallBackType().isAssignableFrom(typeClazz[i])) {
                        if (metadata.getCallBackPosition() != -1) {
                            throw new InitializationException(ErrorCodeDef.ERROR_RESULT_CALL_BACK,
                                method.getDeclaringClass().getName(), method.getName());
                        }
                        metadata.setCallBackPosition(i);
                        continue;
                    }

                    // 如果没有注解参数名称，则获取参数变量名称为key
                    if (pmAnnotaions.length == 0) {
                        if (paramNames == null) {
                            paramNames = BeanUtil.getMethodParamNames(method);
                        }
                        metadata.setParamName(i, paramNames[i]);
                    }
                    else {
                        // 判断是否含有Param注解
                        String name = "NULL";
                        for (Annotation annotation : pmAnnotaions) {
                            if (annotation instanceof Key) {
                                Key p = (Key) annotation;

                                name = p.value();
                                if (DaoConstants.PAGE_INDEX.equals(name)) {
                                    metadata.setIndexPosition(i);
                                }
                                else if (DaoConstants.PAGE_SIZE.equals(name)) {
                                    metadata.setSizePosition(i);
                                }
                                metadata.setParamName(i, name);
                                break;
                            }
                        }

                        // 如果没有Param注解，还是需要自动获取变量名称
                        if (StringUtils.isEmpty(name)) {
                            if (paramNames == null) {
                                paramNames = BeanUtil.getMethodParamNames(method);
                            }
                            metadata.setParamName(i, paramNames[i]);
                        }
                    }
                }
                if ((metadata.getIndexPosition() == -1 && metadata.getSizePosition() != -1)
                    || (metadata.getIndexPosition() != -1 && metadata.getSizePosition() == -1)) {
                    throw new InitializationException(ErrorCodeDef.PAGE_SIZE_PAGE_INDEX_BOTH,
                        method.getDeclaringClass().getName(), method.getName());
                }
                SqlCacheManager.putParamMetadata(key, metadata);
            }
        }
        catch (UtilException e) {
            throw new InitializationException(e);
        }
        catch (Exception e) {
            throw new InitializationException(ErrorCodeDef.FAILURE, e);
        }
        return metadata;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param method
     * @return String
     * @throws InitializationException <br>
     */
    protected String cacheSqlTemplate(final Method method) throws InitializationException {
        String key = SqlCacheManager.buildKey(method.getDeclaringClass().getName(),
            BeanUtil.getMethodSignature(method));
        String templateSql = null;
        try {
            templateSql = SqlCacheManager.getSqlTemplate(key);
            if (StringUtils.isEmpty(templateSql)) {
                String path = null;

                // 获取方法的SQL标签
                if (method.isAnnotationPresent(Sql.class)) {
                    Sql sql = method.getAnnotation(Sql.class);
                    templateSql = sql.value();
                    path = sql.path();
                }

                if (StringUtils.isEmpty(templateSql)) {
                    templateSql = checkSqlPath(method, path);
                }
                SqlCacheManager.putSqlTemplate(key, templateSql);
            }
        }
        catch (Exception e) {
            LoggerUtil.error(e);
            throw new InitializationException(ErrorCodeDef.CACHE_ERROR, e);
        }
        return StringUtils.trim(templateSql);
    }

    /**
     * DaoConfig
     * 
     * @return the daoConfig
     */
    public DaoConfig getDaoConfig() {
        return daoConfig;
    }

    /**
     * setDaoConfig
     * 
     * @param dc the daoConfig to set
     */
    public void setDaoConfig(final DaoConfig dc) {
        this.daoConfig = dc;
    }
}
