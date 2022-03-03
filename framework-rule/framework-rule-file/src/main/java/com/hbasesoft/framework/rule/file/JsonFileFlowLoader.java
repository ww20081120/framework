/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.rule.file;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.StreamSupport;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.hbasesoft.framework.common.InitializationException;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.UtilException;
import com.hbasesoft.framework.common.utils.io.IOUtil;
import com.hbasesoft.framework.common.utils.io.JarFileSpliterator;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.rule.core.config.FlowConfig;
import com.hbasesoft.framework.rule.core.config.FlowLoader;
import com.hbasesoft.framework.rule.core.config.JsonConfigUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月4日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.test.rule.file <br>
 */
public class JsonFileFlowLoader implements FlowLoader {

    /** default path */
    private static final String DEFAULT_PATH = "META-INF/rules";

    /** flow config hodler */
    private Map<String, String> flowConfigHolder = new ConcurrentHashMap<String, String>();

    /**
     * 
     */
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
    public FlowConfig load(final String flowName) {
        String content = flowConfigHolder.get(flowName);
        if (StringUtils.isNotEmpty(content)) {
            return JsonConfigUtil.getFlowConfig(JSONObject.parseObject(content));
        }
        return null;
    }

    private void init() {
        String path = PropertyHolder.getProperty("rules.file.dir");
        try {
            // 如果自定义了文件夹 使用文件夹内的工作流
            if (StringUtils.isNotEmpty(path)) {
                findFlowFile(new File(path));
            }
            else {
                StreamSupport.stream(new JarFileSpliterator(DEFAULT_PATH), false)
                    .filter(url -> url.getFile().toLowerCase().endsWith(".json")).forEach(url -> {
                        try (InputStream in = url.openStream();) {
                            addFlowFile(IOUtil.readString(in), url.getFile());
                        }
                        catch (Exception e) {
                            throw new UtilException(e);
                        }
                    });
            }
        }
        catch (Exception e) {
            LoggerUtil.error("load resource error.", e);
            throw new InitializationException(e);
        }
    }

    private void findFlowFile(final File dir) throws Exception {
        // 获取此包的目录 建立一个File
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(final File file) {
                return file.isDirectory() || file.getName().toLowerCase().endsWith(".json");
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

    private void addFlowFile(final String content, final String fileName) {
        if (StringUtils.isNotEmpty(content)) {
            LoggerUtil.info("find workflow file [{0}]", fileName);
            JSONObject json = JSONObject.parseObject(content);
            String name = json.getString("name");
            if (StringUtils.isEmpty(name)) {
                name = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.lastIndexOf("."));
            }
            flowConfigHolder.put(name, content);
            LoggerUtil.info("add workflow file [{0}]|[{1}] success.", name, fileName);
        }

    }

}
