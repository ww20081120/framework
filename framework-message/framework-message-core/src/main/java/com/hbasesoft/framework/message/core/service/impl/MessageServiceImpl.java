/**
 * 
 */
package com.hbasesoft.framework.message.core.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TException;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.engine.VelocityParseFactory;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.message.api.Attachment;
import com.hbasesoft.framework.message.api.Message;
import com.hbasesoft.framework.message.api.MessageService;
import com.hbasesoft.framework.message.core.bean.AttachmentsPojo;
import com.hbasesoft.framework.message.core.bean.MessageBoxPojo;
import com.hbasesoft.framework.message.core.bean.MessageHistoryPojo;
import com.hbasesoft.framework.message.core.bean.MessageTemplatePojo;
import com.hbasesoft.framework.message.core.bean.SendRecordPojo;
import com.hbasesoft.framework.message.core.dao.MessageBoxDao;
import com.hbasesoft.framework.message.core.dao.MessageHistoryDao;
import com.hbasesoft.framework.message.core.dao.MessageTemplateDao;
import com.hbasesoft.framework.message.core.dao.SendRecordDao;
import com.hbasesoft.framework.message.core.service.MessageExcutor;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年12月10日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message <br>
 */
public class MessageServiceImpl implements MessageService.Iface {

    /**
     * messageBoxDao
     */
    @Resource
    private MessageBoxDao messageBoxDao;

    /**
     * messageHistoryDao
     */
    @Resource
    private MessageHistoryDao messageHistoryDao;

    /**
     * messageTemplateDao
     */
    @Resource
    private MessageTemplateDao messageTemplateDao;

    /**
     * sendRecordDao
     */
    @Resource
    private SendRecordDao sendRecordDao;

    /**
     * messageExcutorMap
     */
    private Map<String, MessageExcutor> messageExcutorMap;

    /*
     * (non-Javadoc)
     * @see com.hbasesoft.framework.api.message.MessageService#sendMessage(java.lang.String,
     * com.hbasesoft.framework.api.bean.message.Message)
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
                messageBoxDao.updateEntity(pojo);
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

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param template <br>
     * @param message <br>
     * @param attachments <br>
     * @throws ServiceException <br>
     */
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
                MessageExcutor excutor = getMessageExcutorMap().get(contactChannelId);
                if (excutor == null) {
                    throw new ServiceException(ErrorCodeDef.CONTACT_CHANNEL_NOT_EXIST_20016, "发送渠道[{0}]不存在",
                        contactChannelId);
                }
                SendRecordPojo record = new SendRecordPojo();
                record.setMessageId(message.getMessageId());
                record.setContactChannelId(contactChannelId);
                try {
                    String result = excutor.sendMessage(message.getSubject(), message.getContent(), message.getSender(),
                        StringUtils.split(message.getReceivers(), GlobalConstants.SPLITOR), attachments);
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
                messageBoxDao.updateEntity(message);
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

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param template <br>
     * @param message <br>
     * @param result <br>
     * @throws DaoException <br>
     */
    private void deleteMessage(MessageTemplatePojo template, MessageBoxPojo message, String result)
        throws DaoException {
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

    /**
     * resendMessage
     * 
     * @see com.hbasesoft.framework.api.message.MessageService#resendMessage(int)
     * @param messageId <br>
     * @throws TException <br>
     */
    public void resendMessage(long messageId) throws TException {
        MessageTemplatePojo template;
        try {
            MessageBoxPojo message = messageBoxDao.get(MessageBoxPojo.class, messageId);
            Assert.notNull(message, "消息盒子中[{0}]不存在", messageId);

            template = messageTemplateDao.get(MessageTemplatePojo.class, message.getMessageTemplateId());
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

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return messageExcutorMap <br>
     */
    public Map<String, MessageExcutor> getMessageExcutorMap() {
        if (messageExcutorMap == null) {
            ServiceLoader<MessageExcutor> serviceLoader = ServiceLoader.load(MessageExcutor.class);
            messageExcutorMap = new HashMap<String, MessageExcutor>();
            for (MessageExcutor excutor : serviceLoader) {
                messageExcutorMap.put(excutor.getChannelId(), excutor);
            }
        }
        return messageExcutorMap;
    }

}
