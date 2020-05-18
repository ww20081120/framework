/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.server.storage.cassandra;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.data.cassandra.core.CassandraOperations;

import com.hbasesoft.framework.common.StartupListener;
import com.hbasesoft.framework.common.utils.io.IOUtil;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Feb 4, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tx.server.storage.db <br>
 */
public class CreateTableLinstener implements StartupListener {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param context <br>
     */
    @Override
    public void complete(final ApplicationContext context) {
        CassandraOperations cassandraOperations = context.getBean(CassandraOperations.class);
        createTableIfNotExist(cassandraOperations);
    }

    private void createTableIfNotExist(CassandraOperations cassandraOperations) {
        try {
            cassandraOperations.getCqlOperations().execute("select id from t_tx_check_info limit 1");
            return;
        }
        catch (Exception e) {
            LoggerUtil.error(e);
        }

        String cqls = IOUtil.readString(this.getClass().getClassLoader().getResourceAsStream("setup.cql"));
        if (StringUtils.isNotEmpty(cqls)) {
            for (String cql : StringUtils.split(cqls, ";")) {
                cql = StringUtils.trim(cql);
                if (StringUtils.isNotEmpty(cql)) {
                    cassandraOperations.getCqlOperations().execute(cql);
                    LoggerUtil.info("执行[{0}]成功", cql);
                }
            }
        }
    }
}
