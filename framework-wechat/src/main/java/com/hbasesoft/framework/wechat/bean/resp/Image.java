/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.wechat.bean.resp;

import com.hbasesoft.framework.db.core.BaseEntity;

/** 
 * <Description> <br> 
 *  
 * @author ruiluhui<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年3月24日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.actsports.portal.bean.wechat.resp <br>
 */
public class Image extends BaseEntity {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 1L;
    
    
    private String MediaId;


    public String getMediaId() {
        return MediaId;
    }


    public void setMediaId(String mediaId) {
        MediaId = mediaId;
    }
    
    

}
