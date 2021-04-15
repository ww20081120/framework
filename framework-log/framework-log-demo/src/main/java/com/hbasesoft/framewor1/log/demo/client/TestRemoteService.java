/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framewor1.log.demo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2021年4月15日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framewor1.log.demo <br>
 */
@FeignClient(name = "http://localhost:8081", url = "http://localhost:8081")
public interface TestRemoteService {

    @GetMapping("/{param}")
    String test(@PathVariable("param") String param);
}
