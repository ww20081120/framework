/**
 * 
 */
package com.fccfc.framework.config.core.dao;

import java.util.List;
import java.util.Map;

import com.fccfc.framework.config.core.bean.ConfigItemPojo;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.db.hibernate.IGenericBaseDao;

/**
 * <Description> <br>
 * 
 * @author Administrator<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月9日 <br>
 * @since V7.3<br>
 * @see com.fccfc.framework.core.db.config <br>
 */
@Dao
public interface ConfigItemDao extends IGenericBaseDao {

    @Sql(bean = ConfigItemPojo.class)
    List<ConfigItemPojo> selectConfigItemList(@Param("configItem") ConfigItemPojo configItemPojo,
        @Param(Param.pageIndex) int pageIndex, @Param(Param.pageSize) int pageSize) throws DaoException;

    @Sql(bean = Map.class)
    List<Map<String, Object>> selectAll(@Param("moduleList") List<String> moduleList) throws DaoException;
}
