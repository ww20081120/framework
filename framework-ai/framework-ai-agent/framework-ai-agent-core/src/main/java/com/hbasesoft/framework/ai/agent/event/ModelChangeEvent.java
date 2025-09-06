/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.event;

import com.hbasesoft.framework.ai.agent.dynamic.model.model.vo.ModelConfig;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.event <br>
 */
public class ModelChangeEvent implements JmanusEvent {

    private ModelConfig dynamicModelEntity;

    private long createTime;

    public ModelChangeEvent(ModelConfig dynamicModelEntity) {
        this.dynamicModelEntity = dynamicModelEntity;
        this.createTime = System.currentTimeMillis();
    }

    public ModelConfig getDynamicModelEntity() {
        return dynamicModelEntity;
    }

    public long getCreateTime() {
        return createTime;
    }

}