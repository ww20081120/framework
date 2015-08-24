package com.fccfc.framework.task.core.dao;

import java.util.List;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.db.hibernate.IGenericBaseDao;
import com.fccfc.framework.task.core.bean.ChangeNotifRedisPojo;

/**
 * 
 * <Description> <br> 
 *  
 * @author yang.zhipeng <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年8月24日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.task.core.dao <br>
 */
@Dao
public interface RedisCacheDao extends IGenericBaseDao {

    /**
     * Description: <br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param num <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = ChangeNotifRedisPojo.class)
    List<ChangeNotifRedisPojo> getChangeNotifRedis(@Param("num") int num) throws DaoException;
}
