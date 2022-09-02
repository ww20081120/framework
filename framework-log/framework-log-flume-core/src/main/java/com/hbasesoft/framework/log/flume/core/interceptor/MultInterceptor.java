/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.flume.core.interceptor;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

import com.google.common.collect.Lists;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Sep 2, 2022 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.log.flume.core.interceptor <br>
 */
public class MultInterceptor implements Interceptor {

    // 过滤正则
    private Pattern regex = null;

    // 截取标志
    private Boolean cutFlag = true;

    // 总截取最大长度
    private Integer cutMax = null;

    // 单个截取最大长度
    private Integer singleCut = null;

    // 最后一个事件流
    private List<Event> lastList = Lists.newArrayList();

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Override
    public void initialize() {
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param event
     * @return <br>
     */
    @Override
    public Event intercept(Event event) {
        return event;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param events
     * @return <br>
     */
    @Override
    public List<Event> intercept(List<Event> events) {
        // 处理结果 event list
        List<Event> intercepted = null;
        int addnum = 0;// 记录上一个正确匹配的event在队列中的位置,以便下一event有和它连接的需要

        if (CollectionUtils.isNotEmpty(lastList)) {
            // 初始化
            int initCapacity = events.size() + lastList.size();
            intercepted = Lists.newArrayListWithCapacity(initCapacity);
            // 添加
            intercepted.addAll(lastList);

            // 清空
            lastList = Lists.newArrayList();
        }
        else {
            intercepted = Lists.newArrayListWithCapacity(events.size());
        }

        // 有正则的情况
        for (Event event : events) {
            Event interceptedEvent = null;
            Matcher matcher = regex.matcher(new String(event.getBody(), StandardCharsets.UTF_8));
            if (matcher.find()) {
                interceptedEvent = intercept(event);
                // 单个的body
                String singleBody = new String(interceptedEvent.getBody(), StandardCharsets.UTF_8);
                int singleBodyLen = singleBody.length();
                if (cutFlag) {
                    // 处理最大截取数边界条件--一定要重新一个变量接收
                    int lsSingleCut = singleCut > singleBodyLen ? singleBodyLen : singleCut;
                    // 截取字符串--新变量
                    String singleCutBody = new String(singleBody.substring(0, lsSingleCut));

                    // 重新赋值body
                    interceptedEvent.setBody(singleCutBody.getBytes());
                }

                intercepted.add(interceptedEvent);
                addnum = addnum + 1;
            }
            else {
                if (intercepted.size() == 0) {
                    // 表示本次没有匹配上
                    continue;
                }
                addnum = addnum >= intercepted.size() ? intercepted.size() - 1 : addnum;

                String body = new String(intercepted.get(addnum).getBody(), StandardCharsets.UTF_8) + "\n"
                    + new String(event.getBody(), StandardCharsets.UTF_8);

                int bodyLen = body.length();
                // 截取body-新变量
                String cutBody = body;
                if (cutFlag) {

                    // 处理最大截取数边界条件--新变量
                    int lsCutMax = cutMax > bodyLen ? bodyLen : cutMax;
                    // 截取字符串
                    cutBody = new String(body.substring(0, lsCutMax));
                }
                intercepted.get(addnum).setBody(cutBody.getBytes());
            }
        }

        // 最后一个保存在静态变量，等待下一批次
        if (intercepted != null && intercepted.size() > 0) {
            int lastIndex = intercepted.size() - 1;
            lastList.add(intercepted.get(lastIndex));
            // 移除最后一个索引
            intercepted.remove(lastIndex);
        }
        return intercepted;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Override
    public void close() {
        lastList.clear();
    }

    public static class Builder implements Interceptor.Builder {
        // 过滤正则
        private Pattern regex = null;

        // 截取标志
        private Boolean cutFlag = true;

        // 总截取最大长度
        private Integer cutMax = null;

        // 单个截取最大长度
        private Integer singleCut = null;

        @Override
        public Interceptor build() {
            MultInterceptor interceptor = new MultInterceptor();
            interceptor.regex = this.regex;
            interceptor.cutFlag = this.cutFlag;
            interceptor.cutMax = this.cutMax;
            interceptor.singleCut = this.singleCut;
            return interceptor;
        }

        @Override
        public void configure(Context context) {
            this.cutFlag = context.getBoolean("cutFlag", true);
            this.cutMax = context.getInteger("cutMax", 0) * 1024;
            this.singleCut = context.getInteger("singleCut", 0) * 1024;

            String regexStr = context.getString("regex", null);
            if (StringUtils.isEmpty(regexStr)) {
                throw new IllegalArgumentException("regex is not set");
            }
            // 转换正则
            this.regex = Pattern.compile(regexStr);
        }
    }

}
