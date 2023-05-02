/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.cg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.UtilException;
import com.hbasesoft.framework.common.utils.bean.BeanUtil;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TableParseUtil {

    /** */
    private static final int CONSTANT_9 = 9;

    /**
     * Description: 解析表<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param tableCode
     * @param dataSource
     * @return <br>
     */
    public static TableInfo parse(final String tableCode, final DataSource dataSource) {
        JdbcTemplate db = new JdbcTemplate(dataSource);
        String code = BeanUtil.toCapitalizeCamelCase(
            tableCode.length() > 2 && tableCode.charAt(1) == '_' ? tableCode.substring(2) : tableCode);
        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableCode(tableCode);
        tableInfo.setCode(code);

        Map<String, Object> result = db.queryForMap("SHOW CREATE TABLE " + tableCode);
        String createSql = (String) result.get("Create Table");
        if (StringUtils.isNotEmpty(createSql)) {
            String remarkes = parseComment(createSql);
            tableInfo.setComment(remarkes);
            if (StringUtils.isNotEmpty(remarkes)) {
                tableInfo.setName(StringUtils.split(remarkes)[0]);
            }
            else {
                tableInfo.setName(code);
            }
            tableInfo.setColumnInfos(parseColumns(db, tableCode));
        }
        else {
            throw new UtilException(ErrorCodeDef.QUERY_ERROR, tableCode);
        }

        return tableInfo;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param db
     * @param tableCode
     * @return <br>
     */
    private static List<ColumnInfo> parseColumns(final JdbcTemplate db, final String tableCode) {
        List<ColumnInfo> columnInfos = new ArrayList<>();

        List<Map<String, Object>> mapList = db.queryForList("show full columns from " + tableCode);
        if (CollectionUtils.isNotEmpty(mapList)) {
            mapList.forEach(map -> {
                ColumnInfo info = new ColumnInfo();
                info.setDbCode((String) map.get("Field"));
                info.setCode(BeanUtil.toCamelCase(info.getDbCode()));

                info.setComment((String) map.get("Comment"));
                if (StringUtils.isNotEmpty(info.getComment())) {
                    info.setName(StringUtils.split(info.getComment())[0]);
                    info.setValueList(parseValueList(info.getComment(), info.getCode()));
                }
                else {
                    info.setName(info.getCode());
                }
                info.setDefaultValue((String) map.get("Default"));
                info.setPrimaryKey("PRI".equals(map.get("Key")));
                info.setRequired("NO".equals(map.get("Null")));
                String type = (String) map.get("Type");
                int index = type.indexOf("(");
                if (index != -1) {
                    String[] ls = StringUtils.split(type.substring(index + 1, type.length() - 1), ",");
                    info.setLength(Integer.parseInt(ls[0]));
                    if (ls.length > 1) {
                        info.setPrecision(Integer.parseInt(ls[1]));
                    }
                    type = type.substring(0, index);
                }
                info.setType(type);
                info.setJavaType(getJavaType(type.toUpperCase()));

                columnInfos.add(info);
            });
        }
        return columnInfos;
    }

    private static String getJavaType(final String type) {
        if (type.contains("INT") || "BIT".equals(type)) {
            return "Integer";
        }
        else if ("LONG".equals(type) || "NUMBER".equals(type) || "BIGINT".equals(type)) {
            return "Long";
        }
        else if ("FLOAT".equals(type) || "DOUBLE".equals(type)) {
            return "Double";
        }
        else if ("DATE".equals(type) || "TIME".equals(type) || "DATETIME".equals(type) || "TIMESTAMP".equals(type)) {
            return "java.util.Date";
        }
        else if (type.contains("BINARY") || type.contains("BLOB")) {
            return "byte[]";
        }
        else if (type.equals("DECIMAL")) {
            return "java.math.BigDecimal";
        }
        else {
            return "String";
        }
    }

    private static String parseComment(final String all) {
        String comment = null;
        int index = all.indexOf("COMMENT='");
        if (index < 0) {
            return GlobalConstants.BLANK;
        }
        comment = all.substring(index + CONSTANT_9);
        comment = comment.substring(0, comment.length() - 1);
        return comment;
    }

    private static String parseValueList(final String comment, final String code) {
        if (StringUtils.isNotEmpty(comment)) {
            String str = StringUtils.trim(StringUtils.replace(comment, "，", GlobalConstants.SPLITOR));
            if (str.indexOf(" ") != -1 && str.indexOf("-") != -1 && str.indexOf(GlobalConstants.SPLITOR) != -1) {
                return "map|" + StringUtils.replace(str.substring(str.indexOf(" ") + 1), "-", "=");
            }

            if (comment.endsWith(" (字典)") || comment.endsWith(" （字典）")) {
                return "dict|" + code;
            }
        }
        return GlobalConstants.BLANK;
    }
}
