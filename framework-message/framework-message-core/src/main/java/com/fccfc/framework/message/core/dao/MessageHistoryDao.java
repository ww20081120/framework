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
import com.fccfc.framework.message.core.bean.MessageHistoryPojo;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年12月11日 <br>
 * @see com.fccfc.framework.message.dao <br>
 */
@Dao
public interface MessageHistoryDao {

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param messageHistoryPojo <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql("INSERT INTO MESSAGE_HISTORY VALUES (:history.messageId,:history.receivers,:history.sender,"
        + ":history.messageType,:history.messageTemplateId,:history.subject,:history.content,:history."
        + "attachmentsNum,:history.createTime,:history.sendTime,:history.sendTimes,:history.result,:history.expDate,:history.extendAttrs)")
    int saveHistory(@Param("history") MessageHistoryPojo messageHistoryPojo) throws DaoException;

}
