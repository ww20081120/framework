package com.hbasesoft.framework.web.system.dao.log;

import java.util.List;

import com.hbasesoft.framework.web.system.bean.EventPojo;
import com.hbasesoft.framework.web.system.bean.OperateLogPojo;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.annotation.Dao;
import com.hbasesoft.framework.db.core.annotation.Param;
import com.hbasesoft.framework.db.core.annotation.Sql;
import com.hbasesoft.framework.db.hibernate.IGenericBaseDao;

/***
 * <Description> <br>
 * 
 * @author bai.wenlong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月13日 <br>
 * @since V6.11<br>
 * @see com.hbasesoft.framework.web.manager.dao.system.log <br>
 */
@Dao
public interface OperateLogDao extends IGenericBaseDao {
    /**
     * Description: <br>
     * 
     * @author xu.jun<br>
     * @taskId <br>
     * @param operatorid 操作员id
     * @param startdate 开始日期
     * @param enddate 结束日期
     * @param eventid 事件id
     * @param pageIndex 当前页数
     * @param pageSize 每页记录数
     * @return List<OperateLogPojo>
     * @throws DaoException <br>
     */
    @Sql(bean = OperateLogPojo.class)
    List<OperateLogPojo> selectServiceRecord(@Param("operatorid") Integer operatorid,
        @Param("startdate") String startdate, @Param("enddate") String enddate, @Param("eventid") Integer eventid,
        @Param(Param.PAGE_INDEX) Integer pageIndex, @Param(Param.PAGE_SIZE) Integer pageSize) throws DaoException;

    /**
     * Description: <br>
     * 
     * @author xu.jun<br>
     * @taskId <br>
     * @param operatorid 操作员id
     * @param startdate 开始日期
     * @param enddate 结束日期
     * @param eventid 事件id
     * @return List<OperateLogPojo>
     * @throws DaoException <br>
     */
    @Sql(bean = OperateLogPojo.class)
    List<OperateLogPojo> selectServiceRecord(@Param("operatorid") Integer operatorid,
        @Param("startdate") String startdate, @Param("enddate") String enddate, @Param("eventid") Integer eventid)
            throws DaoException;

    /**
     * Description: <br>
     * 
     * @author xu.jun<br>
     * @taskId <br>
     * @return List
     * @throws DaoException <br>
     */
    @Sql(bean = EventPojo.class)
    List<EventPojo> qryEventList() throws DaoException;

    /**
     * 查询所有
     */
    @Sql(bean = OperateLogPojo.class)
    List<OperateLogPojo> getAll(@Param(Param.PAGE_INDEX) Integer pageIndex, @Param(Param.PAGE_SIZE) Integer pageSize)
        throws DaoException;
}
