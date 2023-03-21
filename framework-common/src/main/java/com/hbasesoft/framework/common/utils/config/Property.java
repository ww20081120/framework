/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.config;

import java.util.Map;

/**
 * <Description> 配置项<br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2023年2月15日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.utils.bean <br>
 */
public interface Property {

    /**
     * Description: 获取配置项<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param property property
     * @return <br>
     */
    String getProperty(String property);

    /**
     * Description: 获取所有配置项<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    Map<String, String> getProperties();
}
