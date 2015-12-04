/****************************************************************************************
 * Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 * transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 * or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.web.manager.dao.permission.admin;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.db.hibernate.IGenericBaseDao;
import com.fccfc.framework.web.manager.bean.permission.AccountPojo;
import com.fccfc.framework.web.manager.bean.permission.AdminPojo;
import com.fccfc.framework.web.manager.bean.permission.OperatorPojo;

/**
 * <Description> <br>
 *
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年12月4日 <br>
 * @see com.fccfc.framework.web.manager.dao.permission.admin <br>
 */
@Dao
public interface AccountDao extends IGenericBaseDao {

    void deleteAccountById(@Param("ids") Integer[] ids,@Param("state") String state);


}
