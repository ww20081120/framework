/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.wechat.bean.openapiResp;

import com.hbasesoft.framework.wechat.bean.req.BaseMessage;

/** 
 * <Description> <br> 
 *  
 * @author zhasiwei<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年3月17日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.actsports.portal.bean.wechat.resp <br>
 */
public class VideoMessageResp extends BaseMessage {
    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 1L;
    //视频
    private Video Video;
    
    public Video getVideo() {
        return Video;
    }
    public void setVideo(Video video) {
        Video = video;
    }
    
    
    
    
}
