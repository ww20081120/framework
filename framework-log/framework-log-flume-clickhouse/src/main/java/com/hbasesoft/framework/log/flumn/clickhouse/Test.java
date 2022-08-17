/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.flumn.clickhouse;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Aug 7, 2022 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.log.flumn.clickhouse <br>
 */
public class Test {

    public static void main(String[] args) {
        org.apache.flume.node.Application.main(new String[] {
            "agent", "--name", "a1", "--conf-file", "/Users/wangwei/Downloads/apache-flume-1.10.0-bin/job/flume-nc-log.conf"
        });
    }
}
