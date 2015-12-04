package com.fccfc.framework.web.manager.dao.system.dict;

import java.util.List;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.db.hibernate.IGenericBaseDao;
import com.fccfc.framework.web.manager.bean.system.DictionaryDataPojo;
import com.fccfc.framework.web.manager.bean.system.DictionaryPojo;

/**
 * <Description> <br> 
 *  
 * @author yang.zhipeng<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月28日 <br>
 * @since V7.3<br>
 * @see com.fccfc.framework.web.manager.dao.system.dict <br>
 */
@Dao
public interface DictDao extends IGenericBaseDao{

    /**
     * Description: <br> 
     *  
     * @author yang.zhipeng<br>
     * @taskId <br>
     * @return
     * @throws DaoException <br>
     */
    @Sql(bean = DictionaryPojo.class)
    List<DictionaryPojo> queryDict(@Param(Param.PAGE_INDEX) Integer pageIndex, @Param(Param.PAGE_SIZE) Integer pageSize) throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return
     * @throws DaoException <br>
     */
    @Sql(bean = DictionaryPojo.class)
    List<DictionaryPojo> queryDict() throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param dictCode
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws DaoException <br>
     */
    @Sql(bean = DictionaryDataPojo.class)
    List<DictionaryDataPojo> queryDictData(@Param("dictCode") String dictCode, @Param(Param.PAGE_INDEX) Integer pageIndex, @Param(Param.PAGE_SIZE) Integer pageSize) throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return
     * @throws DaoException <br>
     */
    @Sql(bean = DictionaryDataPojo.class)
    List<DictionaryDataPojo> queryDictData() throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param dictCode
     * @return
     * @throws DaoException <br>
     */
    @Sql(bean = DictionaryDataPojo.class)
    List<DictionaryDataPojo> queryDictData(@Param("dictCode") String dictCode) throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param dictCodes
     * @throws DaoException <br>
     */
    void deleteDicts(@Param("dictCodes") String[] dictCodes) throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param dictDataIds
     * @throws DaoException <br>
     */
    void deleteDictDatas(@Param("dictDataIds") Integer[] dictDataIds) throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojoList
     * @return
     * @throws DaoException <br>
     */
    int batchInsertDict(@Param("pojoList") List<DictionaryPojo> pojoList) throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojoList
     * @return
     * @throws DaoException <br>
     */
    int batchInsertDictData(@Param("pojoList") List<DictionaryDataPojo> pojoList) throws DaoException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojo
     * @param oldDictCode
     * @throws DaoException <br>
     */
    void updateDict(@Param("pojo") DictionaryPojo pojo, @Param("oldDictCode") String oldDictCode) throws DaoException;
}
