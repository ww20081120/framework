package com.fccfc.framework.config.core.dao;

import java.util.List;

import com.fccfc.framework.config.core.bean.DictionaryDataPojo;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Sql;
/**
 * <Description> <br>
 * 
 * @author liu.baiyang<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月11日 <br>
 * @since migu<br>
 * @see com.fccfc.framework.config.core.dao <br>
 */
@Dao
public interface DictionaryDataDao {
    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = DictionaryDataPojo.class)
    List<DictionaryDataPojo> selectAlldictData() throws DaoException;
}