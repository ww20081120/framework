/**
 * 
 */
package com.fccfc.framework.web.service;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.web.bean.resource.AttachmentsPojo;

/**
 * <Description> <br>
 * 
 * @author 伟<br>
 * @version 1.0<br>
 * @CreateDate 2014-11-30 <br>
 * @see com.fccfc.framework.web.service <br>
 */
public interface ResourceService {

    /**
     * 下载文件
     * 
     * @param resourceId
     * @return
     * @throws ServiceException
     */
    AttachmentsPojo downloadResource(int resourceId, boolean isThumb) throws ServiceException;

    void saveAttachment(AttachmentsPojo attachments) throws ServiceException;
}
