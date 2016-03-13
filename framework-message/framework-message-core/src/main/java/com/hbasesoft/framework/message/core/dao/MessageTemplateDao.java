/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.core.dao;

import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.annotation.Dao;
import com.hbasesoft.framework.db.core.annotation.Param;
import com.hbasesoft.framework.db.hibernate.IGenericBaseDao;
import com.hbasesoft.framework.message.core.bean.MessageTemplatePojo;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年12月11日 <br>
 * @see com.hbasesoft.framework.message.dao <br>
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
    MessageTemplatePojo getByCode(@Param("code") String code) throws DaoException;
}
