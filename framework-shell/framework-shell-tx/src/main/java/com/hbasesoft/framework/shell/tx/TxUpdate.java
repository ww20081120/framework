/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.shell.tx;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.stereotype.Component;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.hbasesoft.framework.shell.core.Assert;
import com.hbasesoft.framework.shell.core.CommandHandler;
import com.hbasesoft.framework.shell.core.Shell;
import com.hbasesoft.framework.shell.core.vo.AbstractOption;
import com.hbasesoft.framework.shell.tx.TxUpdate.Option;
import com.hbasesoft.framework.shell.tx.entity.TxCheckinfoEntity;
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
public class TxUpdate implements CommandHandler<Option> {

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

        if (StringUtils.isNotEmpty(option.result)) {
            Assert.notEmpty(option.mark, "标记必填");
            Query q = Query.query(Criteria.where("id").is(option.id), Criteria.where("mark").is(option.mark));
            TxCheckinfoEntity entity = cassandraOperations.selectOne(q, TxCheckinfoEntity.class);
            Assert.notNull(entity, "未查询到数据");

            entity.setResult(option.result);
            cassandraOperations.update(entity);
            shell.out.println("更新Checkinfo成功！");

        }

        if (StringUtils.isNotEmpty(option.args) || StringUtils.isNotEmpty(option.context)) {
            TxClientinfoEntity entity = cassandraOperations.selectOneById(option.id, TxClientinfoEntity.class);
            Assert.notNull(entity, "未查询到数据");

            if (StringUtils.isNotEmpty(option.args)) {
                entity.setArgs(option.args);
            }

            if (StringUtils.isNotEmpty(option.context)) {
                entity.setContext(option.context);
            }
            cassandraOperations.update(entity);
            shell.out.println("更新Clientinfo成功！");
        }

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

        @Parameter(names = {
            "--mark", "-m"
        }, help = true, order = 2, description = "根据标记修改")
        private String mark;

        @Parameter(names = {
            "--args", "-a"
        }, help = true, order = 3, description = "修改输入参数")
        private String args;

        @Parameter(names = {
            "--context", "-c"
        }, help = true, order = 4, description = "修改上下文")
        private String context;

        @Parameter(names = {
            "--result", "-r"
        }, help = true, order = 5, description = "修改结果")
        private String result;

    }
}
