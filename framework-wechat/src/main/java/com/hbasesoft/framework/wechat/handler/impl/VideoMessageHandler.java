package com.hbasesoft.framework.wechat.handler.impl;

import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.wechat.bean.AccountPojo;
import com.hbasesoft.framework.wechat.handler.WechatMessageHandler;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by renrui on 2017/8/18.
 */
@Service("videoMessageHandler")
public class VideoMessageHandler implements WechatMessageHandler {
    @Override
    public String process(String msgId, String toUserName, AccountPojo entity, String content,
                          Map<String, String> requestMap, String imagePath, String serverPath, String message) throws ServiceException {
        return null;
    }

    @Override
    public void asynProcess(String msgId, String toUserName, AccountPojo entity, String content,
                            Map<String, String> requestMap, String imagePath, String serverPath, String message) throws FrameworkException {
    }
}
