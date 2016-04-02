/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.core;

import java.util.ServiceLoader;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.PropertyHolder;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月24日 <br>
 * @see com.hbasesoft.framework.core.cache <br>
 */
public final class CacheHelper {

    /** cache */
    private static ICache cache;

    public static ICache getCache() {

        if (cache == null) {
            String cacheModel = PropertyHolder.getProperty("cache.model");
            Assert.notEmpty(cacheModel, "未配置缓存模式 cache.model");

            ServiceLoader<ICache> serviceLoader = ServiceLoader.load(ICache.class);
            for (ICache c : serviceLoader) {
                if (cacheModel.equals(c.getName())) {
                    cache = c;
                    break;
                }
            }

            if (cache == null) {
                throw new RuntimeException("未找到缓存实现类 或者 cache.model 配置不正确");
            }
        }

        return cache;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟 <br>
     * @param paths <br>
     * @return <br>
     */
    public static String buildKey(String... paths) {
        StringBuilder sb = new StringBuilder();
        for (String path : paths) {
            sb.append(GlobalConstants.PATH_SPLITOR).append(path);
        }
        return sb.toString();
    }

}
