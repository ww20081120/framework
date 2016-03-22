/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core;

import com.alibaba.druid.pool.DruidDataSource;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.security.DataUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年12月11日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.core <br>
 */
public class DataSource extends DruidDataSource {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = -4882512944369995514L;

    public void setPassword(String password) {
        if (CommonUtil.isNotEmpty(password) && password.startsWith("ENC(") && password.endsWith(")")) {
            password = DataUtil.decrypt(password.substring(4, password.length() - 1));
        }
        super.setPassword(password);
    }
}
