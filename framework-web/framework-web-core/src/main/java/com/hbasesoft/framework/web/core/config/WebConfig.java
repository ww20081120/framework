/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.web.core.config;

import org.springframework.boot.autoconfigure.velocity.VelocityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.velocity.VelocityLayoutViewResolver;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年3月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.web.core.config <br>
 */
@Configuration
public class WebConfig {

    @Bean(name = "velocityViewResolver")
    public VelocityLayoutViewResolver velocityViewResolver(VelocityProperties properties) {
        VelocityLayoutViewResolver resolver = new VelocityLayoutViewResolver();
        properties.applyToViewResolver(resolver);
        resolver.setLayoutUrl("layout/default.html");
        return resolver;
    }
}
