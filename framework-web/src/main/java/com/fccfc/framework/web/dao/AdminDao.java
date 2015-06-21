/**************************************************************************************** 
 Copyright © 2003-2012 ZTEsoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.web.dao;

import com.fccfc.framework.api.bean.operator.AdminPojo;
import com.fccfc.framework.core.db.DaoException;
import com.fccfc.framework.core.db.annotation.DAO;
import com.fccfc.framework.core.db.annotation.Param;
import com.fccfc.framework.core.db.annotation.Sql;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2015年6月3日 <br>
 * @see com.fccfc.framework.web.dao <br>
 */
@DAO
public interface AdminDao {

    @Sql("SELECT * FROM ADMIN A WHERE A.ADMIN_ID = :id")
    AdminPojo getAdminById(@Param("id") Integer id) throws DaoException;
}
