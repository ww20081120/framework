/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.shell.tx;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.data.cassandra.core.query.CriteriaDefinition;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.stereotype.Component;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.hbasesoft.framework.shell.core.Assert;
import com.hbasesoft.framework.shell.core.CommandHandler;
import com.hbasesoft.framework.shell.core.Shell;
import com.hbasesoft.framework.shell.core.vo.AbstractOption;
import com.hbasesoft.framework.shell.tx.TxDelete.Option;
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
public class TxDelete implements CommandHandler<Option> {

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

        List<CriteriaDefinition> cds = new ArrayList<>();
        Assert.notEmpty(option.id, "ID必填");
        cds.add(Criteria.where("id").is(option.id));

        if (option.all) {
            Query q = Query.query(cds.toArray(new CriteriaDefinition[0])).withAllowFiltering();
            cassandraOperations.delete(q, TxCheckinfoEntity.class);
            shell.out.println("删除TxCheckInfo成功！");

            cassandraOperations.delete(q, TxClientinfoEntity.class);
            shell.out.println("删除TxClientinfo成功！");
        }
        else {
            Assert.notEmpty(option.mark, "Mark必填");
            cds.add(Criteria.where("mark").is(option.mark));

            Query q = Query.query(cds.toArray(new CriteriaDefinition[0])).withAllowFiltering();

            cassandraOperations.delete(q, TxCheckinfoEntity.class);
            shell.out.println("删除TxCheckInfo成功！");
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
        return "删除重试任务";
    }

    @Getter
    @Setter
    public static class Option extends AbstractOption {

        @Parameter(names = {
            "-id"
        }, help = true, order = 1, description = "根据ID删除")
        private String id;

        @Parameter(names = {
            "--mark", "-m"
        }, help = true, order = 2, description = "根据标记删除")
        private String mark;

        @Parameter(names = {
            "--all", "-a"
        }, help = true, order = 3, description = "根据ID删除所有的信息，包含Client信息")
        private boolean all = false;

    }
}
