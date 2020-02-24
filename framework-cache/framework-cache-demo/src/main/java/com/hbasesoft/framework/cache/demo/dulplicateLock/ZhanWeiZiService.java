/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.demo.dulplicateLock;

import com.hbasesoft.framework.cache.core.annotation.DulplicateLock;
import com.hbasesoft.framework.cache.core.annotation.Key;
import com.hbasesoft.framework.common.GlobalConstants;

/**
 * <Description> 抢位子服务<br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年1月5日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.test.lock <br>
 */
public interface ZhanWeiZiService {

    /**
     * 
     * Description: <br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @param no
     * @param name <br>
     */
    @DulplicateLock(name = "zhanwei", key = "${no}", expireTime = GlobalConstants.ONE_SECONDS)
    void rob(@Key("no") int no, String name);
}
