/**
 * 
 */
package com.fccfc.framework.config.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.thrift.TException;

import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.config.api.Config;
import com.fccfc.framework.config.api.ConfigService;
import com.fccfc.framework.config.core.Configuration;
import com.fccfc.framework.config.core.dao.ConfigItemDao;
import com.fccfc.framework.db.core.DaoException;

/**
 * <Description> <br>
 * 
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2015年6月22日 <br>
 * @see com.fccfc.framework.config.core.service.impl <br>
 */
public class ConfigServiceImpl implements ConfigService.Iface {

    /**
     * configItemDao
     */
    @Resource
    private ConfigItemDao configItemDao;

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.config.api.ConfigService.Iface#queryAllConfig(java.lang.String)
     */
    @Override
    public List<Config> queryAllConfig(String moduleCode) throws TException {
        List<Config> configList = new ArrayList<Config>();
        try {
            List<Map<String, Object>> list = configItemDao.selectAll(Configuration.getModuleCode(moduleCode));
            for (Map<String, Object> map : list) {
                Config config = new Config();
                config.setConfigItemCode(CommonUtil.getString(map.get("CONFIG_ITEM_CODE")));
                config.setModuleCode(CommonUtil.getString(map.get("MODULE_CODE")));
                config.setParamCode(CommonUtil.getString(map.get("PARAM_CODE")));
                config.setParamValue(CommonUtil.getString(map.get("PARAM_VALUE")));
                configList.add(config);
            }
        }
        catch (DaoException e) {
            throw new TException(e);
        }
        return configList;
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.config.api.ConfigService.Iface#queryConfig(java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public String queryConfig(String moduleCode, String configItemCode, String paramCode) throws TException {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.config.api.ConfigService.Iface#updateConfig(com.fccfc.framework.config.api.Config)
     */
    @Override
    public void updateConfig(Config config) throws TException {
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.config.api.ConfigService.Iface#addConfig(com.fccfc.framework.config.api.Config)
     */
    @Override
    public void addConfig(Config config) throws TException {
    }

}
