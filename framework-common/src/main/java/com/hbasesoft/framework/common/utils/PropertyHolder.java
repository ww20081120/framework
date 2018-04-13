/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

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
    
    private static Logger log = new Logger(PropertyHolder.class);

    private static final Map<String, String> PROPERTIES = new HashMap<>();

    private static final Map<String, String> ERROR_MESSAGE = new HashMap<>();

    static {
        init();
    }

    public static Map<String, String> getProperties() {
        return PROPERTIES;
    }

    @SuppressWarnings("unchecked")
    private static void loadYml(InputStream inputStream, Map<String, String> map) throws IOException {
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
    private static void transfer(String key, Object value, Map<String, String> map) {
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

    private static void loadProperties(InputStream inputStream, Map<String, String> map) throws IOException {
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

    public static Boolean getBooleanProperty(String name) {
        return getBooleanProperty(name, null);
    }

    public static Boolean getBooleanProperty(String name, Boolean defaultValue) {
        String value = PROPERTIES.get(name);
        return StringUtils.isNotEmpty(value) ? "true".equals(value) : defaultValue;
    }

    public static Integer getIntProperty(String name) {
        return getIntProperty(name, null);
    }

    public static Integer getIntProperty(String name, Integer defaultValue) {
        String value = PROPERTIES.get(name);
        return StringUtils.isNotEmpty(value) ? Integer.parseInt(value) : defaultValue;
    }

    public static Long getLongProperty(String name) {
        return getLongProperty(name, null);
    }

    public static Long getLongProperty(String name, Long defaultValue) {
        String value = PROPERTIES.get(name);
        return StringUtils.isNotEmpty(value) ? Long.parseLong(value) : defaultValue;
    }

    public static String getProperty(String name) {
        return PROPERTIES.get(name);
    }

    public static String getProperty(String name, String defaultValue) {
        String value = PROPERTIES.get(name);
        return StringUtils.isNotEmpty(value) ? value : defaultValue;
    }

    public static void setProperty(String name, String value) {
        PROPERTIES.put(name, value);
    }

    public static String getErrorMessage(int code, Object... params) {
        String message = ERROR_MESSAGE.get(code + GlobalConstants.BLANK);
        if (StringUtils.isNotEmpty(message)) {
            if (CommonUtil.isNotEmpty(params)) {
                return CommonUtil.messageFormat(message, params);
            }
            return message;
        }
        return GlobalConstants.BLANK + code;
    }

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