/**
 * 
 */
package com.fccfc.framework.message.core.service.email;

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

import com.fccfc.framework.common.ErrorCodeDef;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.config.core.Configuration;
import com.fccfc.framework.message.api.Attachment;
import com.fccfc.framework.message.core.service.MessageExcutor;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年12月10日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.message.service.email <br>
 */
@Service
public class EmailMessageExcutorImpl implements MessageExcutor {

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.message.service.AbstractMessageService#sendMessage(java.lang.String, java.lang.String,
     * java.lang.String, java.util.List, java.util.List)
     */
    @Override
    public String sendMessage(String title, String content, String sender, String[] receiver,
        List<Attachment> attachments) throws ServiceException {
        String host = Configuration.getString("EMAIL.EMAIL_HOST");
        String username = Configuration.getString("EMAIL.EMAIL_USERNAME");
        String password = Configuration.getString("EMAIL.EMAIL_PASSWORD");

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
}
