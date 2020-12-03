/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.simple;

import com.hbasesoft.framework.cache.core.lock.DefaultLock;
import com.hbasesoft.framework.cache.core.lock.Lock;
import com.hbasesoft.framework.cache.core.lock.LockLoader;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2020年12月3日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.cache.simple <br>
 */
public class SimpleLockLoader implements LockLoader {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param lockName
     * @return <br>
     */
    @Override
    public Lock getInstance(String lockName) {
        return new DefaultLock(lockName);
    }

}
