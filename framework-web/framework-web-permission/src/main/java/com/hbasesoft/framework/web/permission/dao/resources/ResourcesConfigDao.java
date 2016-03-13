/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.web.permission.dao.resources;

import java.util.List;

import com.hbasesoft.framework.web.permission.bean.ResourcesConfigPojo;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.annotation.Dao;
import com.hbasesoft.framework.db.core.annotation.Param;
import com.hbasesoft.framework.db.core.annotation.Sql;
import com.hbasesoft.framework.db.hibernate.IGenericBaseDao;

/**
 * <Description> Role ORM<br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月17日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.web.manager.dao.permission <br>
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
