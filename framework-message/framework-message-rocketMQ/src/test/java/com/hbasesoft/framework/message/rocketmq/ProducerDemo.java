package com.hbasesoft.framework.message.rocketmq;

import java.util.List;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//@RestController
public class ProducerDemo {

    /** Number */
    private static final int NUM_4 = 4;

    /** default producer */
    @Autowired
    private DefaultMQProducer defaultProducer;

    /** transaction producer */
    @Autowired
    private TransactionMQProducer transactionProducer;

    /** index */
    private int i = 0;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @RequestMapping(value = "/sendMsg", method = RequestMethod.GET)
    public void sendMsg() {
        Message msg = new Message("TopicTest1", // topic
            "TagA", // tag
            "OrderID00" + i, // key
            ("Hello zebra mq" + i).getBytes()); // body
        try {
            defaultProducer.send(msg, new SendCallback() {

                /**
                 * Description: <br>
                 * 
                 * @author 王伟<br>
                 * @taskId <br>
                 * @param sendResult <br>
                 */
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.println(sendResult);
                    // TODO 发送成功处理
                }

                /**
                 * Description: <br>
                 * 
                 * @author 王伟<br>
                 * @taskId <br>
                 * @param e <br>
                 */
                @Override
                public void onException(Throwable e) {
                    System.out.println(e);
                    // TODO 发送失败处理
                }
            });
            i++;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @RequestMapping(value = "/sendTransactionMsg", method = RequestMethod.GET)
    public String sendTransactionMsg() {
        SendResult sendResult = null;
        try {
            // 构造消息
            Message msg = new Message("TopicTest1", // topic
                "TagA", // tag
                "OrderID001", // key
                ("Hello zebra mq").getBytes()); // body

            // 发送事务消息，LocalTransactionExecute的executeLocalTransactionBranch方法中执行本地逻辑
            sendResult = transactionProducer.sendMessageInTransaction(msg, NUM_4);
            System.out.println(sendResult);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return sendResult.toString();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @RequestMapping(value = "/sendMsgOrder", method = RequestMethod.GET)
    public void sendMsgOrder() {
        Message msg = new Message("TopicTest1", // topic
            "TagA", // tag
            "OrderID00" + i, // key
            ("Hello zebra mq" + i).getBytes()); // body
        try {
            defaultProducer.send(msg, new MessageQueueSelector() {

                /**
                 * Description: <br>
                 * 
                 * @author 王伟<br>
                 * @taskId <br>
                 * @param mqs
                 * @param msg
                 * @param arg
                 * @return <br>
                 */
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    System.out.println("MessageQueue" + arg);
                    int index = ((Integer) arg) % mqs.size();
                    return mqs.get(index);
                }
            }, i); // i==arg
            i++;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
