/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.demo.entity;

import com.hbasesoft.framework.db.core.BaseEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * <Description> <br>
 * 
 * @author ww200<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2024年7月7日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.demo.entity <br>
 */
@Getter
@Setter
public class Count extends BaseEntity {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = -3115703439314376932L;

    /** 总数 */
    private Integer total;

    /** 名称 */
    private String name;
}
