package com.hbasesoft.framework.config.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hbasesoft.framework.cache.core.CacheConstant;
import com.hbasesoft.framework.cache.core.annotation.Cache;
import com.hbasesoft.framework.cache.core.annotation.CacheKey;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.config.core.bean.DictionaryDataPojo;
import com.hbasesoft.framework.config.core.dao.DictionaryDataDao;
import com.hbasesoft.framework.config.core.service.DictionaryDataService;
import com.hbasesoft.framework.db.core.DaoException;

/**
 * <Description> <br>
 * 
 * @author liu.baiyang<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月11日 <br>
 * @since migu<br>
 * @see com.hbasesoft.framework.config.core.service.impl <br>
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