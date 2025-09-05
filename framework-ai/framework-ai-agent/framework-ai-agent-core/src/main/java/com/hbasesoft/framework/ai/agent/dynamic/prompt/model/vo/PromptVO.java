/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.dynamic.prompt.model.vo;

import org.springframework.ai.chat.messages.MessageType;

import com.hbasesoft.framework.ai.agent.dynamic.prompt.model.enums.PromptType;

import lombok.Data;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.prompt.model.vo <br>
 */
@Data
public class PromptVO {

    private Long id;

    private String promptName;

    private String namespace;

    private String messageType;

    private String type;

    private String promptContent;

    private Boolean builtIn;

    private String promptDescription;

    public Boolean invalid() {
        return promptName == null || messageType == null || type == null || promptContent == null
            || promptDescription == null || builtIn == null || !isValidEnumValue(type, PromptType.class)
            || !isValidEnumValue(messageType, MessageType.class);
    }

    private static <E extends Enum<E>> boolean isValidEnumValue(String value, Class<E> enumClass) {
        if (value == null) {
            return false;
        }
        try {
            Enum.valueOf(enumClass, value);
            return true;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }
}
