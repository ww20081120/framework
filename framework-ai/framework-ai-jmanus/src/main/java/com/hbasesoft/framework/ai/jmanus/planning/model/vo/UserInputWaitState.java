/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.planning.model.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserInputWaitState 类用于表示用户输入等待状态。 该类包含了计划 ID、提示消息、是否处于等待状态、表单描述以及表单输入项等信息。 实现 Serializable
 * 接口意味着该类的对象可以被序列化，以便在网络传输或持久化存储时使用。
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年7月1日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.planning.model.vo <br>
 */
@Data
@NoArgsConstructor
public class UserInputWaitState implements Serializable {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 1839244199408128722L;

    /**
     * 关联的计划 ID，用于唯一标识用户输入等待状态所属的计划。
     */
    private String planId;

    /**
     * 提示用户输入的消息，向用户说明需要输入的内容或操作。
     */
    private String message;

    /**
     * 表示是否处于等待用户输入的状态。 true 表示正在等待用户输入，false 表示不需要等待。
     */
    private boolean waiting;

    /**
     * 表单描述信息，用于解释表单的用途和填写要求。
     */
    private String formDescription;

    /**
     * 表单输入项列表，每个输入项使用 Map 存储，键值对表示输入项的属性和对应的值。
     */
    private List<Map<String, String>> formInputs;

    public UserInputWaitState(String planId, String message, boolean waiting) {
        this.planId = planId;
        this.message = message;
        this.waiting = waiting;
    }
}
