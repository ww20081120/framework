/**************************************************************************************** 
 Copyright © 2022-2027 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core.annotation.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.db.core.config.ParamMetadata;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2022年10月27日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.core.annotation.handler <br>
 */
public class SqlCacheManager {

    /** */
    private static Map<String, ParamMetadata> metadataCache = new ConcurrentHashMap<>();

    /** */
    private static Map<String, String> sqlTemplateCache = new ConcurrentHashMap<>();

    /**
     * Description: <br>
     * 
     * @author 王伟 <br>
     * @param paths <br>
     * @return <br>
     */
    public static String buildKey(final String... paths) {
        StringBuilder sb = new StringBuilder();
        for (String path : paths) {
            sb.append(GlobalConstants.PATH_SPLITOR).append(path);
        }
        return sb.toString();
    }

    /**
     * @Method getParamMetadata
     * @param key
     * @return com.hbasesoft.framework.db.core.config.ParamMetadata
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 14:26
    */
    public static ParamMetadata getParamMetadata(final String key) {
        return metadataCache.get(key);
    }

    /**
     * @Method putParamMetadata
     * @param key
     * @param metadata
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 14:26
    */
    public static void putParamMetadata(final String key, final ParamMetadata metadata) {
        metadataCache.put(key, metadata);
    }

    /**
     * @Method getSqlTemplate
     * @param key
     * @return java.lang.String
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 14:26
    */
    public static String getSqlTemplate(final String key) {
        return sqlTemplateCache.get(key);
    }

    /**
     * @Method putSqlTemplate
     * @param key
     * @param sql
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 14:27
    */
    public static void putSqlTemplate(final String key, final String sql) {
        sqlTemplateCache.put(key, sql);
    }
}
