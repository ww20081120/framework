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
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2024年7月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.demo.entity <br>
 */
@Getter
@Setter
public class CountEntity extends BaseEntity {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 704766015383881438L;

    /** */
    private Integer total; // DB2使用INT类型，但在Java中通常使用Long以适应更大的范围

    /** */
    private String name;
}
