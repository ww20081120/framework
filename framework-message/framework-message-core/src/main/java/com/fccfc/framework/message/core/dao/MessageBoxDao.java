/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.message.core.dao;

import java.util.Date;
import java.util.List;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.db.hibernate.IGenericBaseDao;
import com.fccfc.framework.message.core.bean.AttachmentsPojo;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年12月11日 <br>
 * @see com.fccfc.framework.message.dao <br>
 */
@Dao
public interface MessageBoxDao extends IGenericBaseDao {

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param date <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = Long.class)
    List<Long> selectMessageBox(@Param("date") Date date) throws DaoException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param messageId <br>
     * @param attachmentId <br>
     * @return <br>
     * @throws DaoException <br>
     */
    int saveMessageAttachments(@Param("messageId") long messageId, @Param("attachmentId") long attachmentId)
        throws DaoException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param messageId <br>
     * @return <br>
     */
    @Sql(bean = AttachmentsPojo.class)
    List<AttachmentsPojo> selectMessageAttachments(@Param("messageId") long messageId);

}
