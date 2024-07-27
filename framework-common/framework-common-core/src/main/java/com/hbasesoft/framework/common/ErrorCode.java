/**************************************************************************************** 
 Copyright © 2022-2027 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2023年1月12日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common <br>
 */
public interface ErrorCode {

    /**
     * Description: 获取错误码<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return code <br>
     */
    int getCode();

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return msg <br>
     */
    String getMsg();
}
