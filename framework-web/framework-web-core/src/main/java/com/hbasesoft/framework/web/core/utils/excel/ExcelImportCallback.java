package com.hbasesoft.framework.web.core.utils.excel;

import org.apache.poi.ss.usermodel.Row;

/**
 * Created by wk on 2015/9/17 0013.
 */
public interface ExcelImportCallback {
    void invokeDataHandler(Row row);

    void invokeInsertHandler();
}
