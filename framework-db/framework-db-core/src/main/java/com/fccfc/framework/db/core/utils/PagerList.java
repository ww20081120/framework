/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.db.core.utils;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月17日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.db.core.utils <br>
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
     * 
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
            return totalCount > pageIndex * (pageSize + 1);
        }
    }

    /**
     * 
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
            //return (int) (totalCount / pageSize + 1);
            return (int) (totalCount % pageSize == 0 ? (totalCount / pageSize) : (totalCount / pageSize + 1));
        }
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

}
