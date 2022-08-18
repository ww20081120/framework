/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.flume.elasticsearch;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.conf.ComponentConfiguration;
import org.apache.flume.formatter.output.BucketPath;

import com.hbasesoft.framework.common.utils.date.DateUtil;

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
public class TimeBasedIndexNameBuilder implements IndexNameBuilder {

    public static final String DATE_FORMAT = "dateFormat";

    public static final String DEFAULT_DATE_FORMAT = "yyyy.MM.dd";

    private String indexPrefix;

    private String dateFormatString;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param context <br>
     */
    @Override
    public void configure(Context context) {
        dateFormatString = context.getString(DATE_FORMAT);
        if (StringUtils.isBlank(dateFormatString)) {
            dateFormatString = DEFAULT_DATE_FORMAT;
        }
        indexPrefix = context.getString(ElasticSearchSinkConstants.INDEX_NAME);
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

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param event
     * @return <br>
     */
    @Override
    public String getIndexName(Event event) {
        TimestampedEvent timestampedEvent = new TimestampedEvent(event);
        long timestamp = timestampedEvent.getTimestamp();
        String realIndexPrefix = getIndexPrefix(event);
        return new StringBuilder().append(realIndexPrefix).append('-')
            .append(DateUtil.date2String(new Date(timestamp), dateFormatString)).toString();
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
    public String getIndexPrefix(Event event) {
        return BucketPath.escapeString(indexPrefix, event.getHeaders());
    }

}
