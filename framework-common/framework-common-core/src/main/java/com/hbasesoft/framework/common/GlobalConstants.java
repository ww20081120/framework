/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common;

import java.nio.charset.Charset;

import com.hbasesoft.framework.common.utils.PropertyHolder;

/**
 * <Description> <br>
 *
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月24日 <br>
 * @see com.hbasesoft.framework.common.constant <br>
 */
public final class GlobalConstants {

    /** 分隔符 */
    public static final String SPLITOR = ",";

    /** 分隔符 */
    public static final String PARAM_SPLITOR = "&";

    /** 路径分割符 */
    public static final String PATH_SPLITOR = "/";

    /** 竖线 */
    public static final String VERTICAL_LINE = "|";

    /** SQL语句分隔符 */
    public static final String SQL_SPLITOR = ";";

    /** 等号分隔符 */
    public static final String EQUAL_SPLITER = "=";

    /** 下划线 */
    public static final char UNDERLINE = '_';

    /** 横杠 */
    public static final String LINE = "-";

    /** 空白 */
    public static final String BLANK = "";

    /** 星号 */
    public static final String ASTERISK = "*";

    /** 默认编码 */
    public static final Charset DEFAULT_CHARSET = Charset.forName("utf-8");

    /** 默认语言 */
    public static final String DEFAULT_LANGUAGE = "zh_CN";

    /** 百分号 */
    public static final String PERCENT = "%";

    /** 句号 */
    public static final String PERIOD = ".";

    /** 符号校验 */
    public static final String SYMBOL_REGULAR = "[\\pP\\p{Punct}]";

    /** 取值表达式 */
    public static final String DOLLAR_BRACE = "${";

    /** MINUTES */
    public static final int MINUTES = 60;

    /** 一秒等于1000毫秒 */
    public static final int SECONDS = 1000;

    /** 默认行 */
    public static final int DEFAULT_LINES = 1000;

    /** 全局文件路径 */
    public static final String FILE_STORAGE_PATH =
        PropertyHolder.getProperty("server.fileupload.filePath", System.getProperty("user.home"))
            + "/uploadFiles";

    /** 系统错错误码前缀 */
    public static final int SYSTEM_PREFIX = 0;
}
