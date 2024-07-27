/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.io;

import java.util.List;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年2月6日 <br>
 * @since V1.0<br>
 * @param <T> T
 * @see com.hbasesoft.framework.common.utils.io <br>
 */
@FunctionalInterface
public interface BatchProcessor<T> {
    
    /**
     * 
     * Description: <br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @param beanList
     * @param pageIndex
     * @param pageSize
     * @return <br>
     */
    boolean process(List<T> beanList, int pageIndex, int pageSize);
}
