package com.fccfc.framework.config.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fccfc.framework.cache.core.CacheConstant;
import com.fccfc.framework.cache.core.annotation.Cache;
import com.fccfc.framework.cache.core.annotation.CacheKey;
import com.fccfc.framework.common.GlobalConstants;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.config.core.bean.DictionaryDataPojo;
import com.fccfc.framework.config.core.dao.DictionaryDataDao;
import com.fccfc.framework.config.core.service.DictionaryDataService;
import com.fccfc.framework.db.core.DaoException;

/**
 * <Description> <br>
 * 
 * @author liu.baiyang<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月11日 <br>
 * @since migu<br>
 * @see com.fccfc.framework.config.core.service.impl <br>
 */
@Service
public class DictionaryDataServiceImpl implements DictionaryDataService {
    /**
     * dictionaryDataDao
     */
    @Resource
    private DictionaryDataDao dictionaryDataDao;

    @Override
    @Cache(node = CacheConstant.DICTIONARY_DATA)
    public List<DictionaryDataPojo> qryAlldictData(@CacheKey String dictCode) throws ServiceException {
        try {
            return dictionaryDataDao.selectAlldictData(dictCode);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param dictCode
     * @param value
     * @return
     * @throws ServiceException <br>
     */
    @Override
    @Cache(node = CacheConstant.DICTIONARY_DATA)
    public String queryDictData(@CacheKey String dictCode, @CacheKey String value) throws ServiceException {
        try {
            DictionaryDataPojo pojo = dictionaryDataDao.selectDictData(dictCode, value);
            return pojo != null ? pojo.getDictDataName() : GlobalConstants.BLANK;
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }
}