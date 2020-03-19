package com.hbasesoft.framework.message.rocketmq;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.context.ApplicationEvent;

/**
 * <Description> <br>
 * 
 * @author 大刘杰<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年6月25日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.rocketmq <br>
 */
public class RocketmqEvent extends ApplicationEvent {
    private static final long serialVersionUID = -4468405250074063206L;

    /** consumer */
    private DefaultMQPushConsumer consumer;

    /** msgs */
    private List<MessageExt> msgs;

    /**
     * @param msgs
     * @param consumer
     * @throws Exception
     */
    public RocketmqEvent(final List<MessageExt> msgs, final DefaultMQPushConsumer consumer) throws Exception {
        super(msgs);
        this.consumer = consumer;
        this.setMsgs(msgs);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param idx
     * @return <br>
     */
    public String getMsg(final int idx) {
        try {
            return new String(getMsgs().get(idx).getBody(), "utf-8");
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param idx
     * @param code
     * @return <br>
     */
    public String getMsg(final int idx, final String code) {
        try {
            return new String(getMsgs().get(idx).getBody(), code);
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public DefaultMQPushConsumer getConsumer() {
        return consumer;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param consumer <br>
     */
    public void setConsumer(final DefaultMQPushConsumer consumer) {
        this.consumer = consumer;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param idx
     * @return <br>
     */
    public MessageExt getMessageExt(final int idx) {
        return getMsgs().get(idx);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param idx
     * @return <br>
     */
    public String getTopic(final int idx) {
        return getMsgs().get(idx).getTopic();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param idx
     * @return <br>
     */
    public String getTag(final int idx) {
        return getMsgs().get(idx).getTags();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param idx
     * @return <br>
     */
    public byte[] getBody(final int idx) {
        return getMsgs().get(idx).getBody();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param idx
     * @return <br>
     */
    public String getKeys(final int idx) {
        return getMsgs().get(idx).getKeys();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public List<MessageExt> getMsgs() {
        return msgs;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param msgs <br>
     */
    public void setMsgs(final List<MessageExt> msgs) {
        this.msgs = msgs;
    }
}
