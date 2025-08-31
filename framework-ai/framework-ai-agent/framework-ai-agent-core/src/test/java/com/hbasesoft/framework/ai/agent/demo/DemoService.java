/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.demo;

import org.springframework.stereotype.Service;

/**
 * 演示服务类<br>
 * 用于演示 bean 名称冲突解决。
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月31日 <br>
 * @since V1.0<br>
 */
@Service
public class DemoService {
    public String getName() {
        return "DemoService from demo package";
    }
}
