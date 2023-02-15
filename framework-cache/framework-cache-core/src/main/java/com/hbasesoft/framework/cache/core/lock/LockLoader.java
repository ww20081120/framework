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
public interface LockLoader {
    /**
     * @Method getInstance
     * @param lockName
     * @return com.hbasesoft.framework.cache.core.lock.Lock
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 11:52
    */
    Lock getInstance(String lockName);
}
