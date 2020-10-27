/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.shell.log;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.hbasesoft.framework.shell.core.Assert;
import com.hbasesoft.framework.shell.core.CommandHandler;
import com.hbasesoft.framework.shell.core.Shell;
import com.hbasesoft.framework.shell.core.vo.AbstractOption;
import com.hbasesoft.framework.shell.log.LQuery.Option;

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
public class LQuery implements CommandHandler<Option> {

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
        Assert.notEmpty(option.sql, "SQL语句不存在");
        StringBuilder sb = new StringBuilder();
        option.sql.forEach(s -> sb.append(s).append(' '));

        List<SQLStatement> statementList = SQLUtils.parseStatements(sb.toString(), "json");
        if (CollectionUtils.isNotEmpty(statementList)) {
            SQLStatement statement = statementList.get(0);
            Assert.isTrue(statement instanceof SQLSelectStatement, "只支持查询语句");
            
            
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
        return "使用SQL语句查询日志";
    }

    @Getter
    @Setter
    public static class Option extends AbstractOption {
        @Parameter(help = true, order = 1, description = "查询的SQL语句")
        private List<String> sql;
    }
}
