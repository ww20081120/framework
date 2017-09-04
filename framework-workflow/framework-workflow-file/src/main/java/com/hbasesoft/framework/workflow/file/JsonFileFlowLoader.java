/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.workflow.file;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.InitializationException;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.io.IOUtil;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.workflow.core.FlowComponent;
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

    private static final String DEFAULT_PATH = "META-INF/workflow";

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
        try {
            // 如果自定义了文件夹 使用文件夹内的工作流
            if (CommonUtil.isNotEmpty(path)) {
                findFlowFile(new File(path));
            }
            else {
                Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(DEFAULT_PATH);

                // 循环迭代下去
                while (dirs.hasMoreElements()) {
                    // 获取下一个元素
                    URL url = dirs.nextElement();
                    // 得到协议的名称
                    String protocol = url.getProtocol();
                    // 如果是以文件的形式保存在服务器上
                    if ("file".equals(protocol)) {
                        LoggerUtil.info("-------------- scan workfow type file ----------------");
                        // 获取包的物理路径
                        String filePath = URLDecoder.decode(url.getFile(), "UTF-8");

                        // 以文件的方式扫描整个包下的文件 并添加到集合中
                        findFlowFile(new File(filePath));
                    }
                    else if ("jar".equals(protocol)) {
                        LoggerUtil.info("-------------- scan workfow type jar ----------------");
                        fileFlowFileInJar(DEFAULT_PATH, url);
                    }
                }

            }
        }
        catch (Exception e) {
            LoggerUtil.error("load resource error.", e);
            throw new InitializationException(e);
        }

    }

    private void findFlowFile(File dir) throws Exception {
        // 获取此包的目录 建立一个File
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return file.getName().toLowerCase().endsWith(".json");
            }
        });

        for (File file : dirfiles) {
            if (file.isDirectory()) {
                findFlowFile(file);
            }
            else {
                addFlowFile(IOUtil.readString(new FileInputStream(file)), file.getAbsolutePath());
            }
        }
    }

    private void fileFlowFileInJar(String dir, URL url) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        // 获取jar
        JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
        // 从此jar包 得到一个枚举类
        Enumeration<JarEntry> entries = jar.entries();

        // 同样的进行循环迭代
        while (entries.hasMoreElements()) {
            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            // 如果前半部分和定义的包名相同
            if (!entry.isDirectory() && name.startsWith(dir) && name.toLowerCase().endsWith(".json")) {
                addFlowFile(IOUtil.readString(classLoader.getResourceAsStream(name)), name);
            }
        }
    }

    private void addFlowFile(String content, String fileName) {
        if (CommonUtil.isNotEmpty(content)) {
            LoggerUtil.info("find workflow file [{0}]", fileName);
            JSONObject json = JSONObject.parseObject(content);

            FlowConfig config = getFlowConfig(json);
            String name = config.getName();
            if (CommonUtil.isEmpty(name)) {
                name = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.lastIndexOf("."));
            }
            flowConfigHolder.put(name, config);
            LoggerUtil.info("add workflow file [{0}]|[{1}] success.", name, fileName);
        }

    }

    private FlowConfig getFlowConfig(JSONObject obj) {

        FlowConfig config = new FlowConfig();

        String component = obj.getString("component");
        if (CommonUtil.isNotEmpty(component)) {
            FlowComponent flowComponent = ContextHolder.getContext().getBean(component, FlowComponent.class);
            Assert.notNull(flowComponent, ErrorCodeDef.FLOW_COMPONENT_NOT_FOUND, component);
            config.setComponent(flowComponent);
        }

        String name = obj.getString("name");
        if (CommonUtil.isEmpty(name)) {
            name = component;
        }
        config.setName(name);

        String version = obj.getString("version");
        if (CommonUtil.isEmpty(version)) {
            version = "1.0";
        }

        JSONArray children = obj.getJSONArray("children");

        if (CommonUtil.isNotEmpty(children)) {
            List<FlowConfig> childConfigList = new ArrayList<FlowConfig>();
            for (int i = 0, size = children.size(); i < size; i++) {
                childConfigList.add(getFlowConfig(children.getJSONObject(i)));
            }
            config.setChildrenConfigList(childConfigList);
        }
        else if (CommonUtil.isEmpty(component)) {
            throw new InitializationException(ErrorCodeDef.FLOW_COMPONENT_INSTANCE_OR_CHILDREN_NOT_FOUND, name);
        }

        obj.remove("component");
        obj.remove("name");
        obj.remove("version");
        obj.remove("children");
        config.setConfigAttrMap(obj);
        return config;
    }

}
