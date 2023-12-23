package com.hbasesoft.framework.db.core.annotation.handler;

/**
 * <Description> <br>
 * 增强切换数据源的方式 实现接口进行自定义切换
 * 
 * @author xz <br>
 * @version 1.0 <br>
 * @CreateDate 2020年06月29日 <br>
 * @see com.hbasesoft.framework <br>
 */
public interface EnhanceDynamicDataSourceHandler {

    /**
     * Description: 实现接口进行自定义切换<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param dbCode
     * @return <br>
     */
    String enhance(String dbCode);
}
