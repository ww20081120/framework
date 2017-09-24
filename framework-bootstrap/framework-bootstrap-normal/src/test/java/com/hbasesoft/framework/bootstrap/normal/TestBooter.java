/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.bootstrap.normal;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hbasesoft.framework.boostrap.normal.Application;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年1月7日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.test <br>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public interface TestBooter {
}
