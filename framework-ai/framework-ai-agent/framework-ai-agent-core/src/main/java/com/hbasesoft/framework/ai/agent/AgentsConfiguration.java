/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Agents 框架的配置类<br>
 * 该类负责导入和配置 Agents 框架所需的所有组件。
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月31日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.config <br>
 */
@Configuration
@Import(AgentsRegistrar.class)
public class AgentsConfiguration {

}
