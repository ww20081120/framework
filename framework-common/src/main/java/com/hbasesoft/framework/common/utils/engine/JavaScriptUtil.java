/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.engine;

import java.util.Map;
import java.util.Map.Entry;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.UtilException;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年10月23日 <br>
 * @see com.hbasesoft.framework.core.utils <br>
 */
public final class JavaScriptUtil {

    /**
     * 默认构造器
     */
    private JavaScriptUtil() {
    }

    /**
     * Description: 执行脚本<br>
     * 
     * @author 王伟 <br>
     * @param script script
     * @param params params
     * @return 脚本返回值
     * @throws UtilException <br>
     */
    public static Object eval(String script, Map<String, Object> params) throws UtilException {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("javascript");
        try {
            ScriptContext context = new SimpleScriptContext();
            if (params != null) {
                for (Entry<String, Object> entry : params.entrySet()) {
                    context.setAttribute(entry.getKey(), entry.getValue(), ScriptContext.ENGINE_SCOPE);
                }
            }
            return engine.eval(script, context);
        }
        catch (ScriptException e) {
            throw new UtilException(ErrorCodeDef.EVAL_JAVASCRIPT_ERROR_10042, e);
        }
    }
}
