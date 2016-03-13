/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.web.core.init;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.StartupListener;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.io.IOUtil;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.web.core.bean.Module;
import com.hbasesoft.framework.web.core.service.ResourceLoaderService;
import com.hbasesoft.framework.web.core.service.impl.ResourceLoaderServiceImpl;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年1月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.web.core.init <br>
 */
public class ModuleLoader implements StartupListener {

    private static Logger logger = new Logger(ModuleLoader.class);

    private ResourceLoaderService resourceLoaderService = new ResourceLoaderServiceImpl();

    private List<Module> moduleList;

    private boolean stop;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public LoadOrder getOrder() {
        return LoadOrder.LAST;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param context
     * @throws FrameworkException <br>
     */
    @Override
    public void init() throws FrameworkException {
        moduleList = new ArrayList<Module>();
        try {
            Enumeration<URL> resources = ModuleLoader.class.getClassLoader().getResources("META-INF/module.json");
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                Module module = getModuleInfoFromJson(url);
                if (module != null) {
                    moduleList.add(module);
                }
            }

            loadStatic();

//            if (GlobalConstants.DEV_MODEL.equals(PropertyHolder.getProperty("project.develop.model"))) {
//                final WatchService watcher = FileSystems.getDefault().newWatchService();
//
//                Paths.get(StartupServlet.getContext().getRealPath("/")).register(watcher,
//                    StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
//                    StandardWatchEventKinds.ENTRY_MODIFY);
//
//                new Thread() {
//                    @SuppressWarnings("rawtypes")
//                    public void run() {
//                        try {
//                            while (!stop) {
//                                WatchKey key = watcher.take();
//                                for (WatchEvent event : key.pollEvents()) {
//                                    WatchEvent.Kind kind = event.kind();
//                                    if (kind == StandardWatchEventKinds.OVERFLOW) {// 事件可能lost or discarded
//                                        continue;
//                                    }
//                                    loadStatic();
//                                }
//
//                                if (!key.reset()) {
//                                    break;
//                                }
//
//                            }
//                        }
//                        catch (InterruptedException e) {
//                            logger.error(e.getMessage(), e);
//                        }
//
//                    };
//                }.start();
//            }
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void loadStatic() {
        try {
            for (Module module : moduleList) {
                logger.info("===============>开始加载[{0}]模块静态资源", module.getModuleName());
                long begin = System.currentTimeMillis();
                resourceLoaderService.loadStaticResource(module);
                logger.info("===============>加载[{0}]模块成功,耗时[{1}]毫秒", module.getModuleName(),
                    System.currentTimeMillis() - begin);
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private Module getModuleInfoFromJson(URL url) throws ServiceException {
        try {
            String jsonStr = IOUtil.readString(url.openStream());
            if (CommonUtil.isNotEmpty(jsonStr)) {
                JSONObject json = JSONObject.parseObject(jsonStr);
                Module module = new Module();
                String moduleName = json.getString("name");
                Assert.notEmpty(moduleName, "模块名称不能为空");
                module.setModuleName(moduleName);

                String moduleCode = json.getString("code");
                Assert.notEmpty(moduleCode, "模块code不能为空");
                module.setModuleCode(moduleCode);

                JSONArray controllers = json.getJSONArray("controllers");
                if (CommonUtil.isNotEmpty(controllers)) {
                    List<String> controllerList = new ArrayList<String>();
                    for (Object obj : controllers) {
                        if (obj instanceof String) {
                            controllerList.add((String) obj);
                        }
                    }
                    module.setControllerPath(controllerList);
                }

                JSONArray resources = json.getJSONArray("resources");
                if (CommonUtil.isNotEmpty(resources)) {
                    List<String> resourceList = new ArrayList<String>();
                    for (Object obj : resources) {
                        if (obj instanceof String) {
                            resourceList.add((String) obj);
                        }
                    }
                    module.setResources(resourceList);
                }
                return module;
            }
        }
        catch (Exception e) {
            throw new ServiceException(ErrorCodeDef.MODULE_LOADER_ERROR, "读取模块配置文件[{0}]失败", url, e);
        }
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Override
    public void destory() {
        stop = true;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param context
     * @throws FrameworkException <br>
     */
    @Override
    public void complete(ApplicationContext context) throws FrameworkException {
        try {
            for (Module module : moduleList) {
                logger.info("===============>开始加载[{0}]模块URL资源", module.getModuleName());
                long begin = System.currentTimeMillis();
                resourceLoaderService.loadUrlResouce(module);
                logger.info("===============>加载[{0}]模块URL资源成功,耗时[{1}]毫秒", module.getModuleName(),
                    System.currentTimeMillis() - begin);
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
