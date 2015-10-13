package com.fccfc.framework.web.utils.excel;

import java.util.List;

/**
 * Created by 14080554 on 2015/7/8 0008.
 */
public interface GetDataCallback {
    List invoke(int currentPage, int pageSize);
}
