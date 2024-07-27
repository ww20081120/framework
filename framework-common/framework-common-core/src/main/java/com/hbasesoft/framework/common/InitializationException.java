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
     * @param code
     * @param params
     */
    public InitializationException(final ErrorCode code, final Object... params) {
        super(code, params);
    }

    /**
     * @param code
     * @param t
     */
    public InitializationException(final ErrorCode code, final Throwable t) {
        super(code, t);
    }

    /**
     * @param code
     */
    public InitializationException(final ErrorCode code) {
        super(code);
    }

    /**
     * @param t
     * @param code
     * @param params
     */
    public InitializationException(final Throwable t, final ErrorCode code, final Object... params) {
        super(t, code, params);
    }

    /**
     * @param t
     */
    public InitializationException(final Throwable t) {
        super(t);
    }

}
