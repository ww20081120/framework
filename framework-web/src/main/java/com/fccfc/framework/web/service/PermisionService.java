/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.web.service;

import java.util.List;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.web.bean.resource.MenuPojo;
import com.fccfc.framework.web.bean.resource.UrlResourcePojo;

/** 
 * <Description> <br> 
 *  
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月25日 <br>
 * @since V1.0<br>
 * @see com.fccfc.migu.kf.manger.service <br>
 */
public interface PermisionService {

    /**
     * 
     * Description: queryMenu <br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @param operateId
     * @return
     * @throws ServiceException <br>
     */
    List<MenuPojo> queryMenu(Integer operateId) throws ServiceException;
    
    /**
     * 
     * Description: queryUrlResource<br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @param operateId
     * @return
     * @throws ServiceException <br>
     */
    List<UrlResourcePojo> queryUrlResource(Integer operateId) throws ServiceException;
}
