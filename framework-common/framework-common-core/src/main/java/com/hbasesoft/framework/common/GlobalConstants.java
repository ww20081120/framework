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
public interface GlobalConstants {

    /** 分隔符 */
    String SPLITOR = ",";

    /** 分隔符 */
    String PARAM_SPLITOR = "&";

    /** 路径分割符 */
    String PATH_SPLITOR = "/";

    /** 竖线 */
    String VERTICAL_LINE = "|";

    /** SQL语句分隔符 */
    String SQL_SPLITOR = ";";

    /** 等号分隔符 */
    String EQUAL_SPLITER = "=";

    /** 下划线 */
    char UNDERLINE = '_';

    /** 横杠 */
    String LINE = "-";

    /** 空白 */
    String BLANK = "";

    /** 星号 */
    String ASTERISK = "*";

    /** 默认编码 */
    Charset DEFAULT_CHARSET = Charset.forName("utf-8");

    /** 默认语言 */
    String DEFAULT_LANGUAGE = "zh_CN";

    /** 百分号 */
    String PERCENT = "%";

    /** 句号 */
    String PERIOD = ".";

    /** 符号校验 */
    String SYMBOL_REGULAR = "[\\pP\\p{Punct}]";

    /** 取值表达式 */
    String DOLLAR_BRACE = "${";

    /** MINUTES */
    int MINUTES = 60;

    /** 一秒等于1000毫秒 */
    int SECONDS = 1000;

    /** 默认行 */
    int DEFAULT_LINES = 1000;

    /** 全局文件路径 */
    String FILE_STORAGE_PATH = PropertyHolder.getProperty("server.fileupload.filePath", System.getProperty("user.home"))
        + "/uploadFiles";

    /** 系统错错误码前缀 */
    int SYSTEM_PREFIX = 0;
}
