package com.hbasesoft.framework.web.system.service;

import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.message.core.bean.AttachmentsPojo;

/**
 * Created by wk on 2015/10/20.
 */
public interface AttachmentService {

    AttachmentsPojo downloadResource(int var1, boolean var2) throws ServiceException;

    void saveAttachment(AttachmentsPojo var1) throws ServiceException;
}
