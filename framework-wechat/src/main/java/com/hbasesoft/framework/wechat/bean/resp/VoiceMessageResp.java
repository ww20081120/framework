/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.wechat.bean.resp;

/** 
 * <Description> <br> 
 *  
 * @author zhasiwei<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年3月16日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.actsports.portal.bean.wechat.resp <br>
 */
public class VoiceMessageResp extends BaseMessageResp {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 1L;
    //音频
    private Voice Voice;
    
    public Voice getVoice() {
        return Voice;
    }
    public void setVoice(Voice voice) {
        Voice = voice;
    }
    
    
    
    

}
