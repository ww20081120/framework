package com.fccfc.framework.web.manager.dao.common.area;

import java.util.List;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.web.manager.bean.common.AreaRangePojo;

/**
 * <Description> <br> 
 *  
 * @author shao.dinghui<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月19日 <br>
 * @since V7.3<br>
 * @see com.fccfc.framework.web.manager.dao.common.area <br>
 */
@Dao
public interface AreaRangeDao {

    /**
     * Description: 从数据库中获取的AREA_RANGE表所有记录<br> 
     *  
     * @author shao.dinghui<br>
     * @taskId <br>
     * @return
     * @throws DaoException <br>
     */
    @Sql(bean = AreaRangePojo.class)
    List<AreaRangePojo> getAllAreaRange() throws DaoException;

    /**
     * Description: 向数据库中写入AreaRange数据<br> 
     *  
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param pojo
     * @throws DaoException <br>
     */
    void insertAreaRange(@Param("pojo") AreaRangePojo pojo) throws DaoException;
    
    /**
     * Description: 向数据库中写入AreaRange数据<br> 
     *  
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param pojo
     * @throws DaoException <br>
     */
    void importAreaRange(@Param("pojo") AreaRangePojo pojo) throws DaoException;

    /**
     * Description: <br> 
     *  
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param areaId
     * @throws DaoException <br>
     */
    void deleteAreaRangeByAreaId(@Param("areaId") Integer areaId) throws DaoException;

    /**
     * Description: <br> 
     *  
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param areaPojo
     * @throws DaoException <br>
     */
    void modifyAreaRange(@Param("pojo") AreaRangePojo pojo) throws DaoException;

}
