package com.fccfc.framework.task.core.dao;

import java.util.List;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.db.hibernate.IGenericBaseDao;
import com.fccfc.framework.task.core.bean.ChangeNotifRedisPojo;
import com.fccfc.framework.task.core.bean.TaskPojo;

@Dao
public interface RedisCacheDao extends IGenericBaseDao {

	/**
	 * Description: <br> 
	 *  
	 * @author shao.dinghui<br>
	 * @taskId <br>
	 * @param redisPojo
	 * @return
	 * @throws DaoException <br>
	 */
	@Sql(bean = ChangeNotifRedisPojo.class)
	List<ChangeNotifRedisPojo> getChangeNotifRedis(@Param("num") int num) throws DaoException;
}
