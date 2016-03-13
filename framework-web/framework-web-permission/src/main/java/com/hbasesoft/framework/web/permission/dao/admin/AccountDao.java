/****************************************************************************************
 * Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 * transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 * or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.web.permission.dao.admin;

import com.hbasesoft.framework.db.core.annotation.Dao;
import com.hbasesoft.framework.db.core.annotation.Param;
import com.hbasesoft.framework.db.hibernate.IGenericBaseDao;

/**
 * <Description> <br>
 *
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年12月4日 <br>
 * @see com.hbasesoft.framework.web.permission.dao.admin <br>
 */
@Dao
public interface AccountDao extends IGenericBaseDao {

    void deleteAccountById(@Param("ids") Integer[] ids, @Param("state") String state);

}
