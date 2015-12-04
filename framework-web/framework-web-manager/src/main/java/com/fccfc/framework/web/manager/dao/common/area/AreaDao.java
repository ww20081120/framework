/**
 * 
 */
package com.fccfc.framework.web.manager.dao.common.area;

import java.util.List;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.db.hibernate.IGenericBaseDao;
import com.fccfc.framework.web.manager.bean.common.AreaPojo;

/**
 * <Description> <br>
 * 
 * @author 伟<br>
 * @version 1.0<br>
 * @CreateDate 2015-1-25 <br>
 * @see com.fccfc.framework.web.dao <br>
 */
@Dao
public interface AreaDao extends IGenericBaseDao {

    /**
     * Description: 从数据库中获取AREA表的所有记录<br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @return
     * @throws DaoException <br>
     */
    @Sql(bean = AreaPojo.class,
        value = "SELECT AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME, AREA_CODE, REMARK FROM AREA")
    List<AreaPojo> selectList() throws DaoException;

}
