/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.db.core.annotation.handler;

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

import org.apache.commons.lang.StringUtils;

import com.fccfc.framework.cache.core.CacheConstant;
import com.fccfc.framework.cache.core.CacheException;
import com.fccfc.framework.cache.core.CacheHelper;
import com.fccfc.framework.common.ErrorCodeDef;
import com.fccfc.framework.common.GlobalConstants;
import com.fccfc.framework.common.InitializationException;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.common.utils.UtilException;
import com.fccfc.framework.common.utils.bean.BeanUtil;
import com.fccfc.framework.common.utils.logger.Logger;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.db.core.config.DaoConfig;
import com.fccfc.framework.db.core.config.ParamMetadata;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月24日 <br>
 * @see com.fccfc.framework.dao.annotation.handler <br>
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
    private static Map<String, Method> genericBaseDaoMethodMap;

    /**
     * daoConfig
     */
    private DaoConfig daoConfig;

    /**
     * 默认构造函数
     */
    public AbstractAnnotationHandler() {
    }

    /**
     * 获取IGenericBaseDao执行方法
     * 
     * @param method <br>
     * @return <br>
     */
    protected Method getBaseDaoExcutor(Method method) {
        Method result = null;
        Object obj = daoConfig.getBaseDao();
        if (obj != null) {
            if (genericBaseDaoMethodMap == null) {
                genericBaseDaoMethodMap = new HashMap<String, Method>();
                Method[] methods = obj.getClass().getDeclaredMethods();
                for (Method m : methods) {
                    genericBaseDaoMethodMap.put(BeanUtil.getMethodSignature(m), m);
                }
            }
            String methodSignature = BeanUtil.getMethodSignature(method);
            if (genericBaseDaoMethodMap.containsKey(methodSignature)) {
                result = genericBaseDaoMethodMap.get(methodSignature);
            }
        }
        return result;
    }

    /**
     * 检查sql路径
     * 
     * @param method <br>
     * @param path <br>
     * @return <br>
     * @throws InitializationException <br>
     */
    private String checkSqlPath(Method method, String path) throws InitializationException {
        StringBuilder sb = new StringBuilder();
        if (CommonUtil.isNotEmpty(path)) {
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
                        throw new InitializationException(ErrorCodeDef.INIT_SQL_ERROR_10005, "初始化sql失败，未找到{0}#{1}的sql",
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
            throw new InitializationException(ErrorCodeDef.CAN_NOT_FIND_SQL_FILE_10006, "读取sql文件失败,路径[{0}]", sqlPath);
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
     * @param method <br>
     * @return <br>
     * @throws InitializationException <br>
     */
    protected ParamMetadata cacheSqlParamMetadata(Method method) throws InitializationException {
        String key = CacheHelper.buildKey(method.getDeclaringClass().getName(), BeanUtil.getMethodSignature(method));
        ParamMetadata metadata = null;
        try {
            metadata = daoConfig.isCache() ? (ParamMetadata) CacheHelper.getCache().getValue(
                CacheConstant.SQL_PARAM_DIR, key) : null;
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
                            throw new InitializationException(ErrorCodeDef.ERROR_RESULT_CALL_BACK_10007,
                                "Clazz[{0}] Method[{1}]含有多个ResultCallback参数", method.getDeclaringClass().getName(),
                                method.getName());
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
                        String name = GlobalConstants.NULL;
                        for (Annotation annotation : pmAnnotaions) {
                            if (annotation instanceof Param) {
                                Param p = (Param) annotation;
                                name = p.value();
                                if (Param.PAGE_INDEX.equals(name)) {
                                    metadata.setIndexPosition(i);
                                }
                                else if (Param.PAGE_SIZE.equals(name)) {
                                    metadata.setSizePosition(i);
                                }
                                metadata.setParamName(i, name);
                                break;
                            }
                        }

                        // 如果没有Param注解，还是需要自动获取变量名称
                        if (CommonUtil.isEmpty(name)) {
                            if (paramNames == null) {
                                paramNames = BeanUtil.getMethodParamNames(method);
                            }
                            metadata.setParamName(i, paramNames[i]);
                        }
                    }
                }
                if ((metadata.getIndexPosition() == -1 && metadata.getSizePosition() != -1)
                    || (metadata.getIndexPosition() != -1 && metadata.getSizePosition() == -1)) {
                    throw new InitializationException(ErrorCodeDef.PAGE_SIZE_PAGE_INDEX_BOTH_10008,
                        "Clazz[{0}] Method[{1}]中Pagesize 和 PageIndex 必须同时设置", method.getDeclaringClass().getName(),
                        method.getName());
                }
                if (daoConfig.isCache()) {
                    CacheHelper.getCache().putValue(CacheConstant.SQL_PARAM_DIR, key, metadata);
                }
            }
        }
        catch (UtilException e) {
            throw new InitializationException(e);
        }
        catch (CacheException e) {
            throw new InitializationException(e);
        }
        catch (Exception e) {
            throw new InitializationException(ErrorCodeDef.SYSTEM_ERROR_10001, e);
        }
        return metadata;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟 <br>
     * @param method <br>
     * @throws InitializationException InitializationException
     */
    protected String cacheSqlTemplate(Method method) throws InitializationException {
        String key = CacheHelper.buildKey(method.getDeclaringClass().getName(), BeanUtil.getMethodSignature(method));
        String templateSql = null;
        try {
            templateSql = daoConfig.isCache() ? CacheHelper.getStringCache().getValue(CacheConstant.SQL_DIR, key)
                : null;
            if (CommonUtil.isEmpty(templateSql)) {
                String path = null;

                // 获取方法的SQL标签
                if (method.isAnnotationPresent(Sql.class)) {
                    Sql sql = method.getAnnotation(Sql.class);
                    templateSql = sql.value();
                    path = sql.path();
                }

                if (CommonUtil.isEmpty(templateSql)) {
                    templateSql = checkSqlPath(method, path);
                }
                if (daoConfig.isCache()) {
                    CacheHelper.getStringCache().putValue(CacheConstant.SQL_DIR, key, templateSql);
                }
            }
        }
        catch (CacheException e) {
            throw new InitializationException(e);
        }
        return StringUtils.trim(templateSql);
    }

    /**
     * DaoConfig
     * @return the daoConfig
     */
    public DaoConfig getDaoConfig() {
        return daoConfig;
    }

    /**
     * setDaoConfig
     * @param daoConfig the daoConfig to set
     */
    public void setDaoConfig(DaoConfig daoConfig) {
        this.daoConfig = daoConfig;
    }
}
