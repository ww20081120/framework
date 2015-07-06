/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.message.core.dao;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.db.hibernate.IGenericBaseDao;
import com.fccfc.framework.message.core.bean.MessageTemplatePojo;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年12月11日 <br>
 * @see com.fccfc.framework.message.dao <br>
 */
@Dao
public interface MessageTemplateDao extends IGenericBaseDao {

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param code <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql("SELECT * FROM MESSAGE_TEMPLATE M WHERE M.STATE = 'A' AND M.MESSAGE_TEMPLATE_CODE = :code")
    MessageTemplatePojo getByCode(@Param("code") String code) throws DaoException;
}
