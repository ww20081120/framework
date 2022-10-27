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

    private static Map<String, ParamMetadata> metadataCache = new ConcurrentHashMap<>();

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

    public static ParamMetadata getParamMetadata(String key) {
        return metadataCache.get(key);
    }

    public static void putParamMetadata(String key, ParamMetadata metadata) {
        metadataCache.put(key, metadata);
    }

    public static String getSqlTemplate(String key) {
        return sqlTemplateCache.get(key);
    }

    public static void putSqlTemplate(String key, String sql) {
        sqlTemplateCache.put(key, sql);
    }
}
