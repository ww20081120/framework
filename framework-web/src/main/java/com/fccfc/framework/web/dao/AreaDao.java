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

    @Sql(bean = AreaPojo.class)
    List<AreaPojo> selectAreaList(@Param("area") AreaPojo area, @Param(Param.pageIndex) Integer pageIndex,
        @Param(Param.pageSize) Integer pageSize) throws DaoException;

    AreaPojo selectAreaByMemberId(@Param("memberId") Integer memberId) throws DaoException;
}
