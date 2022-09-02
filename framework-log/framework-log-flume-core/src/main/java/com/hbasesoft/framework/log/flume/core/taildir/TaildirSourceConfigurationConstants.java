/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.flume.core.taildir;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Sep 2, 2022 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.log.flume.core.taildir <br>
 */
public interface TaildirSourceConfigurationConstants {
    /** Mapping for tailing file groups. */
    String FILE_GROUPS = "filegroups";

    String FILE_GROUPS_PREFIX = FILE_GROUPS + ".";

    /** Mapping for putting headers to events grouped by file groups. */
    String HEADERS_PREFIX = "headers.";

    /** Path of position file. */
    String POSITION_FILE = "positionFile";

    String DEFAULT_POSITION_FILE = "/.flume/taildir_position.json";

    /** What size to batch with before sending to ChannelProcessor. */
    String BATCH_SIZE = "batchSize";

    int DEFAULT_BATCH_SIZE = 100;

    /** Whether to skip the position to EOF in the case of files not written on the position file. */
    String SKIP_TO_END = "skipToEnd";

    boolean DEFAULT_SKIP_TO_END = false;

    /** Time (ms) to close idle files. */
    String IDLE_TIMEOUT = "idleTimeout";

    int DEFAULT_IDLE_TIMEOUT = 120000;

    /** Interval time (ms) to write the last position of each file on the position file. */
    String WRITE_POS_INTERVAL = "writePosInterval";

    int DEFAULT_WRITE_POS_INTERVAL = 3000;

    /** Whether to add the byte offset of a tailed line to the header */
    String BYTE_OFFSET_HEADER = "byteOffsetHeader";

    String BYTE_OFFSET_HEADER_KEY = "byteoffset";

    boolean DEFAULT_BYTE_OFFSET_HEADER = false;

    /**
     * Whether to cache the list of files matching the specified file patterns till parent directory is modified.
     */
    String CACHE_PATTERN_MATCHING = "cachePatternMatching";

    boolean DEFAULT_CACHE_PATTERN_MATCHING = true;

    /** Header in which to put absolute path filename. */
    String FILENAME_HEADER_KEY = "fileHeaderKey";

    String DEFAULT_FILENAME_HEADER_KEY = "file";

    /** Whether to include absolute path filename in a header. */
    String FILENAME_HEADER = "fileHeader";

    boolean DEFAULT_FILE_HEADER = false;

    /** The max number of batch reads from a file in one loop */
    String MAX_BATCH_COUNT = "maxBatchCount";

    Long DEFAULT_MAX_BATCH_COUNT = Long.MAX_VALUE;
}
