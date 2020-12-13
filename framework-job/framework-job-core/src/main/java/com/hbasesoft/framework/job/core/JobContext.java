/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.job.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2020年12月13日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.job.core <br>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobContext {

    /** 任务名称 */
    private String jobName;

    /** 任务Id */
    private String taskId;

    /** 分片数量 */
    private int shardingTotalCount;

    /** 任务参数 */
    private String jobParameter;

    /** 分片对象 */
    private int shardingItem;

    /** 共享参数 */
    private String shardingParameter;

}
