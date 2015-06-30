/**
 * 
 */
package com.fccfc.framework.bootstrap.utils.cmd;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import com.fccfc.framework.common.utils.CommonUtil;

/**
 * <Description> <br>
 * 
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2015年6月27日 <br>
 * @see com.fccfc.framework.bootstrap.utils.cmd <br>
 */
public final class OptionParser {

    /**
     * OptionParser
     */
    private OptionParser() {
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param target <br>
     * @param args <br>
     */
    public static void parse(Object target, String[] args) {
        Map<String, String> paramsMap = parse(target.getClass(), args);
        if (CommonUtil.isNotEmpty(paramsMap)) {

            for (Entry<String, String> entry : paramsMap.entrySet()) {
                try {
                    BeanUtils.setProperty(target, entry.getKey(), entry.getValue());
                }
                catch (Exception e) {
                    throw new IllegalArgumentException("设置" + entry.getKey() + "参数失败");
                }
            }

        }
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param clazz <br>
     * @param args <br>
     * @return <br>
     */
    public static Map<String, String> parse(Class<?> clazz, String[] args) {
        Map<String, Option> optionMap = getOptions(clazz, args);

        Map<String, String> paramsMap = new HashMap<String, String>();
        if (CommonUtil.isNotEmpty(optionMap)) {
            for (Entry<String, Option> entry : optionMap.entrySet()) {
                String value = null;
                Option option = entry.getValue();

                if (CommonUtil.isNotEmpty(args)) {
                    StringBuilder sb = new StringBuilder();
                    boolean isBegin = false;
                    for (String arg : args) {
                        if (isBegin) {
                            if (optionMap.containsKey(arg)) {
                                break;
                            }
                            if (sb.length() > 0) {
                                sb.append(' ');
                            }
                            sb.append(arg);
                        }
                        else if (StringUtils.equals(arg, option.name())) {
                            isBegin = true;
                        }
                    }

                    if (isBegin) {
                        value = sb.length() > 0 ? sb.toString() : "true";
                    }
                }

                if (CommonUtil.isEmpty(value)) {
                    value = option.value();

                    if (CommonUtil.isEmpty(value) && option.required()) {
                        throw new IllegalArgumentException("缺少" + option.name() + "参数");
                    }
                }

                paramsMap.put(entry.getKey(), value);
            }
        }
        return paramsMap;
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param clazz <br>
     * @param args <br>
     * @return <br>
     */
    private static Map<String, Option> getOptions(Class<?> clazz, String[] args) {
        Map<String, Option> optionMap = new HashMap<String, Option>();

        Field[] fileds = clazz.getDeclaredFields();
        for (Field field : fileds) {
            if (field.isAnnotationPresent(Option.class)) {
                optionMap.put(field.getName(), field.getAnnotation(Option.class));
            }
        }
        return optionMap;
    }
}
