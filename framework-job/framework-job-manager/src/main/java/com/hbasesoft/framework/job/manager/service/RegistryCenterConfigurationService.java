/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.job.manager.service;

import java.util.List;

import com.hbasesoft.framework.job.manager.bean.RegistryCenterConfiguration;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年4月14日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.vcc.sgp.plat.job.manager.service <br>
 */
public interface RegistryCenterConfigurationService {

    List<RegistryCenterConfiguration> queryAll();

    RegistryCenterConfiguration get(String id);

    RegistryCenterConfiguration add(RegistryCenterConfiguration entity);

    void delete(String id);
}
