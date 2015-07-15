package com.fccfc.framework.config.core.service.impl;

import java.util.List;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

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
    public List<DictionaryDataPojo> qryAlldictData() throws ServiceException {
        try {
            return dictionaryDataDao.selectAlldictData();
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }
}