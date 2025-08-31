/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.jpa.memory.dao;

import java.util.List;

import com.hbasesoft.framework.ai.agent.jpa.memory.po.MessagePo4Jpa;
import com.hbasesoft.framework.db.Dao;
import com.hbasesoft.framework.db.core.BaseDao;
import com.hbasesoft.framework.db.core.annotation.Sql;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月31日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.jpa.memory.dao <br>
 */
@Dao
public interface MessageDao extends BaseDao<MessagePo4Jpa> {

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @return <br>
	 */
	@Sql(value = "SELECT DISTINCT conversation_id FROM ai_chat_memory", bean = String.class)
	List<String> findConversationIds();
}
