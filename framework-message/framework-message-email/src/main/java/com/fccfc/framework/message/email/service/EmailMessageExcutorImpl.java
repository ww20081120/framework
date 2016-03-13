/**
 * 
 */
package com.hbasesoft.framework.message.email.service;

import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.stereotype.Service;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.config.core.ConfigHelper;
import com.hbasesoft.framework.message.api.Attachment;
import com.hbasesoft.framework.message.core.service.MessageExcutor;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年12月10日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.service.email <br>
 */
@Service
public class EmailMessageExcutorImpl implements MessageExcutor {

    private static final String CHANNEL_ID = "EMAIL";

    /*
     * (non-Javadoc)
     * @see com.hbasesoft.framework.message.service.AbstractMessageService#sendMessage(java.lang.String, java.lang.String,
     * java.lang.String, java.util.List, java.util.List)
     */
    @Override
    public String sendMessage(String title, String content, String sender, String[] receiver,
        List<Attachment> attachments) throws ServiceException {
        String host = ConfigHelper.getString("EMAIL.EMAIL_HOST");
        String username = ConfigHelper.getString("EMAIL.EMAIL_USERNAME");
        String password = ConfigHelper.getString("EMAIL.EMAIL_PASSWORD");

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(props);
        session.setDebug(true);
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(sender));
            message.setSubject(title);

            Multipart multipart = new MimeMultipart();
            BodyPart contentPart = new MimeBodyPart();
            contentPart.setText(content);
            multipart.addBodyPart(contentPart);

            // 添加附件
            // BodyPart messageBodyPart = new MimeBodyPart();
            // DataSource source = new FileDataSource(affix);
            // 添加附件的内容
            // messageBodyPart.setDataHandler(new DataHandler(source));
            // 添加附件的标题
            // 这里很重要，通过下面的Base64编码的转换可以保证你的中文附件标题名在发送时不会变成乱码
            // sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
            // messageBodyPart.setFileName("=?GBK?B?"+ enc.encode(affixName.getBytes()) + "?=");
            // multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);
            message.saveChanges();

            Transport transport = session.getTransport();
            transport.connect(host, username, password);
            transport.sendMessage(message, getToAddress(receiver));
            transport.close();
            return "SUCCESS";
        }
        catch (Exception e) {
            throw new ServiceException(ErrorCodeDef.EMAIL_SEND_ERROR_10032, e.getMessage(), e);
        }
    }

    /**
     * 组装收件人地址
     * 
     * @param receiver 收件人地址
     * @return 收件人地址
     * @throws AddressException 异常
     */
    private Address[] getToAddress(String[] receiver) throws AddressException {
        Address[] toAddress = null;

        if (CommonUtil.isNotEmpty(receiver)) {
            toAddress = new Address[receiver.length];

            for (int i = 0; i < receiver.length; i++) {
                toAddress[i] = new InternetAddress(receiver[i]);
            }
        }

        return toAddress;

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String getChannelId() {
        return CHANNEL_ID;
    }
}
