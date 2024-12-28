/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.bean;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2024年12月11日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.utils.bean <br>
 */
@FunctionalInterface
public interface ClassFilter {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param clazz
     * @return <br>
     */
    boolean accept(Class<?> clazz);
}
