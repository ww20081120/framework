/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.shell.tx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Component;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.hbasesoft.framework.common.utils.date.DateUtil;
import com.hbasesoft.framework.shell.core.Assert;
import com.hbasesoft.framework.shell.core.CommandHandler;
import com.hbasesoft.framework.shell.core.Shell;
import com.hbasesoft.framework.shell.core.vo.AbstractOption;
import com.hbasesoft.framework.shell.tx.TxRetry.Option;
import com.hbasesoft.framework.shell.tx.entity.TxClientinfoEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2020年8月14日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.shell.tx <br>
 */
@Component
public class TxRetry implements CommandHandler<Option> {

    @Autowired
    private CassandraOperations cassandraOperations;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param cmd
     * @param option
     * @param shell <br>
     */
    @Override
    public void execute(JCommander cmd, Option option, Shell shell) {

        Assert.notEmpty(option.id, "ID必填");

        TxClientinfoEntity entity = cassandraOperations.selectOneById(option.id, TxClientinfoEntity.class);
        Assert.notNull(entity, "未查询到数据");

        if (entity.getCurrentRetryTimes() > entity.getMaxRetryTimes()) {
            entity.setMaxRetryTimes(entity.getCurrentRetryTimes() + 1);
        }

        entity.setNextRetryTime(DateUtil.getCurrentDate());
        cassandraOperations.update(entity);
        shell.out.println("更新Clientinfo成功, 请等待执行");

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String toString() {
        return "修改重试的参数";
    }

    @Getter
    @Setter
    public static class Option extends AbstractOption {

        @Parameter(names = {
            "-id"
        }, help = true, order = 1, description = "根据ID修改")
        private String id;
    }
}
