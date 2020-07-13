/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.file;

import org.springframework.stereotype.Component;

import com.hbasesoft.framework.common.annotation.NoTransLog;
import com.hbasesoft.framework.common.utils.logger.Logger;

import zipkin2.Span;
import zipkin2.reporter.Reporter;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Jul 13, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.log.file <br>
 */
@Component("zipkinReporter")
@NoTransLog
public class ZipkinSpanReporter implements Reporter<Span> {

    /**
     * logger
     */
    private Logger logger = new Logger(TransLoggerService4File.class);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param span <br>
     */
    @Override
    public void report(Span span) {
        if (span != null) {
            logger.info(span.toString());
        }
    }

}
