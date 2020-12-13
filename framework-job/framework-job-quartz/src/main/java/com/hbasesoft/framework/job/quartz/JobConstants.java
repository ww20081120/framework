/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.job.quartz;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2020年12月13日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.job.quartz <br>
 */
public interface JobConstants {

    /** job实例类 */
    String JOB_INSTANCE_CLASS = "__JOB_INSTANCE_CLASS";

    /** job分片参数 */
    String JOB_SHARDING_PARAM = "__JOB_SHARDING_PARAM";

    /** job名称 */
    String JOB_NAME = "__JOB_NAME";
}
