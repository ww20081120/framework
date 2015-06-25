/**
 * 
 */
package com.fccfc.framework.message.core.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TException;

import com.fccfc.framework.common.ErrorCodeDef;
import com.fccfc.framework.common.FrameworkException;
import com.fccfc.framework.common.GlobalConstants;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.common.utils.Assert;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.common.utils.engine.VelocityParseFactory;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.message.api.Attachment;
import com.fccfc.framework.message.api.Message;
import com.fccfc.framework.message.api.MessageService;
import com.fccfc.framework.message.core.bean.AttachmentsPojo;
import com.fccfc.framework.message.core.bean.MessageBoxPojo;
import com.fccfc.framework.message.core.bean.MessageHistoryPojo;
import com.fccfc.framework.message.core.bean.MessageTemplatePojo;
import com.fccfc.framework.message.core.bean.SendRecordPojo;
import com.fccfc.framework.message.core.dao.MessageBoxDao;
import com.fccfc.framework.message.core.dao.MessageHistoryDao;
import com.fccfc.framework.message.core.dao.MessageTemplateDao;
import com.fccfc.framework.message.core.dao.SendRecordDao;
import com.fccfc.framework.message.core.service.MessageExcutor;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年12月10日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.message <br>
 */
public class MessageServiceImpl implements MessageService.Iface {

    @Resource
    private MessageBoxDao messageBoxDao;

    @Resource
    private MessageHistoryDao messageHistoryDao;

    @Resource
    private MessageTemplateDao messageTemplateDao;

    @Resource
    private SendRecordDao sendRecordDao;

