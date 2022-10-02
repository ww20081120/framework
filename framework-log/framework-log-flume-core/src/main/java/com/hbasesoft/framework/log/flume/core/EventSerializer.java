/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.flume.core;

import java.io.IOException;

import org.apache.flume.Event;
import org.apache.flume.conf.Configurable;
import org.apache.flume.conf.ConfigurableComponent;

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
public interface EventSerializer extends Configurable, ConfigurableComponent {

    /**
     * Return an {@link BytesStream} made up of the serialized flume event
     * 
     * @param event The flume event to serialize
     * @return A {@link BytesStream} used to write to ElasticSearch
     * @throws IOException If an error occurs during serialization
     */
    String getContentBuilder(Event event) throws IOException;
}
