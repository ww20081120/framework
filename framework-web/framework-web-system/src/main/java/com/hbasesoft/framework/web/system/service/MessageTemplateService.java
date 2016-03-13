package com.hbasesoft.framework.web.system.service;

import java.util.List;

import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.message.core.bean.MessageTemplatePojo;

/**
 * <Description> <br>
 * 
 * @author shao.dinghui<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月27日 <br>
 * @since bps<br>
 * @see com.hbasesoft.framework.web.manager.service.template <br>
 */
public interface MessageTemplateService {

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param pageIndex <br>
     * @param pageSize <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    List<MessageTemplatePojo> queryAllMessageTemplate(int pageIndex, int pageSize) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param pojo <br>
     * @throws ServiceException <br>
     */
    void addMessageTemplateService(MessageTemplatePojo pojo) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param pojo <br>
     * @throws ServiceException <br>
     */
    void modifyMessageTemplate(MessageTemplatePojo pojo) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param messageTemplateId <br>
     * @throws ServiceException <br>
     */
    void deleteMessageTemplate(Integer messageTemplateId) throws ServiceException;

    /**
     * Description: 导入<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param mediaId <br>
     * @param mediaName <br>
     * @throws ServiceException <br>
     */
    void importMessageTemplateData(String mediaId, String mediaName) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param messageTemplateId <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    MessageTemplatePojo queryMessageTemplateById(Integer messageTemplateId) throws ServiceException;

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    List<MessageTemplatePojo> queryAllMt() throws ServiceException;

}
