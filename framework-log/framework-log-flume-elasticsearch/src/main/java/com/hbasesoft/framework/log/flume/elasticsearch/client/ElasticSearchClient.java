/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.flume.elasticsearch.client;

import org.apache.flume.Event;
import org.apache.flume.conf.Configurable;

import com.hbasesoft.framework.log.flume.elasticsearch.IndexNameBuilder;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Aug 18, 2022 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.log.flume.elasticsearch.client <br>
 */
public interface ElasticSearchClient extends Configurable {

    /**
     * Close connection to elastic search in client
     */
    void close();

    /**
     * Add new event to the bulk
     *
     * @param event Flume Event
     * @param indexNameBuilder Index name builder which generates name of index to feed
     * @param indexType Name of type of document which will be sent to the elasticsearch cluster
     * @param ttlMs Time to live expressed in milliseconds. Value <= 0 is ignored
     * @throws Exception
     */
    void addEvent(Event event, IndexNameBuilder indexNameBuilder, String indexType, long ttlMs) throws Exception;

    /**
     * Sends bulk to the elasticsearch cluster
     *
     * @throws Exception
     */
    void execute() throws Exception;
}
