/**
 * 
 */
package com.fccfc.framework.web.interceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.fccfc.framework.cache.core.CacheConstant;
import com.fccfc.framework.cache.core.CacheHelper;
import com.fccfc.framework.common.ErrorCodeDef;
import com.fccfc.framework.common.Initialization;
import com.fccfc.framework.common.InitializationException;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.web.bean.area.AreaPojo;
import com.fccfc.framework.web.service.AreaService;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年1月25日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.interceptor <br>
 */
public class AreaInitialization implements Initialization {

    @Resource
    private AreaService areaService;

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<AreaPojo> areaList = areaService.queryAllAreaPojo();
        Map<String, Object> areaMap = new HashMap<String, Object>();
        if (CommonUtil.isNotEmpty(areaList)) {
            for (AreaPojo area : areaList) {
                areaMap.put(area.getAreaId().toString(), area);
            }

            for (AreaPojo area : areaList) {
                if (area.getParentAreaId() != null) {
                    AreaPojo parentArea = (AreaPojo) areaMap.get(area.getParentAreaId().toString());
                    if (parentArea == null) {
                        throw new InitializationException(ErrorCodeDef.AREA_INITIAL_ERROR_10039);
                    }
                    area.setParent(parentArea);

                    List<AreaPojo> childrenList = parentArea.getChildren();
                    if (childrenList == null) {
                        childrenList = new ArrayList<AreaPojo>();
                        parentArea.setChildren(childrenList);
                    }
                    childrenList.add(parentArea);
                }
            }
        }
        CacheHelper.getCache().putNode(CacheConstant.AREA, areaMap);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    @Override
    public void destroy() throws Exception {
    }

}
