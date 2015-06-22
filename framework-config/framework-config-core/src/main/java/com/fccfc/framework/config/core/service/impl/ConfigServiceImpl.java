/**
 * 
 */
package com.fccfc.framework.config.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.thrift.TException;

import com.fccfc.framework.config.api.Config;
import com.fccfc.framework.config.api.ConfigService;
import com.fccfc.framework.config.core.dao.ConfigItemDao;

/**
 * <Description> <br>
 * 
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2015年6月22日 <br>
 * @see com.fccfc.framework.config.core.service.impl <br>
 */
public class ConfigServiceImpl implements ConfigService.Iface {

    @Resource
    private ConfigItemDao configItemDao;

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.config.api.ConfigService.Iface#queryAllConfig(java.lang.String)
     */
    @Override
    public List<Config> queryAllConfig(String moduleCode) throws TException {
        return null;
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
