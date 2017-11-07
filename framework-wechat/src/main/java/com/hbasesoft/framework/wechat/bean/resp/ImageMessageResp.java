/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.wechat.bean.resp;

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
public class ImageMessageResp extends BaseMessage {
    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 1L;
    //图像
    private Image Image;
    
    public Image getImage() {
        return Image;
    }
    public void setImage(Image image) {
        Image = image;
    }

    
    
}
