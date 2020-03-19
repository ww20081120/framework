/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.server;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月17日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.core.utils <br>
 * @param <E> <br>
 */
public class PagerList<E> extends CopyOnWriteArrayList<E> {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 5591930350150260403L;

    /**
     * 页数
     */
    private int pageIndex = -1;

    /**
     * 每页数量
     */
    private int pageSize = -1;

    /**
     * 总数
     */
    private long totalCount = 0;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    public boolean hasNextPage() {
        if (totalCount <= 0 || pageIndex <= 0 || pageSize <= 0) {
            return false;
        }
        else {
            return totalCount > pageIndex * pageSize;
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    public int getTotalPage() {
        if (totalCount < 0 || pageSize < 1) {
            return 0;
        }
        else {
            // return (int) (totalCount / pageSize + 1);
            return (int) (totalCount % pageSize == 0 ? (totalCount / pageSize) : (totalCount / pageSize + 1));
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public int getPageIndex() {
        return pageIndex;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param pageIndex <br>
     */
    public void setPageIndex(final int pageIndex) {
        this.pageIndex = pageIndex;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param pageSize <br>
     */
    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public long getTotalCount() {
        return totalCount;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param totalCount <br>
     */
    public void setTotalCount(final long totalCount) {
        this.totalCount = totalCount;
    }

}
