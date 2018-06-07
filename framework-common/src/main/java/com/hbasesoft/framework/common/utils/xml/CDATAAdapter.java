/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.lang3.StringUtils;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年6月7日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.utils.xml <br>
 */
public class CDATAAdapter extends XmlAdapter<String, String> {

    private static final String CDATA_END = "]]>";

    private static final String CDATA_BEGIN = "<![CDATA[";

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param v
     * @return
     * @throws Exception <br>
     */
    @Override
    public String unmarshal(String v) throws Exception {
        return StringUtils.isNotEmpty(v) && v.startsWith(CDATA_BEGIN) && v.endsWith(CDATA_END)
            ? v.substring(CDATA_BEGIN.length(), v.length() - CDATA_END.length())
            : v;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param v
     * @return
     * @throws Exception <br>
     */
    @Override
    public String marshal(String v) throws Exception {
        return CDATA_BEGIN + v + CDATA_END;
    }

}
