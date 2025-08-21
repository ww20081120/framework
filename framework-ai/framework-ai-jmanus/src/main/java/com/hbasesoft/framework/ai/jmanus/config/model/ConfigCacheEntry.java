/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.config.model;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月21日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.config.model <br>
 */

public class ConfigCacheEntry<T> {

	private T value;

	private long lastUpdateTime;

	private static final long EXPIRATION_TIME = 30000; // 30 seconds expiration

	public ConfigCacheEntry(T value) {
		this.value = value;
		this.lastUpdateTime = System.currentTimeMillis();
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
		this.lastUpdateTime = System.currentTimeMillis();
	}

	public boolean isExpired() {
		return System.currentTimeMillis() - lastUpdateTime > EXPIRATION_TIME;
	}

}
