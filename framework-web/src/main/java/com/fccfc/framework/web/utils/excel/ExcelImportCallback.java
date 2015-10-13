package com.fccfc.framework.web.utils.excel;

import org.apache.poi.ss.usermodel.Row;

/**
 * Created by 14080554 on 2015/5/7 0007.
 */
public interface ExcelImportCallback {
    void invokeDataHandler(Row row);

    void invokeInsertHandler();
}
