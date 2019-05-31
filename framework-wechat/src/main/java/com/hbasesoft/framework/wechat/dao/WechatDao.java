/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.wechat.dao;

import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.annotation.Dao;
import com.hbasesoft.framework.db.core.annotation.Param;
import com.hbasesoft.framework.db.core.annotation.Sql;
import com.hbasesoft.framework.db.hibernate.IGenericBaseDao;
import com.hbasesoft.framework.wechat.bean.ChangeQrcodeParamPojo;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年4月17日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.wechat.dao <br>
 */
@Dao
public interface WechatDao extends IGenericBaseDao {

	@Sql(bean = ChangeQrcodeParamPojo.class)
    ChangeQrcodeParamPojo getChangeQrcodeParamPojo(@Param("orgCode") String orgCode, @Param("appId") String appId, @Param("usedFlag") String usedFlag) throws DaoException;

}
