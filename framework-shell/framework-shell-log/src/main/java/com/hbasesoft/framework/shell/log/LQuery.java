/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.shell.log;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Component;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.hbasesoft.framework.common.utils.PropertyHolder;
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
 * @CreateDate 2020年10月28日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.shell.log <br>
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
        Assert.notEmpty(option.filePath, "-f 文件路径为参数必填参数");

        File file = new File(option.filePath);
        Assert.isTrue(file.exists(), "日志文件不存在");

        String fileName = file.getName();
        int index = fileName.indexOf(".");
        if (index != -1) {
            fileName = fileName.substring(0, index);
        }
        shell.out.print("...正在加载");
        shell.out.print(option.filePath);
        shell.out.println("文件...");

        SparkSession spark = SparkSession.builder().appName(PropertyHolder.getProjectName()).master("local")
            .getOrCreate();

        try {
            Dataset<Row> df = spark.read().json(option.filePath);
            df.createTempView(fileName);
            shell.out.print("加载文件成功，表名：");
            shell.out.println(fileName);
            shell.out.print(">> ");
            while (!shell.isExit() && shell.getScanner().hasNextLine()) {
                String sql = StringUtils.trim(shell.getScanner().nextLine());
                if (StringUtils.isNotEmpty(sql)) {
                    if ("exit".equalsIgnoreCase(sql)) {
                        break;
                    }
                    else if ("show".equalsIgnoreCase(sql)) {
                        df.printSchema();
                    }
                    else {
                        try {
                            Dataset<Row> sqlDF = spark.sql(sql);
                            sqlDF.show(option.truncate);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                shell.out.print("\n>> ");
            }

        }
        catch (AnalysisException e) {
            e.printStackTrace();
        }
        finally {
            spark.close();
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
        @Parameter(names = {
            "--file", "-f"
        }, help = true, order = 1, description = "日志文件的位置")
        private String filePath;

        @Parameter(names = {
            "--truncate", "-t"
        }, help = true, order = 2, description = "加上该参数，则只显示一部分数据")
        private boolean truncate;
    }
}
