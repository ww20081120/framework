/**
 * 
 */
package com.fccfc.framework.web.dao;

import java.util.List;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.web.bean.area.AreaPojo;

/**
 * <Description> <br>
 * 
 * @author 伟<br>
 * @version 1.0<br>
 * @CreateDate 2015-1-25 <br>
 * @see com.fccfc.framework.web.dao <br>
 */
@Dao
public interface AreaDao {

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param area <br>
     * @param pageIndex <br>
     * @param pageSize <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = AreaPojo.class)
    List<AreaPojo> selectAreaList(@Param("area") AreaPojo area, @Param(Param.PAGE_INDEX) Integer pageIndex,
        @Param(Param.PAGE_SIZE) Integer pageSize) throws DaoException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param memberId <br>
     * @return <br>
     * @throws DaoException <br>
     */
    AreaPojo selectAreaByMemberId(@Param("memberId") Integer memberId) throws DaoException;
}
