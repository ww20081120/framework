/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.server.retry;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Jan 21, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tx.server.retry <br>
 */
public interface RetryService {

    String getType();

    void retryMsg(String id, String serverInfo);
}
