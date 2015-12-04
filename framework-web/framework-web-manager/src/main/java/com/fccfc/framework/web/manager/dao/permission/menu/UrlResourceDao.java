package com.fccfc.framework.web.manager.dao.permission.menu;

import java.util.List;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.db.hibernate.IGenericBaseDao;
import com.fccfc.framework.web.manager.bean.permission.UrlResourcePojo;

/**
 * <Description>Url资源Dao <br>
 *
 * @author 胡攀<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月21日 <br>
 * @see com.fccfc.framework.web.manager.dao.permission.menu <br>
 * @since V1.0<br>
 */
@Dao
public interface UrlResourceDao extends IGenericBaseDao {

	/**
	 * Description:查询所有的Url资源 <br>
	 *
	 * @return
	 * @throws DaoException
	 * <br>
	 * @author 胡攀<br>
	 * @taskId <br>
	 */
	@Sql(bean = UrlResourcePojo.class)
	List<UrlResourcePojo> selectUrl() throws DaoException;

	/**
	 * Description: 根据resourceName分页查询所有的资源<br>
	 *
	 * @param functionId
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws DaoException
	 * <br>
	 * @author 胡攀<br>
	 * @taskId <br>
	 */
	@Sql(bean = UrlResourcePojo.class)
	List<UrlResourcePojo> selectUrl(@Param("functionId") Long functionId,
			@Param(Param.PAGE_INDEX) Integer pageIndex,
			@Param(Param.PAGE_SIZE) Integer pageSize) throws DaoException;

	/**
	 * Description: 根据resourceIds批量删除URL资源 <br>
	 *
	 * @param resourceIds
	 * @return
	 * @throws DaoException
	 * <br>
	 * @author 胡攀<br>
	 * @taskId <br>
	 */
	@Sql(bean = UrlResourcePojo.class)
	int deleteByResourceId(@Param("resourceIds") Long[] resourceIds)
			throws DaoException;

	/**
	 * Description: 修改URL_RESOURCE资源<br>
	 *
	 * @param urlResourcePojo
	 * @return
	 * @throws DaoException
	 * <br>
	 * @author 胡攀<br>
	 * @taskId <br>
	 */
	@Sql(value = "UPDATE URL_RESOURCE SET RESOURCE_NAME = :urlResourcePojo.resourceName,URL = :urlResourcePojo.url,METHOD = :urlResourcePojo.method, EVENT_ID = :urlResourcePojo.eventId WHERE RESOURCE_ID = :urlResourcePojo.resourceId")
	int update(@Param("urlResourcePojo") UrlResourcePojo urlResourcePojo)
			throws DaoException;

	@Sql(bean = UrlResourcePojo.class)
	List<UrlResourcePojo> selectListByModule(
			@Param("moduleCodes") List<String> moduleCodes) throws DaoException;
}
