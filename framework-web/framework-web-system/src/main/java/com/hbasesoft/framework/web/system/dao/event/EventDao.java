/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.web.system.dao.event;

import java.util.List;

import com.hbasesoft.framework.web.system.bean.EventPojo;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.annotation.Dao;
import com.hbasesoft.framework.db.core.annotation.Param;
import com.hbasesoft.framework.db.core.annotation.Sql;
import com.hbasesoft.framework.db.hibernate.IGenericBaseDao;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年12月4日 <br>
 * @see com.hbasesoft.framework.web.dao.operator <br>
 */
@Dao
public interface EventDao extends IGenericBaseDao {

    /** 查询所有 */
    @Sql(bean = EventPojo.class)
    List<EventPojo> selectList(@Param(Param.PAGE_INDEX) Integer pageIndex, @Param(Param.PAGE_SIZE) Integer pageSize)
        throws DaoException;

    @Sql("SELECT E.EVENT_ID, E.EVENT_CODE, E.EVENT_TYPE, E.PARAMS_NAME, E.EVENT_NAME, E.REMARK FROM EVENT E WHERE E.EVENT_CODE = :eventCode")
    EventPojo queryByCode(@Param("eventCode") String eventCode) throws DaoException;
}
