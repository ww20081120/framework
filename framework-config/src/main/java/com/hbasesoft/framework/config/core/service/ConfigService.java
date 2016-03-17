/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.config.core.service;

import java.util.List;

import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.config.core.bean.Config;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年3月17日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.config.core.service <br>
 */
public interface ConfigService {

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param moduleCode <br>
     * @return <br>
     * @throws org.apache.thrift.TException <br>
     */
    public List<Config> queryAllConfig(String moduleCode) throws ServiceException;

    public String queryConfig(String moduleCode, String configItemCode, String paramCode) throws ServiceException;
}
