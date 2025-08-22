/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.jmanus.dynamic.jpa.memory.service.impl;

import java.util.List;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hbasesoft.framework.ai.jmanus.dynamic.jpa.memory.dao.MemoryDao;
import com.hbasesoft.framework.ai.jmanus.dynamic.jpa.memory.po.MemoryPo4Jpa;
import com.hbasesoft.framework.ai.jmanus.dynamic.jpa.memory.service.MemoryManagerService;
import com.hbasesoft.framework.ai.jmanus.dynamic.memory.service.MemoryService;
import com.hbasesoft.framework.ai.jmanus.dynamic.memory.vo.MemoryVo;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年8月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.jmanus.dynamic.memory.service.impl <br>
 */

@Service
public class MemoryServiceImpl implements MemoryService, MemoryManagerService {

	@Autowired
	private MemoryDao memoryRepository;

	@Autowired
	private ChatMemory chatMemory;

	@Override
	@Transactional(readOnly = true)
	public List<MemoryPo4Jpa> getMemories() {
		List<MemoryPo4Jpa> memoryEntities = memoryRepository.queryAll();
		memoryEntities.forEach(memoryEntity -> {
			List<Message> messages = chatMemory.get(memoryEntity.getMemoryId());
			memoryEntity.setMessages(messages);
		});
		memoryEntities.stream()
				.sorted((m1, m2) -> Math.toIntExact(m1.getCreateTime().getTime() - m2.getCreateTime().getTime()))
				.toList();
		return memoryEntities;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteMemory(String memoryId) {
		chatMemory.clear(memoryId);
		memoryRepository.deleteByLambda(q -> q.eq(MemoryPo4Jpa::getMemoryId, memoryId));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public MemoryVo saveMemory(MemoryVo memoryEntity) {
		MemoryPo4Jpa findEntity = memoryRepository
				.getByLambda(q -> q.eq(MemoryPo4Jpa::getMemoryId, memoryEntity.getMemoryId()));
		if (findEntity != null) {
			findEntity.setMessages(null);
		} else {
			findEntity = new MemoryPo4Jpa();
			BeanUtils.copyProperties(memoryEntity, findEntity);
		}
		memoryRepository.save(findEntity);
		findEntity.setMessages(chatMemory.get(findEntity.getMemoryId()));
		return convert(findEntity);
	}

	private MemoryVo convert(MemoryPo4Jpa memoryEntity) {
		MemoryVo memoryVo = new MemoryVo();
		BeanUtils.copyProperties(memoryEntity, memoryVo);
		memoryVo.setMessages(memoryEntity.getMessages());
		return memoryVo;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public MemoryPo4Jpa updateMemory(MemoryPo4Jpa memoryEntity) {
		MemoryPo4Jpa findEntity = memoryRepository
				.getByLambda(q -> q.eq(MemoryPo4Jpa::getMemoryId, memoryEntity.getMemoryId()));
		if (findEntity == null) {
			throw new IllegalArgumentException();
		}
		findEntity.setMemoryName(memoryEntity.getMemoryName());
		memoryRepository.save(findEntity);
		findEntity.setMessages(chatMemory.get(findEntity.getMemoryId()));
		return findEntity;
	}

	@Override
	@Transactional(readOnly = true)
	public MemoryPo4Jpa singleMemory(String memoryId) {
		MemoryPo4Jpa findEntity = memoryRepository.getByLambda(q -> q.eq(MemoryPo4Jpa::getMemoryId, memoryId));
		if (findEntity == null) {
			throw new IllegalArgumentException();
		}
		findEntity.setMessages(chatMemory.get(findEntity.getMemoryId()));
		return findEntity;
	}

}
