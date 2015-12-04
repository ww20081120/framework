package com.fccfc.framework.web.manager.service.system;

import java.util.List;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.web.manager.bean.system.DictionaryDataPojo;
import com.fccfc.framework.web.manager.bean.system.DictionaryPojo;

public interface DictDetailsService {

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng<br>
     * @taskId <br>
     * @return
     * @throws ServiceException <br>
     */
    List<DictionaryPojo> queryDictPager(int pageIndex, int pageSize) throws ServiceException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return
     * @throws ServiceException <br>
     */
    List<DictionaryPojo> queryDict() throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng<br>
     * @taskId <br>
     * @param pojo
     * @throws ServiceException <br>
     */
    void addDict(DictionaryPojo pojo) throws ServiceException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojo
     * @param oldDictCode
     * @throws ServiceException <br>
     */
    void modifyDict(DictionaryPojo pojo, String oldDictCode) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng<br>
     * @taskId <br>
     * @param dictCode
     * @throws ServiceException <br>
     */
    void deleteDict(String dictCode) throws ServiceException;


    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param dictCode
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws ServiceException <br>
     */
    List<DictionaryDataPojo> queryDictDataPager(String dictCode, int pageIndex, int pageSize) throws ServiceException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return
     * @throws ServiceException <br>
     */
    List<DictionaryDataPojo> queryDictData() throws ServiceException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param dictCode
     * @return
     * @throws ServiceException <br>
     */
    List<DictionaryDataPojo> queryDictData(String dictCode) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param dictCode
     * @return
     * @throws ServiceException <br>
     */
    boolean checkDictCode(String dictCode) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param dictCode
     * @return
     * @throws ServiceException <br>
     */
    DictionaryPojo queryDict(String dictCode) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param dictCodes
     * @throws ServiceException <br>
     */
    void deleteDicts(String[] dictCodes) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojo
     * @throws ServiceException <br>
     */
    void addDictData(DictionaryDataPojo pojo) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param dictDataId
     * @return
     * @throws ServiceException <br>
     */
    DictionaryDataPojo queryDictData(Integer dictDataId) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojo
     * @throws ServiceException <br>
     */
    void modifyDictData(DictionaryDataPojo pojo) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param dictDataIds
     * @throws ServiceException <br>
     */
    void deleteDictData(Integer[] dictDataIds) throws ServiceException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param mediaId
     * @param mediaName
     * @throws ServiceException <br>
     */
    void importDict(String mediaId, String mediaName) throws ServiceException;
    
    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param mediaId
     * @param mediaName
     * @throws ServiceException <br>
     */
    void importDictData(String mediaId, String mediaName) throws ServiceException;
}
