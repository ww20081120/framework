/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.flume.jdbc.client;

import org.apache.flume.Event;
import org.apache.flume.conf.Configurable;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Aug 18, 2022 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.log.flume.jdbc.client <br>
 */
public interface JdbcClient extends Configurable {

    /**
     * Close connection to elastic search in client
     */
    void close();

    /**
     * Add new event to the bulk
     *
     * @param event Flume Event
     * @throws Exception
     */
    void addEvent(Event event) throws Exception;

    /**
     * Sends bulk to the elasticsearch cluster
     *
     * @throws Exception
     */
    void execute() throws Exception;
}
