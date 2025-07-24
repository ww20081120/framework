/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.demo.nl2sql;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.cloud.ai.dbconnector.DbConfig;
import com.alibaba.cloud.ai.request.SchemaInitRequest;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年7月7日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.demo.nl2sql <br>
 */
@RestController
public class SimpleChatController {
    
    @Autowired
    private DbConfig dbConfig;

    @PostMapping("/simpleChat")
    public String simpleNl2Sql(@RequestBody String input) throws Exception {
        SchemaInitRequest schemaInitRequest = new SchemaInitRequest();
        schemaInitRequest.setTables(Arrays.asList("categories","order_items","orders","products","users","product_categories"));

        return null;
    }
}
