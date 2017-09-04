/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.workflow.file;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.workflow.core.config.FlowConfig;
import com.hbasesoft.framework.workflow.core.config.FlowLoader;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月4日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.workflow.file <br>
 */
public class JsonFileFlowLoader implements FlowLoader {

    private Map<String, FlowConfig> flowConfigHolder = new ConcurrentHashMap<String, FlowConfig>();

    public JsonFileFlowLoader() {
        init();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param flowName
     * @return <br>
     */
    @Override
    public FlowConfig load(String flowName) {
        return flowConfigHolder.get(flowName);
    }

    private void init() {
        String path = PropertyHolder.getProperty("workflow.file.dir");
        // 如果自定义了文件夹 使用文件夹内的工作流
    }

}
