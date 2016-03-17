package com.hbasesoft.framework.config.core.dao;

import java.util.List;

import com.hbasesoft.framework.config.core.bean.DictionaryDataPojo;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.annotation.Dao;
import com.hbasesoft.framework.db.core.annotation.Param;
import com.hbasesoft.framework.db.core.annotation.Sql;

/**
 * <Description> <br>
 * 
 * @author liu.baiyang<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月11日 <br>
 * @since migu<br>
 * @see com.hbasesoft.framework.config.core.dao <br>
 */
@Dao
public interface DictionaryDataDao {
    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @param dictCode
     * @taskId <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = DictionaryDataPojo.class)
    List<DictionaryDataPojo> selectAlldictData(@Param("dictCode") String dictCode) throws DaoException;

    DictionaryDataPojo selectDictData(@Param("dictCode") String dictCode, @Param("value") String value)
        throws DaoException;
}