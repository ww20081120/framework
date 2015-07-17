/**
 * 
 */
package com.fccfc.framework.web.service;

import java.util.List;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.message.core.bean.AttachmentsPojo;
import com.fccfc.framework.web.bean.resource.UrlResourcePojo;

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
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param resourceId <br>
     * @param isThumb <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    AttachmentsPojo downloadResource(int resourceId, boolean isThumb) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param attachments <br>
     * @throws ServiceException <br>
     */
    void saveAttachment(AttachmentsPojo attachments) throws ServiceException;

    /***
     * Description: <br>
     * 
     * @author bai.wenlong<br>
     * @taskId <br>
     * @return List<UrlResourcePojo> <br>
     * @throws ServiceException <br>
     */
    List<UrlResourcePojo> selectResource() throws ServiceException;

}
