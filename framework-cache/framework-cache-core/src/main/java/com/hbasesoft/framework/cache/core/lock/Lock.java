/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.core.lock;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2020年12月3日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.cache.core <br>
 */
public interface Lock {

    /**
     * Description: 阻塞的锁 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param timeout <br>
     */
    void lock(int timeout);

    /**
     * Description: 非阻塞的锁 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param timeout
     * @return <br>
     */
    boolean tryLock(int timeout);

    /**
     * Description: 解锁 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    void unlock();
}
