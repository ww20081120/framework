/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.flume.elasticsearch;

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
public interface IndexNameBuilder extends Configurable, ConfigurableComponent {
    /**
     * Gets the name of the index to use for an index request
     * 
     * @param event Event which determines index name
     * @return index name of the form 'indexPrefix-indexDynamicName'
     */
    String getIndexName(Event event);

    /**
     * Gets the prefix of index to use for an index request.
     * 
     * @param event Event which determines index name
     * @return Index prefix name
     */
    String getIndexPrefix(Event event);
}
