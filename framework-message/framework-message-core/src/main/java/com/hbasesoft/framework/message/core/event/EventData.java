/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.core.event;

import java.util.HashMap;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年2月21日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.core.event <br>
 */
public class EventData extends HashMap<String, Object> {

    private static final String DEFAULT_KEY = "__DATA";

    public EventData() {
        super();
    }

    public EventData(Object data) {
        super();
        super.put(DEFAULT_KEY, data);
    }

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 2323769185641461392L;

    public String getParameter(String key) {
        return (String) this.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getData() {
        return (T) super.get(DEFAULT_KEY);
    }

}
