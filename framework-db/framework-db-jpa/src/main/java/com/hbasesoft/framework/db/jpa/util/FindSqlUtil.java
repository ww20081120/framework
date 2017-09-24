/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.jpa.util;

import java.lang.reflect.Method;
import java.net.URL;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.InitializationException;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.io.IOUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月24日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.jpa <br>
 */
public final class FindSqlUtil {

    /** sql文件后缀 */
    private static final String SQL_SUFFIX = ".sql";

    /** sql 包名 */
    private static final String SQL_PACKAGE = "/sql";

    private FindSqlUtil() {
    }

    /**
     * 检查sql路径
     * 
     * @param method <br>
     * @param path <br>
     * @return <br>
     * @throws InitializationException <br>
     */
    public static String checkSqlPath(Method method) throws InitializationException {
        StringBuilder sb = new StringBuilder();
        sb.append(method.getDeclaringClass().getName().replace(".", GlobalConstants.PATH_SPLITOR))
            .append(GlobalConstants.UNDERLINE).append(method.getName()).append(SQL_SUFFIX);

        ClassLoader classLoader = FindSqlUtil.class.getClassLoader();

        URL sqlFileUrl = classLoader.getResource(sb.toString());
        if (sqlFileUrl == null) {
            String dbType = "." + PropertyHolder.getProperty("master.db.type", "mysql");
            sb.insert(sb.lastIndexOf(SQL_SUFFIX), dbType);
            sqlFileUrl = classLoader.getResource(sb.toString());
            if (sqlFileUrl == null) {
                sb.insert(sb.lastIndexOf(GlobalConstants.PATH_SPLITOR), SQL_PACKAGE);
                sqlFileUrl = FindSqlUtil.class.getClassLoader().getResource(sb.toString());
                if (sqlFileUrl == null) {
                    sb.replace(sb.lastIndexOf(dbType), sb.lastIndexOf(SQL_SUFFIX), GlobalConstants.BLANK);
                    sqlFileUrl = FindSqlUtil.class.getClassLoader().getResource(sb.toString());
                    if (sqlFileUrl == null) {
                        throw new InitializationException(ErrorCodeDef.INIT_SQL_ERROR_10005,
                            method.getDeclaringClass().getName(), method.getName());
                    }
                }
            }
        }

        return IOUtil.readString(classLoader.getResourceAsStream(sb.toString())).replaceAll("\\n", " ")
            .replaceAll("\\t", " ").replaceAll("\\s{1,}", " ").trim();

    }
}
