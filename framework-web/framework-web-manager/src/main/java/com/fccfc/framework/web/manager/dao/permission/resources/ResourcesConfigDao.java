/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.web.manager.dao.permission.resources;

import java.util.List;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.db.hibernate.IGenericBaseDao;
import com.fccfc.framework.web.manager.bean.permission.ResourcesConfigPojo;

/**
 * <Description> Role ORM<br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月17日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.manager.dao.permission <br>
 */
@Dao
public interface ResourcesConfigDao extends IGenericBaseDao {

    /**
     * Description: 查询角色<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param roleName
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws DaoException <br>
     */
    @Sql(bean = ResourcesConfigPojo.class)
    List<ResourcesConfigPojo> selectResourcesConfig(@Param("moduleCode") String moduleCode,@Param("resourceId") String resourceId) throws DaoException;

}
