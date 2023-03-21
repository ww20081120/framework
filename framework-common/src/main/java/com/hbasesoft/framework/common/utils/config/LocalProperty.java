/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.InitializationException;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2023年2月15日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.utils.config <br>
 */
public class LocalProperty implements Property {

    /** properties */
    private final Map<String, String> props = new HashMap<>();

    /**
     * chckstyle
     */
    public LocalProperty() {
        init();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param property property
     * @return <br>
     */
    @Override
    public String getProperty(final String property) {
        return props.get(property);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public Map<String, String> getProperties() {
        return props;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param inputStream
     * @param map
     * @throws IOException <br>
     */
    @SuppressWarnings("unchecked")
    private void loadYml(final InputStream inputStream, final Map<String, String> map) throws IOException {
        try {

            Yaml yaml = new Yaml();
            HashMap<String, Object> value = yaml.loadAs(inputStream, HashMap.class);
            if (MapUtils.isNotEmpty(value)) {
                for (Entry<String, Object> entry : value.entrySet()) {
                    transfer(entry.getKey(), entry.getValue(), map);
                }
            }
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @SuppressWarnings("unchecked")
    private void transfer(final String key, final Object value, final Map<String, String> map) {
        if (value != null) {
            if (value instanceof Map) {
                Map<String, Object> m = (Map<String, Object>) value;
                if (MapUtils.isNotEmpty(m)) {
                    for (Entry<String, Object> entry : m.entrySet()) {
                        transfer(key + GlobalConstants.PERIOD + entry.getKey(), entry.getValue(), map);
                    }
                }
            }
            else {
                map.put(key, value.toString());
            }
        }
    }

    private void loadProperties(final InputStream inputStream, final Map<String, String> map) throws IOException {
        try {
            Properties properties = new Properties();
            properties.load(new InputStreamReader(inputStream, "utf-8"));
            for (Entry<Object, Object> entry : properties.entrySet()) {
                map.put(entry.getKey() + GlobalConstants.BLANK, entry.getValue() + GlobalConstants.BLANK);
            }
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * Description: 本方法中的日志只能输出中文，因为APDPlatLoggerImpl中默认指定输出中文 只有配置项加载完毕，调用了指定日志输出语言方法LOG.setLocale(getLogLanguage())
     * 之后，配置的日志输出语言才会生效<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    private void init() {
        String systemConfig = "/application.yml";
        ClassPathResource cr = null;
        try {
            cr = new ClassPathResource(systemConfig);
            if (cr.exists()) {
                loadYml(cr.getInputStream(), props);
            }
            else {
                systemConfig = "/application.properties";
                cr = new ClassPathResource(systemConfig);
                loadProperties(cr.getInputStream(), props);
            }
            System.out.println("装入主配置文件:" + systemConfig);
        }
        catch (Exception e) {
            System.out.println("装入主配置文件" + systemConfig + "失败!");
            e.printStackTrace();
            // throw new InitializationException(e);
            return;
        }

        // 加载扩展文件
        loadExtendFiles();

        System.out.println("系统配置属性装载完毕");
        System.out.println("******************属性列表***************************");
        props.keySet().forEach(propertyName -> {
            System.out.println("  " + propertyName + " = " + props.get(propertyName));
        });
        System.out.println("***********************************************************");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    private void loadExtendFiles() {
        String extendPropertyFiles = props.get("extend.property.files");
        String springIncloud = props.get("spring.profiles.include");
        if (StringUtils.isNotEmpty(springIncloud)) {
            StringBuilder sb = new StringBuilder();
            if (sb != null) {
                sb.append(extendPropertyFiles);
            }

            String[] fs = StringUtils.split(springIncloud, GlobalConstants.SPLITOR);
            for (String f : fs) {
                sb.append(GlobalConstants.SPLITOR).append("application-").append(f).append(".yml");
            }
            extendPropertyFiles = sb.toString();
        }

        if (StringUtils.isNotEmpty(extendPropertyFiles)) {
            String[] files = StringUtils.split(extendPropertyFiles, GlobalConstants.SPLITOR);
            ClassPathResource cr = null;
            for (String file : files) {
                try {
                    file = StringUtils.trim(file);
                    cr = new ClassPathResource(file);
                    if (cr.exists()) {
                        if (StringUtils.endsWith(file, "yml")) {
                            loadYml(cr.getInputStream(), props);
                        }
                        else {
                            loadProperties(cr.getInputStream(), props);
                        }
                        System.out.println("装入扩展配置文件：" + file);
                    }
                }
                catch (Exception e) {
                    System.out.println("装入扩展配置文件" + file + "失败！");
                    e.printStackTrace();
                    throw new InitializationException(e);
                }
            }
        }
    }
}
