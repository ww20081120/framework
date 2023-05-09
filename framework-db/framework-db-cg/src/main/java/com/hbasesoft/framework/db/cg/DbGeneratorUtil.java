/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.cg;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.hbasesoft.framework.common.utils.date.DateUtil;
import com.hbasesoft.framework.common.utils.engine.VelocityParseFactory;
import com.hbasesoft.framework.common.utils.io.IOUtil;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2023年4月2日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.shell.cg.util <br>
 */
public class DbGeneratorUtil {

    /**
     * pkg
     */
    private final String basePackage;

    /**
     * outPath
     */
    private final File outDir;

    /**
     * template
     */
    private final String daoTemplate;

    /** entity template */
    private final String entityTemplate;

    /**
     * @param pkg
     * @param outPath
     */
    public DbGeneratorUtil(final String pkg, final String outPath) {
        this.basePackage = pkg;
        this.outDir = new File(outPath);
        if (!this.outDir.isDirectory() || !this.outDir.exists()) {
            this.outDir.mkdirs();
        }

        daoTemplate = IOUtil.readString(
            this.getClass().getClassLoader().getResourceAsStream("com/hbasesoft/framework/db/cg/daoTemplate.vm"));
        entityTemplate = IOUtil.readString(
            this.getClass().getClassLoader().getResourceAsStream("com/hbasesoft/framework/db/cg/entityTemplate.vm"));
    }

    /**
     * Description:生成dao <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param tableInfo <br>
     */
    public void generateDao(final TableInfo tableInfo) {
        File dir = new File(outDir, "dao");
        if (!dir.isDirectory() || !dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, tableInfo.getCode() + "Dao.java");
        if (!file.exists()) {
            Map<String, Object> map = new HashMap<>();
            map.put("table", tableInfo);
            map.put("package", basePackage);
            map.put("DATE", DateUtil.date2String(DateUtil.getCurrentDate()));
            String daoData = VelocityParseFactory.parse("daoTemplate", daoTemplate, map);
            IOUtil.writeFile(daoData, file);
            LoggerUtil.info("{0}文件生成成功！", file.getAbsolutePath());
        }
        else {
            LoggerUtil.info("{0}文件已存在，请删除后在生成！", file.getAbsolutePath());
        }
    }

    /**
     * Description: 生成entity<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param tableInfo <br>
     */
    public void generateEntity(final TableInfo tableInfo) {
        File dir = new File(outDir, "entity");
        if (!dir.isDirectory() || !dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, tableInfo.getCode() + "Entity.java");
        if (!file.exists()) {
            Map<String, Object> map = new HashMap<>();
            map.put("table", tableInfo);
            map.put("package", basePackage);
            map.put("DATE", DateUtil.date2String(DateUtil.getCurrentDate()));
            generateEntityCode(tableInfo, map);

            String daoData = VelocityParseFactory.parse("entityTemplate", entityTemplate, map);
            IOUtil.writeFile(daoData, file);
            LoggerUtil.info("{0}文件生成成功！", file.getAbsolutePath());
        }
        else {
            LoggerUtil.info("{0}文件已存在，请删除后在生成！", file.getAbsolutePath());
        }
    }

    private void generateEntityCode(final TableInfo tableInfo, final Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        tableInfo.getColumnInfos().forEach(column -> {
            if (column.isPrimaryKey()) {
                map.put("ENTITY",
                    new StringBuilder().append(" @Entity(name = \"").append(tableInfo.getTableCode()).append("\")"));
            }
            generateEntityFieldStr(column, sb);
        });
        map.put("CODE", sb.toString());
    }

    private void generateEntityFieldStr(final ColumnInfo column, final StringBuilder sb) {
        sb.append('\n');
        sb.append("    ").append("/** ").append(column.getComment()).append(" */").append('\n');
        if (column.isPrimaryKey()) {
            sb.append("    ").append("@Id").append('\n');
            if ("Integer".equals(column.getJavaType())) {
                sb.append("    ").append("@GeneratedValue(strategy = GenerationType.IDENTITY)").append('\n');
                sb.append("    ").append("@GenericGenerator(name = \"persistenceGenerator\", strategy = \"increment\")")
                    .append('\n');
            }
            else {
                sb.append("    ").append("@GeneratedValue(generator = \"paymentableGenerator\")").append('\n');
                sb.append("    ").append("@GenericGenerator(name = \"paymentableGenerator\", strategy = \"uuid\")")
                    .append('\n');
            }
        }
        sb.append("    ").append("@Column(name = \"").append(column.getDbCode()).append("\")").append('\n');
        sb.append("    ").append("private ").append(column.getJavaType()).append(" ").append(column.getCode())
            .append(';').append('\n');
    }
}
