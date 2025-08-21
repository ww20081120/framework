package com.hbasesoft.framework.ai.jmanus.test;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.hbasesoft.framework.common.Bootstrap;

@TestConfiguration
public class GlobalConfig implements ApplicationListener<ContextRefreshedEvent>, WebMvcConfigurer {

	/**
	 *
	 */
	static {
		Bootstrap.before();
	}

	/**
	 * Description: <br>
	 *
	 * @author ww200<br>
	 * @taskId <br>
	 * @param event <br>
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext().getParent() == null) { // 确保是根上下文初始化完成
			Bootstrap.after(event.getApplicationContext());
		}
	}

}
