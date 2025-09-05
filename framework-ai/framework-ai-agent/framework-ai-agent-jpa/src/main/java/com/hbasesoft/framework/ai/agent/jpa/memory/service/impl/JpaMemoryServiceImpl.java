/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.jpa.memory.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage.ToolResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSONArray;
import com.hbasesoft.framework.ai.agent.jpa.memory.dao.MessageDao;
import com.hbasesoft.framework.ai.agent.jpa.memory.po.MessagePo4Jpa;
import com.hbasesoft.framework.common.utils.date.DateUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月31日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.jpa.memory.service.impl <br>
 */
@Service
public class JpaMemoryServiceImpl implements ChatMemoryRepository {

    @Autowired
    private MessageDao messageDao;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Transactional(readOnly = true)
    @Override
    public @NonNull List<String> findConversationIds() {
        return messageDao.findConversationIds();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param conversationId
     * @return <br>
     */
    @Transactional(readOnly = true)
    @Override
    public @NonNull List<Message> findByConversationId(final @NonNull String conversationId) {
        return messageDao
            .queryByLambda(
                q -> q.eq(MessagePo4Jpa::getConversationId, conversationId).orderByDesc(MessagePo4Jpa::getId))
            .stream().map(this::convert).toList();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param conversationId
     * @param messages <br>
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveAll(final @NonNull String conversationId, final @NonNull List<Message> messages) {
        List<MessagePo4Jpa> pos = messages.stream().map(m -> convert(conversationId, m)).toList();
        messageDao.saveBatch(pos);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param conversationId <br>
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByConversationId(final @NonNull String conversationId) {
        messageDao.deleteByLambda(q -> q.eq(MessagePo4Jpa::getConversationId, conversationId));
    }

    private MessagePo4Jpa convert(final String conversationId, final Message message) {
        var po = new MessagePo4Jpa();
        po.setConversationId(conversationId);
        po.setContent(message.getText());
        po.setType(message.getMessageType().name());
        po.setCreateTime(DateUtil.getCurrentDate());
        return po;
    }

    private Message convert(final MessagePo4Jpa po) {
        var content = po.getContent();
        var type = MessageType.valueOf(po.getType());

        return switch (type) {
            case USER -> new UserMessage(content);
            case ASSISTANT -> new AssistantMessage(content);
            case SYSTEM -> new SystemMessage(content);
            case TOOL -> new ToolResponseMessage(
                StringUtils.isNotEmpty(content) ? (JSONArray.parseArray(content, ToolResponse.class)) : List.of());
        };
    }
}
