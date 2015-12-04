package com.fccfc.framework.config.core.service;

import java.util.List;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.config.core.bean.DictionaryDataPojo;

/**
 * <Description> <br>
 * 
 * @author liu.baiyang<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月11日 <br>
 * @since migu<br>
 * @see com.fccfc.framework.config.core.service <br>
 */
public interface DictionaryDataService {
    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    List<DictionaryDataPojo> qryAlldictData(String dictCode) throws ServiceException;

    String queryDictData(String dictCode, String value) throws ServiceException;
}
