package com.fccfc.framework.config.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fccfc.framework.cache.core.CacheConstant;
import com.fccfc.framework.cache.core.CacheHelper;
import com.fccfc.framework.common.ErrorCodeDef;
import com.fccfc.framework.common.GlobalConstants;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.common.utils.logger.Logger;
import com.fccfc.framework.config.core.bean.DictionaryDataPojo;
import com.fccfc.framework.config.core.service.DictionaryDataService;
import org.apache.commons.lang.StringUtils;

/**
 * <Description> <br>
 *
 * @author liu.baiyang<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月11日 <br>
 * @see com.fccfc.framework.config.core <br>
 * @since migu<br>
 */
public final class DictionaryHelper {
    /**
     * configurationService
     */
    private static DictionaryDataService dictionaryDataService;

    /**
     * logger
     */
    private static Logger logger = new Logger(DictionaryHelper.class);

    /**
     * Description: <br>
     *
     * @throws ServiceException <br>
     * @author liu.baiyang<br>
     * @taskId <br>
     */
    public static void reloadCache() throws ServiceException {
        try {
            List<DictionaryDataPojo> dictionaryDatas = dictionaryDataService.qryAlldictData();
            if (CommonUtil.isNotEmpty(dictionaryDatas)) {
                Map<String, String> cacheMap = new HashMap<String, String>();
                for (DictionaryDataPojo dictData : dictionaryDatas) {
                    cacheMap.put(dictData.getDictCode() + GlobalConstants.SPLITOR + dictData.getDictDataValue(),
                            dictData.getDictDataName());
                }
                CacheHelper.getStringCache().putNode(CacheConstant.DICTIONARY_DATA, cacheMap);
            }
        } catch (Exception e) {
            throw new ServiceException(ErrorCodeDef.CACHE_ERROR_10002, e);
        }
    }

    /**
     * Description: 根据key获取字典数据：字符串类型<br>
     *
     * @param dictCode <br>
     * @param data     <br>
     * @return <br>
     * @author liu.baiyang<br>
     * @taskId <br>
     */
    public static String getString(String dictCode, String data) {
        String value = null;
        String key = dictCode + GlobalConstants.SPLITOR + data;
        try {
            value = CacheHelper.getStringCache().getValue(CacheConstant.DICTIONARY_DATA, key);
        } catch (Exception e) {
            logger.warn(e, "get cache error. key is [{0}]", key);
        }
        return CommonUtil.isEmpty(value) ? data : value;
    }

    /**
     * 从缓存中获取字典数据集合
     * @param dictCode - 字典代码
     * @return 返回字典数据集合
     */
    public static Map<String, String> getMap(String dictCode) {
        Map<String, String> dictDataMap = new HashMap<String, String>();
        try {
            String key = dictCode + GlobalConstants.SPLITOR;
            Map<String, String> cache = CacheHelper.getStringCache().getNode(CacheConstant.DICTIONARY_DATA);
            for (Map.Entry<String, String> entry : cache.entrySet()) {
                String entryKey = entry.getKey();
                if(entryKey.contains(key)){
                    dictDataMap.put(StringUtils.split(entryKey, GlobalConstants.SPLITOR)[1], entry.getValue());
                }
            }
        } catch (Exception e) {
            logger.warn(e, "get cache error. key is [{0}]", dictCode);
        }
        return dictDataMap;
    }

    /**
     * Description: <br>
     *
     * @param dictionaryDataService <br>
     * @author liu.baiyang<br>
     * @taskId <br>
     */
    public static void setdictDataService(DictionaryDataService dictionaryDataService) {
        DictionaryHelper.dictionaryDataService = dictionaryDataService;
    }
}