    private Map<String, MessageExcutor> messageExcutorMap;

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.api.message.MessageService#sendMessage(java.lang.String,
     * com.fccfc.framework.api.bean.message.Message)
     */
    @SuppressWarnings({
        "unchecked", "rawtypes"
    })
    @Override
    public long sendMessage(String templateCode, Message message) throws TException {
        try {
            Assert.notEmpty(templateCode, "模板代码不能为空");
            Assert.notNull(message, "消息不能为空");
            MessageTemplatePojo template = messageTemplateDao.getByCode(templateCode);
            Assert.notNull(template, "模板[{0}]不存在", templateCode);
            String content = template.getTemplate();
            if (CommonUtil.isNotEmpty(content)) {
                Map param = message.getParams();
                content = VelocityParseFactory.parse(CommonUtil.getRandomChar(6), content, param);
            }

            Calendar current = Calendar.getInstance();

            MessageBoxPojo pojo = new MessageBoxPojo();
            pojo.setAttachmentsNum(CommonUtil.isEmpty(message.getAttachments()) ? 0 : message.getAttachments().size());
            pojo.setContent(content);
            pojo.setCreateTime(current.getTime());
            pojo.setMessageTemplateId(template.getMessageTemplateId());
            pojo.setMessageType(message.getMessageType());
            pojo.setExtendAttrs(message.getExtendAttrs());

            List<String> receiverList = message.getReceivers();
            Assert.notEmpty(receiverList, "接收人不能为空");
            StringBuilder receivers = new StringBuilder();
            for (String receiver : receiverList) {
                receivers.append(receiver).append(GlobalConstants.SPLITOR);
            }
            pojo.setReceivers(receivers.toString());

            pojo.setSender(message.getSender());
            pojo.setSendTimes(0);
            pojo.setSubject(message.getSubject());

            messageBoxDao.save(pojo);

            if (pojo.getAttachmentsNum() > 0) {
                for (Attachment attachment : message.getAttachments()) {
                    messageBoxDao.saveMessageAttachments(pojo.getMessageId(), attachment.getId());
                }
            }

            // 延迟发送
            if (template.getDelay() > 0) {
                current.add(Calendar.SECOND, template.getDelay());
                pojo.setNextSendTime(current.getTime());
                messageBoxDao.update(pojo);
            }
            else {
                sendMessage(template, pojo, message.getAttachments());
            }
            return pojo.getMessageId();
        }
        catch (FrameworkException e) {
            throw new TException(e);
        }

    }

    private void sendMessage(MessageTemplatePojo template, MessageBoxPojo message, List<Attachment> attachments)
        throws ServiceException {

        String[] contactChannelIds = StringUtils.split(template.getContactChannelIds(), GlobalConstants.SPLITOR);
        if (CommonUtil.isEmpty(contactChannelIds)) {
            throw new ServiceException(ErrorCodeDef.CONTACT_CHANNEL_NOT_EXIST_20016, "发送渠道不存在");
        }
        try {
            message.setSendTimes(message.getSendTimes() + 1);
            StringBuilder sb = new StringBuilder();
            for (String contactChannelId : contactChannelIds) {
                sb.append(GlobalConstants.SQL_SPLITOR);
                MessageExcutor excutor = messageExcutorMap.get(contactChannelId);
                if (excutor == null) {
                    throw new ServiceException(ErrorCodeDef.CONTACT_CHANNEL_NOT_EXIST_20016, "发送渠道[{0}]不存在",
                        contactChannelId);
                }
                SendRecordPojo record = new SendRecordPojo();
                record.setMessageId(message.getMessageId());
                record.setContactChannelId(Integer.parseInt(contactChannelId));
                try {
                    String result = excutor.sendMessage(message.getSubject(), message.getContent(),
                        message.getSender(), StringUtils.split(message.getReceivers(), GlobalConstants.SPLITOR),
                        attachments);
                    sb.append(result);
                    record.setResult(result);
                }
                catch (ServiceException e) {
                    String result = e.getMessage();
                    sb.append(result);
                    record.setResult(result);
                    throw e;
                }
                finally {
                    record.setSendTime(new Date());
                    try {
                        sendRecordDao.save(record);
                    }
                    catch (DaoException e) {
                        throw new ServiceException(e);
                    }
                }
            }

            deleteMessage(template, message, sb.toString());
        }
        catch (ServiceException e) {
            try {
                if (message.getSendTimes() >= template.getResendTimes()) {
                    deleteMessage(template, message, e.getMessage());
                }
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.SECOND, template.getDelay());
                message.setNextSendTime(calendar.getTime());
                messageBoxDao.update(message);
            }
            catch (DaoException e1) {
                throw new ServiceException(e1);
            }

            throw e;
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }

    }

    private void deleteMessage(MessageTemplatePojo template, MessageBoxPojo message, String result) throws DaoException {
        if (GlobalConstants.YES.equals(template.getSaveHistory())) {
            Calendar calendar = Calendar.getInstance();

            MessageHistoryPojo history = new MessageHistoryPojo();
            history.setAttachmentsNum(message.getAttachmentsNum());
            history.setContent(message.getContent());
            history.setCreateTime(message.getCreateTime());
            history.setMessageId(message.getMessageId());
            history.setMessageTemplateId(message.getMessageTemplateId());
            history.setMessageType(message.getMessageType());
            history.setReceivers(message.getReceivers());
            history.setResult(result);
            history.setSender(message.getSender());
            history.setSendTime(calendar.getTime());
            history.setSendTimes(message.getSendTimes());
            history.setSubject(message.getSubject());
            if (template.getSaveDay() != null && template.getSaveDay() >= 1) {
                calendar.add(Calendar.DAY_OF_YEAR, template.getSaveDay());
                history.setExpDate(calendar.getTime());
            }
            messageHistoryDao.saveHistory(history);
        }
        messageBoxDao.delete(message);
    }

    public void setMessageExcutorMap(Map<String, MessageExcutor> messageExcutorMap) {
        this.messageExcutorMap = messageExcutorMap;
    }

    /**
     * @see com.fccfc.framework.api.message.MessageService#resendMessage(int)
     */
    public void resendMessage(long messageId) throws TException {
        MessageTemplatePojo template;
        try {
            MessageBoxPojo message = messageBoxDao.getById(MessageBoxPojo.class, messageId);
            Assert.notNull(message, "消息盒子中[{0}]不存在", messageId);

            template = messageTemplateDao.getById(MessageTemplatePojo.class, message.getMessageTemplateId());
            Assert.notNull(template, "重发模板[{0}]不存在", message.getMessageTemplateId());
            List<Attachment> attachments = null;
            if (message.getAttachmentsNum() > 0) {
                List<AttachmentsPojo> attachmentList = messageBoxDao.selectMessageAttachments(message.getMessageId());
                if (CommonUtil.isNotEmpty(attachmentList)) {
                    attachments = new ArrayList<Attachment>();
                    Attachment attachment = null;
                    for (AttachmentsPojo pojo : attachmentList) {
                        attachment = new Attachment();
                        attachment.setFileSize(pojo.getFileSize());
                        attachment.setId(pojo.getAttachmentsId());
                        attachment.setName(pojo.getAttachmentsName());
                        attachment.setType(pojo.getAttachmentsType());
                        attachment.setUrl(pojo.getFilePath());
                        attachments.add(attachment);
                    }
                }
            }

            sendMessage(template, message, attachments);
        }
        catch (FrameworkException e) {
            throw new TException(e);
        }
    }
}
