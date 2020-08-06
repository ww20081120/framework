/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.demo;

import org.springframework.stereotype.Service;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Jul 13, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.log.demo <br>
 */
@Service
public class DemoServiceImpl implements DemoService {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String say(String name) {
        return name + ": hello!";
    }

}
