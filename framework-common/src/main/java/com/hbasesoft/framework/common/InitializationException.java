/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月24日 <br>
 * @see com.hbasesoft.framework.common <br>
 */
public class InitializationException extends FrameworkException {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 3036926828687373135L;

    /**
     * @param exception
     */
    public InitializationException(FrameworkException exception) {
        super(exception);
    }

    /**
     * @param code
     * @param params
     */
    public InitializationException(int code, Object... params) {
        super(code, params);
    }

    /**
     * @param code
     * @param t
     */
    public InitializationException(int code, Throwable t) {
        super(code, t);
    }

    /**
     * @param code
     */
    public InitializationException(int code) {
        super(code);
    }

    /**
     * @param t
     * @param code
     * @param params
     */
    public InitializationException(Throwable t, int code, Object... params) {
        super(t, code, params);
    }

    /**
     * @param t
     */
    public InitializationException(Throwable t) {
        super(t);
    }

}
