/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.flume.jdbc;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Aug 18, 2022 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.log.flume.jdbc <br>
 */
public interface JdbcSinkConstants {

    String TABLE_NAME = "tableName";

    String BATCH_SIZE = "batchSize";

    /**
     * The fully qualified class name of the serializer the sink should use.
     */
    String SERIALIZER = "serializer";

    /**
     * Configuration to pass to the serializer.
     */
    String SERIALIZER_PREFIX = SERIALIZER + ".";

    String DATASOURCE_CODE = "datasourceCode";

    String CLIENT_PREFIX = "client";

    String DEFAULT_DATASOURCE_CODE = "master";

    String DEFAULT_TABLE_NAME = "t_ability_api_log";

    String DEFAULT_SERIALIZER_CLASS = "com.hbasesoft.framework.log.flume.core.LogStashEventSerializer";

}
