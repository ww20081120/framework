/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.shell.tx;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.data.cassandra.core.query.CriteriaDefinition;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.stereotype.Component;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.hbasesoft.framework.common.utils.date.DateUtil;
import com.hbasesoft.framework.shell.core.CommandHandler;
import com.hbasesoft.framework.shell.core.Shell;
import com.hbasesoft.framework.shell.core.vo.AbstractOption;
import com.hbasesoft.framework.shell.tx.TxGet.Option;
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
public class TxGet implements CommandHandler<Option> {

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

        com.hbasesoft.framework.shell.core.Assert.notEmpty(option.id, "ID必填");

        cds.add(Criteria.where("id").is(option.id));

        if (StringUtils.isNotEmpty(option.mark)) {
            cds.add(Criteria.where("mark").is(option.mark));
        }

        if (option.isCount()) {
            Query q = Query.query(cds.toArray(new CriteriaDefinition[0]));

            long s = cassandraOperations.count(q, TxClientinfoEntity.class);
            shell.out.println("统计到：" + s + "条数据。");
        }
        else {
            Query q = Query.query(cds.toArray(new CriteriaDefinition[0]));
            List<TxCheckinfoEntity> entities = cassandraOperations.select(q, TxCheckinfoEntity.class);

            shell.out.println("ID\t\t标记(mark)\t\t结果(args)\t\t创建时间(createTime)");

            if (CollectionUtils.isNotEmpty(entities)) {
                for (TxCheckinfoEntity entity : entities) {
                    shell.out.print(entity.getId());
                    shell.out.print("\t\t");
                    shell.out.print(entity.getMark());
                    shell.out.print("\t\t");
                    shell.out.print(entity.getResult());
                    shell.out.print("\t\t");
                    shell.out.println(DateUtil.date2String(entity.getCreateTime()));
                }
            }
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
        return "获取具体某个执行过程的结果";
    }

    @Getter
    @Setter
    public static class Option extends AbstractOption {

        @Parameter(names = {
            "-id"
        }, help = true, order = 1, description = "根据ID查询，必填")
        private String id;

        @Parameter(names = {
            "--mark", "-m"
        }, help = true, order = 2, description = "根据标记查询")
        private String mark;

        @Parameter(names = {
            "--count", "-c"
        }, help = true, order = 5, description = "统计数量")
        private boolean count = false;

    }
}
