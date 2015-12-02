package com.fccfc.framework.config.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.fccfc.framework.cache.core.CacheConstant;
import com.fccfc.framework.cache.core.CacheHelper;
import com.fccfc.framework.common.ErrorCodeDef;
import com.fccfc.framework.common.GlobalConstants;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.common.utils.logger.Logger;
import com.fccfc.framework.config.core.bean.DictionaryDataPojo;
import com.fccfc.framework.config.core.service.DictionaryDataService;

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
     * logger
     */
    private static Logger logger = new Logger(DictionaryHelper.class);

    /**
     * configurationService
     */
    private static DictionaryDataService dictionaryDataService;

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
                CacheHelper.getCache().putNode(CacheConstant.DICTIONARY_DATA, cacheMap);
            }
        }
        catch (Exception e) {
            throw new ServiceException(ErrorCodeDef.CACHE_ERROR_10002, e);
        }
    }

    /**
     * Description: 根据key获取字典数据：字符串类型<br>
     *
     * @param dictCode <br>
     * @param data <br>
     * @return <br>
     * @author liu.baiyang<br>
     * @taskId <br>
     */
    public static String getString(String dictCode, String data) {
        String value = null;
        String key = dictCode + GlobalConstants.SPLITOR + data;
        try {
            value = CacheHelper.getCache().getValue(CacheConstant.DICTIONARY_DATA, key);
        }
        catch (Exception e) {
            logger.warn(e, "get cache error. key is [{0}]", key);
        }
        return CommonUtil.isEmpty(value) ? data : value;
    }

    /**
     * 从缓存中获取字典数据集合
     * 
     * @param dictCode - 字典代码
     * @return 返回字典数据集合
     */
    public static List<Map<String, String>> getMap(String dictCode) {
        List<Map<String, String>> dictDataList = new ArrayList<Map<String, String>>();
        try {
            String key = dictCode + GlobalConstants.SPLITOR;
            Map<String, String> cache = CacheHelper.getCache().getNode(CacheConstant.DICTIONARY_DATA);

            Map<String, String> dictDataMap = null;
            for (Map.Entry<String, String> entry : cache.entrySet()) {
                String entryKey = entry.getKey();
                if (entryKey.contains(key)) {
                    dictDataMap = new HashMap<String, String>();
                    dictDataMap.put("key", StringUtils.split(entryKey, GlobalConstants.SPLITOR)[1]);
                    dictDataMap.put("value", entry.getValue());
                    dictDataList.add(dictDataMap);
                }
            }
        }
        catch (Exception e) {
            logger.warn(e, "get cache error. key is [{0}]", dictCode);
        }
        return dictDataList;
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