/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.flume.elasticsearch.client;

import com.hbasesoft.framework.common.utils.io.ProtocolUtil.Address;
import com.hbasesoft.framework.log.flume.core.EventSerializer;

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
public class ElasticSearchClientFactory {

    /**
     * @param clientType String representation of client type
     * @param hostNames Array of strings that represents hostnames with ports (hostname:port)
     * @param clusterName Elasticsearch cluster name used only by Transport Client
     * @param serializer Serializer of flume events to elasticsearch documents
     * @return
     */
    public ElasticSearchClient getClient(String clientType, Address[] hostNames, String clusterName,
        EventSerializer serializer) {
        return new ElasticSearchRestClient(hostNames, serializer);
    }
}
