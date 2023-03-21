/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/

package com.hbasesoft.framework.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.config.LocalProperty;
import com.hbasesoft.framework.common.utils.config.Property;

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

    /**
     * propertys
     */
    private static final List<Property> PROPERTYS;

    /**
     * LOCAL_PROPERTY
     */
    private static final LocalProperty LOCAL_PROPERTY = new LocalProperty();

    static {
        PROPERTYS = new ArrayList<Property>();
        ServiceLoader<Property> loader = ServiceLoader.load(Property.class);
        if (loader != null) {
            for (Property p : loader) {
                PROPERTYS.add(p);
            }
        }
        PROPERTYS.add(LOCAL_PROPERTY);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<>();
        for (Property p : PROPERTYS) {
            Map<String, String> map = p.getProperties();
            if (MapUtils.isNotEmpty(map)) {
                properties.putAll(map);
            }
        }
        return properties;
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
        String value = getProperty(name);
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
        String value = getProperty(name);
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
        String value = getProperty(name);
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
        for (Property property : PROPERTYS) {
            String value = getProperty(property, name);
            if (StringUtils.isNotEmpty(value)) {
                return value;
            }
        }
        return null;
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

    /**
     * Description: 获取本地的配置项 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static Property getLocalProperty() {
        return LOCAL_PROPERTY;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param property
     * @param name
     * @return <br>
     */
    public static String getProperty(final Property property, final String name) {
        String value = property.getProperty(name);
        if (StringUtils.isNotEmpty(value)) {
            int startIndex = value.indexOf("${");
            int end = value.indexOf("}", startIndex + 2);
            while (startIndex != -1 && end != -1) {
                String key = value.substring(startIndex + 2, end);
                if (!StringUtils.equals(name, key) && StringUtils.isNotEmpty(key)) {
                    String kv = getProperty(property, key);
                    if (kv == null && property != LOCAL_PROPERTY) {
                        kv = LOCAL_PROPERTY.getProperty(key);
                    }
                    if (kv == null) {
                        throw new UtilException(ErrorCodeDef.CONFIG_NOT_SET, key);
                    }
                    value = StringUtils.replace(value, "${" + key + "}", kv);
                }
                startIndex = value.indexOf("${");
                end = value.indexOf("}", startIndex + 2);
            }
            return value;
        }
        return value;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static String getVersion() {
        return getProperty("project.version", "1.0");
    }
}
