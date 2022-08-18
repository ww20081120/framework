/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.flume.elasticsearch;

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
public interface ElasticSearchSinkConstants {
    /**
     * Comma separated list of hostname:port, if the port is not present the default port '9300' will be used
     * </p>
     * Example:
     * 
     * <pre>
     *  127.0.0.1:92001,127.0.0.2:9300
     * </pre>
     */
    String HOSTNAMES = "hostNames";

    /**
     * The name to index the document to, defaults to 'flume'
     * </p>
     * The current date in the format 'yyyy-MM-dd' will be appended to this name, for example 'foo' will result in a
     * daily index of 'foo-yyyy-MM-dd'
     */
    String INDEX_NAME = "indexName";

    /**
     * The type to index the document to, defaults to 'log'
     */
    String INDEX_TYPE = "indexType";

    /**
     * Name of the ElasticSearch cluster to connect to
     */
    String CLUSTER_NAME = "clusterName";

    /**
     * Maximum number of events the sink should take from the channel per transaction, if available. Defaults to 100
     */
    String BATCH_SIZE = "batchSize";

    /**
     * TTL in days, when set will cause the expired documents to be deleted automatically, if not set documents will
     * never be automatically deleted
     */
    String TTL = "ttl";

    /**
     * The fully qualified class name of the serializer the sink should use.
     */
    String SERIALIZER = "serializer";

    /**
     * Configuration to pass to the serializer.
     */
    String SERIALIZER_PREFIX = SERIALIZER + ".";

    /**
     * The fully qualified class name of the index name builder the sink should use to determine name of index where the
     * event should be sent.
     */
    String INDEX_NAME_BUILDER = "indexNameBuilder";

    /**
     * The fully qualified class name of the index name builder the sink should use to determine name of index where the
     * event should be sent.
     */
    String INDEX_NAME_BUILDER_PREFIX = INDEX_NAME_BUILDER + ".";

    /**
     * The client type used for sending bulks to ElasticSearch
     */
    String CLIENT_TYPE = "client";

    /**
     * The client prefix to extract the configuration that will be passed to elasticsearch client.
     */
    String CLIENT_PREFIX = CLIENT_TYPE + ".";

    /**
     * DEFAULTS USED BY THE SINK
     */

    int DEFAULT_PORT = 9300;

    int DEFAULT_TTL = -1;

    String DEFAULT_INDEX_NAME = "flume";

    String DEFAULT_INDEX_TYPE = "log";

    String DEFAULT_CLUSTER_NAME = "elasticsearch";

    String DEFAULT_CLIENT_TYPE = "rest";

    String TTL_REGEX = "^(\\d+)(\\D*)";

    String DEFAULT_SERIALIZER_CLASS = "com.hbasesoft.framework.log.flume.elasticsearch.ElasticSearchLogStashEventSerializer";

    String DEFAULT_INDEX_NAME_BUILDER_CLASS = "com.hbasesoft.framework.log.flume.elasticsearch.TimeBasedIndexNameBuilder";
}
