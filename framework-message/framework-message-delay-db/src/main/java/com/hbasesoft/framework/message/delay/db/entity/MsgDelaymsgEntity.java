/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or     <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/

package com.hbasesoft.framework.message.delay.db.entity;

import java.net.InetAddress;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.common.utils.security.DataUtil;
import com.hbasesoft.framework.db.core.BaseEntity;
import com.hbasesoft.framework.message.core.delay.DelayMessage;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * <Description> T_MSG_DELAYMSG的Entity<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2019年04月10日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.BaseEntity.bean.BaseEntity <br>
 */
@Entity(name = "T_MSG_DELAYMSG")
public class MsgDelaymsgEntity extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** id */
    @Id
    @Column(name = "id")
    private String id;

    /** channel */
    @Column(name = "channel")
    private String channel;

    /** create_time */
    @Column(name = "create_time")
    private java.util.Date createTime;

    /** delay_seconds */
    @Column(name = "delay_seconds")
    private Integer delaySeconds;

    /** memery_flag */
    @Column(name = "memery_flag")
    private String memeryFlag;

    /** content */
    @Column(name = "content")
    private String content;

    /** expire_time */
    @Column(name = "expire_time")
    private java.util.Date expireTime;

    /** shard_info */
    @Column(name = "shard_info")
    private String shardInfo;

    /**
     * 
     */
    public MsgDelaymsgEntity() {
    }

    /**
     * @param delayMessage
     */
    public MsgDelaymsgEntity(final DelayMessage delayMessage) {
        this.setId(delayMessage.getMessageId());
        this.setChannel(delayMessage.getChannel());
        if (delayMessage.getData() != null) {
            this.setContent(DataUtil.byte2HexStr(delayMessage.getData()));
        }
        this.setCreateTime(new Date(delayMessage.getCurrentTime()));
        this.setDelaySeconds(delayMessage.getSeconds());
        this.setMemeryFlag("N");
        this.setShardInfo(getShardMsg());
        this.expireTime = new Date(delayMessage.getCurrentTime() + this.getDelaySeconds() * GlobalConstants.SECONDS);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public DelayMessage toVo() {
        return new DelayMessage(this.id, this.channel,
            StringUtils.isNotEmpty(content) ? DataUtil.hexStr2Byte(content) : null, this.delaySeconds,
            this.createTime.getTime());
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public static String getShardMsg() {
        String shardInfo = PropertyHolder.getProjectName();
        try {
            shardInfo += PropertyHolder.getProperty("eureka.instance.hostname",
                InetAddress.getLocalHost().getHostAddress());
        }
        catch (Exception e) {
            LoggerUtil.error(e);
        }
        return shardInfo;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public String getId() {
        return this.id;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id <br>
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public String getChannel() {
        return this.channel;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param channel <br>
     */
    public void setChannel(final String channel) {
        this.channel = channel;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public java.util.Date getCreateTime() {
        return this.createTime;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param createTime <br>
     */
    public void setCreateTime(final java.util.Date createTime) {
        this.createTime = createTime;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public Integer getDelaySeconds() {
        return this.delaySeconds;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param delaySeconds <br>
     */
    public void setDelaySeconds(final Integer delaySeconds) {
        this.delaySeconds = delaySeconds;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public String getMemeryFlag() {
        return this.memeryFlag;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param memeryFlag <br>
     */
    public void setMemeryFlag(final String memeryFlag) {
        this.memeryFlag = memeryFlag;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param content <br>
     */
    public void setContent(final String content) {
        this.content = content;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public java.util.Date getExpireTime() {
        return this.expireTime;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param expireTime <br>
     */
    public void setExpireTime(final java.util.Date expireTime) {
        this.expireTime = expireTime;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public String getShardInfo() {
        return shardInfo;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param shardInfo <br>
     */
    public void setShardInfo(final String shardInfo) {
        this.shardInfo = shardInfo;
    }

}
