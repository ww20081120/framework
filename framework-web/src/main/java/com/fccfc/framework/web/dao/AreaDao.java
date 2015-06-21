/**
 * 
 */
package com.fccfc.framework.web.dao;

import java.util.List;

import com.fccfc.framework.api.bean.area.AreaPojo;
import com.fccfc.framework.core.db.DaoException;
import com.fccfc.framework.core.db.annotation.DAO;
import com.fccfc.framework.core.db.annotation.Param;
import com.fccfc.framework.core.db.annotation.Sql;

/**
 * <Description> <br>
 * 
 * @author 伟<br>
 * @version 1.0<br>
 * @CreateDate 2015-1-25 <br>
 * @see com.fccfc.framework.web.dao <br>
 */
@DAO
public interface AreaDao {

    @Sql(bean = AreaPojo.class)
    List<AreaPojo> selectAreaList(@Param("area") AreaPojo area, @Param(Param.pageIndex) Integer pageIndex,
        @Param(Param.pageSize) Integer pageSize) throws DaoException;
    
    AreaPojo selectAreaByMemberId(@Param("memberId") Integer memberId) throws DaoException;
}
