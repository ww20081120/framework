/**
 *
 */
package com.hbasesoft.framework.web.core.service.common;

import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.message.core.bean.AttachmentsPojo;

/**
 * <Description> <br>
 *
 * @author 伟<br>
 * @version 1.0<br>
 * @CreateDate 2014-11-30 <br>
 * @see com.hbasesoft.framework.web.manager.service.common <br>
 */
public interface ResourceService {

    /**
     * Description: <br>
     *
     * @param resourceId <br>
     * @param isThumb <br>
     * @return <br>
     * @throws ServiceException <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    AttachmentsPojo downloadResource(int resourceId, boolean isThumb) throws ServiceException;

    /**
     * Description: <br>
     *
     * @param attachments <br>
     * @throws ServiceException <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    void saveAttachment(AttachmentsPojo attachments) throws ServiceException;

    /**
     * queryResourceByfilePath Description: <br>
     *
     * @param filePath filePath
     * @return AttachmentsPojo
     * @throws ServiceException <br>
     * @author XXX<br>
     * @taskId <br>
     */
    AttachmentsPojo queryResourceByfilePath(String filePath) throws ServiceException;
}
