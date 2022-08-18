/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.flume.elasticsearch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.conf.ComponentConfiguration;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.date.DateUtil;

/**
 * <Description> <br>
 * Serialize flume events into the same format LogStash uses
 * </p>
 * This can be used to send events to ElasticSearch and use clients such as Kabana which expect Logstash formated
 * indexes
 *
 * <pre>
 * {
 *    "@timestamp": "2010-12-21T21:48:33.309258Z",
 *    "@tags": [ "array", "of", "tags" ],
 *    "@type": "string",
 *    "@source": "source of the event, usually a URL."
 *    "@source_host": ""
 *    "@source_path": ""
 *    "@fields":{
 *       # a set of fields for this event
 *       "user": "jordan",
 *       "command": "shutdown -r":
 *     }
 *     "@message": "the original plain-text message"
 *   }
 * </pre>
 *
 * If the following headers are present, they will map to the above logstash output as long as the logstash fields are
 * not already present.
 * </p>
 *
 * <pre>
 *  timestamp: long -> @timestamp:Date
 *  host: String -> @source_host: String
 *  src_path: String -> @source_path: String
 *  type: String -> @type: String
 *  source: String -> @source: String
 * </pre>
 *
 * @see https ://github.com/logstash/logstash/wiki/logstash%27s-internal-message- format
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Aug 18, 2022 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.log.flume.elasticsearch <br>
 */
public class ElasticSearchLogStashEventSerializer implements ElasticSearchEventSerializer {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param event
     * @return
     * @throws IOException <br>
     */
    @Override
    public String getContentBuilder(Event event) throws IOException {
        JSONObject builder = new JSONObject();
        appendBody(builder, event);
        appendHeaders(builder, event);
        return builder.toJSONString();
    }

    private void appendHeaders(JSONObject builder, Event event) throws IOException {
        Map<String, String> headers = Maps.newHashMap(event.getHeaders());
        builder.putAll(headers);

        String timestamp = headers.get("timestamp");
        if (!StringUtils.isBlank(timestamp) && StringUtils.isBlank(headers.get("@timestamp"))) {
            long timestampMs = Long.parseLong(timestamp);
            builder.put("@timestamp", DateUtil.date2String(new Date(timestampMs), "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
        }

    }

    private void appendBody(JSONObject builder, Event event) throws IOException, UnsupportedEncodingException {
        byte[] body = event.getBody();
        Object data = getJson(new String(body));
        if (data instanceof JSONObject) {
            builder.putAll((JSONObject) data);
        }
        else {
            builder.put("message", data);
        }
    }

    private Object getJson(String body) {
        if (StringUtils.isNotEmpty(body)) {
            body = StringUtils.trim(body);
            if (body.startsWith("{") && body.endsWith("}")) {
                try {
                    return JSONObject.parseObject(body);
                }
                catch (Exception e) {
                }
            }
            return body;
        }
        return GlobalConstants.BLANK;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param context <br>
     */
    @Override
    public void configure(Context context) {
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param conf <br>
     */
    @Override
    public void configure(ComponentConfiguration conf) {
    }
}
