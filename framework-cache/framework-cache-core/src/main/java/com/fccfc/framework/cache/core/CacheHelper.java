/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.cache.core;

import com.fccfc.framework.common.GlobalConstants;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月24日 <br>
 * @see com.fccfc.framework.core.cache <br>
 */
public final class CacheHelper {

    /** cache */
    private static ICache cache;

    /** stringCache */
    private static IStringCache stringCache;

    public static ICache getCache() {
        return cache;
    }

    public static void setCache(ICache cache) {
        CacheHelper.cache = cache;
    }

    public static IStringCache getStringCache() {
        return stringCache;
    }

    public static void setStringCache(IStringCache stringCache) {
        CacheHelper.stringCache = stringCache;
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
