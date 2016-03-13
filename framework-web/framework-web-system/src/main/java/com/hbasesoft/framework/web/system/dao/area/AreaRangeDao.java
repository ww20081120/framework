package com.hbasesoft.framework.web.system.dao.area;

import java.util.List;

import com.hbasesoft.framework.web.system.bean.AreaRangePojo;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.annotation.Dao;
import com.hbasesoft.framework.db.core.annotation.Param;
import com.hbasesoft.framework.db.core.annotation.Sql;

/**
 * <Description> <br>
 * 
 * @author shao.dinghui<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月19日 <br>
 * @since V7.3<br>
 * @see com.hbasesoft.framework.web.manager.dao.common.area <br>
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
