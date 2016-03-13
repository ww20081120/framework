/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.core.dao;

import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.annotation.Dao;
import com.hbasesoft.framework.db.core.annotation.Param;
import com.hbasesoft.framework.message.core.bean.MessageHistoryPojo;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年12月11日 <br>
 * @see com.hbasesoft.framework.message.dao <br>
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
    int saveHistory(@Param("history") MessageHistoryPojo messageHistoryPojo) throws DaoException;

}
