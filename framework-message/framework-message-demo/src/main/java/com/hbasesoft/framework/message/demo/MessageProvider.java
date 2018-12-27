/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.demo;

import com.hbasesoft.framework.message.core.MessageHelper;
import com.hbasesoft.framework.message.core.MessagePublisher;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年2月19日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.test.message <br>
 */
public class MessageProvider {

	public static void main(String[] args) throws InterruptedException {
		MessagePublisher publisher = MessageHelper.createMessagePublisher();
		int i = 0;
		while (true) {
			++i;
			publisher.publish("log-p21", (i + "").getBytes());
			// publisher.publish("log-p16", (i + "").getBytes());

			// if (i % 5 == 0) {
			// publisher.publish("log-p2", (++i + "").getBytes());
			// }
			System.out.println("produce " + i);
			Thread.sleep(100);
		}
	}

}
