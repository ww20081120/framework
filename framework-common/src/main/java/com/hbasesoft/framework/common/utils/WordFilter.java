package com.hbasesoft.framework.common.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.hbasesoft.framework.common.GlobalConstants;

/**
 * <Description> 敏感词过滤工具类 <br>
 * 
 * @author hejiawen<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年2月16日 <br>
 * @since V1.0<br>
 * @see com.fccfc.migu.kf.portal.util <br>
 */
@SuppressWarnings("rawtypes")
public class WordFilter {

    private static final char endTag = (char) (1);

    private Map<Character, Map> filterMap = new HashMap<Character, Map>(1024);

    @SuppressWarnings("unchecked")
    public void init(Collection<String> filterWordList) {
        if (filterMap != null && filterMap.size() > 0) {
            filterMap.clear();
        }

        if (CollectionUtils.isNotEmpty(filterWordList)) {
            for (String filterWord : filterWordList) {
                char[] charArray = filterWord.trim().toCharArray();
                int len = charArray.length;
                if (len > 0) {
                    Map<Character, Map> subMap = filterMap;
                    for (int i = 0; i < len - 1; i++) {
                        Map<Character, Map> obj = subMap.get(charArray[i]);
                        if (obj == null) {
                            // 新索引，增加HashMap
                            int size = (int) Math.max(2, 16 / Math.pow(2, i));
                            Map<Character, Map> subMapTmp = new HashMap<Character, Map>(size);
                            subMap.put(charArray[i], subMapTmp);
                            subMap = subMapTmp;
                        }
                        else {
                            // 索引已经存在
                            subMap = obj;
                        }
                    }
                    // 处理最后一个字符
                    Map<Character, HashMap> obj = subMap.get(charArray[len - 1]);
                    if (obj == null) {
                        // 新索引，增加HashMap，并设置结束符
                        int size = (int) Math.max(2, 16 / Math.pow(2, len - 1));
                        HashMap<Character, HashMap> subMapTmp = new HashMap<Character, HashMap>(size);
                        subMapTmp.put(endTag, null);
                        subMap.put(charArray[len - 1], subMapTmp);
                    }
                    else {
                        // 索引已经存在,设置结束符
                        obj.put(endTag, null);
                    }
                }
            }
        }
    }

    // 返回是否包含需要过滤的词
    @SuppressWarnings("unchecked")
    public boolean hasFilterWord(String info) {
        if (info == null || info.length() == 0) {
            return false;
        }
        char[] charArray = info.toCharArray();
        int len = charArray.length;
        for (int i = 0; i < len; i++) {
            int index = i;
            Map<Character, Map> sub = filterMap.get(charArray[index]);
            while (sub != null) {
                if (sub.containsKey(endTag)) {
                    // 匹配结束
                    return true;
                }
                else {
                    index++;
                    if (index >= len) {
                        // 字符串结束
                        return false;
                    }
                    sub = sub.get(charArray[index]);
                }
            }
        }
        return false;
    }

    // 将字符串中包含的关键词过滤并替换为指定字符串
    @SuppressWarnings("unchecked")
    public String getFilterString(String info, String replaceTag) {
        if (info == null || info.length() == 0 || replaceTag == null) {
            return info;
        }
        char[] charArray = info.toCharArray();
        int len = charArray.length;
        StringBuilder newInfo = new StringBuilder(GlobalConstants.BLANK);
        int i = 0;
        while (i < len) {
            int end = -1;
            int index;
            Map<Character, Map> sub = filterMap;
            for (index = i; index < len; index++) {
                sub = sub.get(charArray[index]);
                if (sub == null) {
                    // 匹配失败，将已匹配的最长字符进行替换
                    if (end == -1) {
                        // 没匹配到任何关键词
                        newInfo.append(charArray[i]);
                        i++;
                        break;
                    }
                    else {
                        // 将最长匹配字符串替换为特定字符
                        for (int j = i; j <= end; j++) {
                            newInfo.append(replaceTag);
                        }
                        i = end + 1;
                        break;
                    }
                }
                else {
                    if (sub.containsKey(endTag)) {
                        // 匹配
                        end = index;
                    }
                }
            }
            if (index >= len) {
                // 字符串结束
                if (end == -1) {
                    // 没匹配到任何关键词
                    newInfo.append(charArray[i]);
                    i++;
                }
                else {
                    // 将最长匹配字符串替换为特定字符
                    for (int j = i; j <= end; j++) {
                        newInfo.append(replaceTag);
                    }
                    i = end + 1;
                }
            }
        }
        return newInfo.toString();
    }

    @SuppressWarnings("unchecked")
    public String getKeyword(String statement) {
        if (StringUtils.isEmpty(statement)) {
            return statement;
        }
        char[] charArray = statement.toCharArray();
        int len = charArray.length;
        int i = 0;
        while (i < len) {
            int end = -1;
            int index;
            Map<Character, Map> sub = filterMap;
            for (index = i; index < len; index++) {
                sub = sub.get(charArray[index]);
                if (sub == null) {
                    // 匹配失败，将已匹配的最长字符进行替换
                    if (end == -1) {
                        i++;
                        break;
                    }
                    else {
                        // 将最长匹配字符串替换为特定字符
                        StringBuilder sb = new StringBuilder();
                        for (int j = i; j <= end; j++) {
                            sb.append(charArray[j]);
                        }
                        return sb.toString();
                    }
                }
                else {
                    if (sub.containsKey(endTag)) {
                        end = index;
                    }
                }
            }
            if (index >= len) {
                // 字符串结束
                if (end == -1) {
                    i++;
                }
                else {
                    // 将最长匹配字符串替换为特定字符
                    StringBuilder sb = new StringBuilder();
                    for (int j = i; j <= end; j++) {
                        sb.append(charArray[j]);
                    }
                    return sb.toString();
                }
            }
        }
        return null;
    }
}
