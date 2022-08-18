/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.flume.elasticsearch;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.flume.Event;
import org.apache.flume.event.SimpleEvent;
import org.joda.time.DateTimeUtils;

import com.google.common.collect.Maps;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Aug 18, 2022 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.log.flume.elasticsearch <br>
 */
public class TimestampedEvent extends SimpleEvent {

    private final long timestamp;

    TimestampedEvent(Event base) {
        setBody(base.getBody());
        Map<String, String> headers = Maps.newHashMap(base.getHeaders());
        String timestampString = headers.get("timestamp");
        if (StringUtils.isBlank(timestampString)) {
            timestampString = headers.get("@timestamp");
        }

        if (StringUtils.isBlank(timestampString)) {
            this.timestamp = DateTimeUtils.currentTimeMillis();
            headers.put("timestamp", String.valueOf(timestamp));
        }
        else {
            this.timestamp = Long.valueOf(timestampString);
        }
        setHeaders(headers);
        base.setHeaders(headers);
    }

    long getTimestamp() {
        return timestamp;
    }
}
