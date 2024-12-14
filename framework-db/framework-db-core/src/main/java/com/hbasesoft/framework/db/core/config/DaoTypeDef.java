/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core.config;

/**
 * <Description> <br>
 * 
 * @author ww200<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2024年12月14日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.core.config <br>
 */
public enum DaoTypeDef {

    /** 支持jdbc的数据库类型 */
    db,

    /** mongoDb */
    mongoDb,

    /** elastic-search */
    es,

    /** spark */
    spark,

    /** cassandra */
    cassandra,

    /** neo4j */
    neo4j,

    /** couchdb */
    couchdb,

    /** hbase */
    hbase,

    /** kudu */
    kudu

}
