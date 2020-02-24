/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/

package com.hbasesoft.framework.common.utils;

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
import com.hbasesoft.framework.common.utils.logger.Logger;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <Description> 系统配置<br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年8月11日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.utils <br>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PropertyHolder {

    /** log */
    private static Logger log = new Logger(PropertyHolder.class);

    /** properties */
    private static final Map<String, String> PROPERTIES = new HashMap<>();

    /** error message */
    private static final Map<String, String> ERROR_MESSAGE = new HashMap<>();

    static {
        init();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static Map<String, String> getProperties() {
        return PROPERTIES;
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
    private static void loadYml(final InputStream inputStream, final Map<String, String> map) throws IOException {
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
    private static void transfer(final String key, final Object value, final Map<String, String> map) {
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

    private static void loadProperties(final InputStream inputStream, final Map<String, String> map)
        throws IOException {
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
    private static void init() {
        String systemConfig = "/application.yml";
        ClassPathResource cr = null;
        try {
            cr = new ClassPathResource(systemConfig);
            if (cr.exists()) {
                loadYml(cr.getInputStream(), PROPERTIES);
            }
            else {
                systemConfig = "/application.properties";
                cr = new ClassPathResource(systemConfig);
                loadProperties(cr.getInputStream(), PROPERTIES);
            }
            log.info("装入主配置文件:" + systemConfig);
        }
        catch (Exception e) {
            log.error("装入主配置文件" + systemConfig + "失败!", e);
            throw new InitializationException(e);
        }

        String extendPropertyFiles = PROPERTIES.get("extend.property.files");
        if (StringUtils.isNotEmpty(extendPropertyFiles)) {
            String[] files = StringUtils.split(extendPropertyFiles, GlobalConstants.SPLITOR);
            for (String file : files) {
                try {
                    file = StringUtils.trim(file);
                    cr = new ClassPathResource(file);
                    if (cr.exists()) {
                        if (StringUtils.endsWith(file, "yml")) {
                            loadYml(cr.getInputStream(), PROPERTIES);
                        }
                        else {
                            loadProperties(cr.getInputStream(), PROPERTIES);
                        }
                        log.info("装入扩展配置文件：" + file);
                    }
                }
                catch (Exception e) {
                    log.info("装入扩展配置文件" + file + "失败！", e);
                    throw new InitializationException(e);
                }
            }
        }
        log.info("系统配置属性装载完毕");
        log.info("******************属性列表***************************");
        PROPERTIES.keySet().forEach(propertyName -> {
            log.info("  " + propertyName + " = " + PROPERTIES.get(propertyName));
        });
        log.info("***********************************************************");

        loadErrorMessage();
    }

    private static void loadErrorMessage() {
        String systemErrorMessagePath = "/errorMessage.properties";
        ClassPathResource cr = null;
        try {
            cr = new ClassPathResource(systemErrorMessagePath);
            loadProperties(cr.getInputStream(), ERROR_MESSAGE);
            log.info("装入系统错误码文件:" + systemErrorMessagePath);
        }
        catch (Exception e) {
            log.info("装入系统错误码文件" + systemErrorMessagePath + "失败!", e);
        }

        String projectErrorMessagePath = getProjectName() + "_errorMessage.properties";
        try {
            cr = new ClassPathResource(projectErrorMessagePath);
            if (cr.exists()) {
                loadProperties(cr.getInputStream(), ERROR_MESSAGE);
                log.info("装入项目错误码文件:" + projectErrorMessagePath);
            }
        }
        catch (Exception e) {
            log.info("装入系统错误码文件" + projectErrorMessagePath + "失败!", e);
        }

        String selfPaths = getProperty("extend.errorMessage.files");
        if (StringUtils.isNotEmpty(selfPaths)) {
            String[] files = StringUtils.split(selfPaths, GlobalConstants.SPLITOR);
            for (String file : files) {
                try {
                    cr = new ClassPathResource(file);
                    loadProperties(cr.getInputStream(), ERROR_MESSAGE);
                    log.info("装入错误码文件：" + file);
                }
                catch (Exception e) {
                    log.info("装入错误码文件" + file + "失败！", e);
                }
            }
        }

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param name
     * @return <br>
     */
    public static Boolean getBooleanProperty(final String name) {
        return getBooleanProperty(name, null);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param name
     * @param defaultValue
     * @return <br>
     */
    public static Boolean getBooleanProperty(final String name, final Boolean defaultValue) {
        String value = PROPERTIES.get(name);
        return StringUtils.isNotEmpty(value) ? "true".equals(value) : defaultValue;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param name
     * @return <br>
     */
    public static Integer getIntProperty(final String name) {
        return getIntProperty(name, null);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param name
     * @param defaultValue
     * @return <br>
     */
    public static Integer getIntProperty(final String name, final Integer defaultValue) {
        String value = PROPERTIES.get(name);
        return StringUtils.isNotEmpty(value) ? Integer.parseInt(value) : defaultValue;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param name
     * @return <br>
     */
    public static Long getLongProperty(final String name) {
        return getLongProperty(name, null);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param name
     * @param defaultValue
     * @return <br>
     */
    public static Long getLongProperty(final String name, final Long defaultValue) {
        String value = PROPERTIES.get(name);
        return StringUtils.isNotEmpty(value) ? Long.parseLong(value) : defaultValue;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param name
     * @return <br>
     */
    public static String getProperty(final String name) {
        String value = PROPERTIES.get(name);
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(value)) {
            int startIndex = value.indexOf("${");
            if (startIndex != -1) {
                String key = value.substring(startIndex + 2, value.indexOf("}", startIndex + 2));
                if (!org.apache.commons.lang3.StringUtils.equals(name, key)
                    && org.apache.commons.lang3.StringUtils.isNotEmpty(key)) {
                    String kv = getProperty(key);
                    value = StringUtils.replace(value, "${" + key + "}", kv);
                }
            }

        }
        return value;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param name
     * @param defaultValue
     * @return <br>
     */
    public static String getProperty(final String name, final String defaultValue) {
        String value = getProperty(name);
        return StringUtils.isNotEmpty(value) ? value : defaultValue;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param name
     * @param value <br>
     */
    public static void setProperty(final String name, final String value) {
        PROPERTIES.put(name, value);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param code
     * @param params
     * @return <br>
     */
    public static String getErrorMessage(final int code, final Object... params) {
        String message = ERROR_MESSAGE.get(code + GlobalConstants.BLANK);
        if (StringUtils.isNotEmpty(message)) {
            if (CommonUtil.isNotEmpty(params)) {
                return CommonUtil.messageFormat(message, params);
            }
            return message;
        }
        return GlobalConstants.BLANK + code;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static String getProjectName() {
        String name = getProperty("project.name");
        if (StringUtils.isEmpty(name)) {
            String realPath = StringUtils.replaceEach(
                PropertyHolder.class.getClassLoader().getResource(GlobalConstants.BLANK).getPath(), new String[] {
                    "/target/classes/", "/WEB-INF/classes/"
                }, new String[] {
                    GlobalConstants.BLANK, GlobalConstants.BLANK
                });

            name = realPath.substring(realPath.lastIndexOf(GlobalConstants.PATH_SPLITOR));
        }
        return name;
    }
}
