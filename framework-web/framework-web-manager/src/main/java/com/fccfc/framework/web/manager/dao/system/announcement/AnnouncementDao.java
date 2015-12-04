package com.fccfc.framework.web.manager.dao.system.announcement;

import java.util.List;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.db.hibernate.IGenericBaseDao;
import com.fccfc.framework.web.manager.bean.system.AnnouncementPojo;

/**
 * <Description> <br>
 * 
 * @author liu.baiyang<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月30日 <br>
 * @since bps<br>
 * @see com.fccfc.framework.web.manager.dao.system.announcement <br>
 */
@Dao
public interface AnnouncementDao extends IGenericBaseDao {

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param pageIndex <br>
     * @param pageSize <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = AnnouncementPojo.class)
    List<AnnouncementPojo> listAnnouncement(@Param(Param.PAGE_INDEX) int pageIndex, @Param(Param.PAGE_SIZE) int pageSize)
        throws DaoException;

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param pojo <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = AnnouncementPojo.class)
    List<AnnouncementPojo> qryAnnouncement(@Param("pojo") AnnouncementPojo pojo) throws DaoException;

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param pojo <br>
     * @return <br>
     * @throws DaoException <br>
     */
    int insertAnnouncement(@Param("pojo") AnnouncementPojo pojo) throws DaoException;

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param pojo <br>
     * @param isStateChange
     * @throws DaoException <br>
     */
    void modifyAnnouncement(@Param("pojo") AnnouncementPojo pojo) throws DaoException;

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param ids <br>
     * @throws DaoException <br>
     */
    void deleteAnnouncement(@Param("ids") String[] ids) throws DaoException;

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param ids <br>
     * @param pojo <br>
     * @throws DaoException <br>
     */
    void auditAnnouncement(@Param("ids") String[] ids, @Param("pojo") AnnouncementPojo pojo) throws DaoException;

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param ids <br>
     * @param state <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = AnnouncementPojo.class)
    List<AnnouncementPojo> checkAnnouncement(@Param("ids") String[] ids, @Param("state") String state)
        throws DaoException;
}
