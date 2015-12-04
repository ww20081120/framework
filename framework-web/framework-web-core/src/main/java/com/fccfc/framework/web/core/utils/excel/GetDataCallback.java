package com.fccfc.framework.web.core.utils.excel;

import java.util.List;

/**
 * Created by wk on 2015/9/17 0013.
 */
public interface GetDataCallback {
    List invoke(int currentPage, int pageSize);
}
