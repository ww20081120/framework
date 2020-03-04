/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.rule.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hbasesoft.framework.rule.core.plugin.CodeMatchInterceptor;
import com.hbasesoft.framework.rule.core.plugin.ConditionInterceptor;
import com.hbasesoft.framework.rule.core.plugin.ToolsInterceptor;
import com.hbasesoft.rule.plugin.statemachine.StateMachineInterceptor;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年1月31日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.test.rule.file <br>
 */
@Configuration
public class ConfigBean {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Bean
    public CodeMatchInterceptor codeMatchInterceptor() {
        return new CodeMatchInterceptor("code", "type");
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Bean
    public ConditionInterceptor conditionInterceptor() {
        return new ConditionInterceptor();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Bean
    public ToolsInterceptor toolsInterceptor() {
        return new ToolsInterceptor();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Bean
    public StateMachineInterceptor stateMachineInterceptor() {
        return new StateMachineInterceptor();
    }
}
