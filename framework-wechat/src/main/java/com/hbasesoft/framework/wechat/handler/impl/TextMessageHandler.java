/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.wechat.handler.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.wechat.bean.AccountPojo;
import com.hbasesoft.framework.wechat.bean.AutoresponsePojo;
import com.hbasesoft.framework.wechat.handler.AbstractMessageHandler;
import com.hbasesoft.framework.wechat.util.WechatUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年6月4日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.actsports.portal.service.impl <br>
 */
@Service("textMessageHandler")
public class TextMessageHandler extends AbstractMessageHandler {

	private static Logger logger = LoggerFactory.getLogger(TextMessageHandler.class);

	/**
	 * Description: <br>
	 * 
	 * @author 王伟<br>
	 * @taskId <br>
	 * @param msgId
	 * @param toUserName
	 * @param entity
	 * @param content
	 * @return
	 * @throws VccException
	 *             <br>
	 */
	@Override
	public String process(String msgId, String toUserName, AccountPojo entity, String content,
			Map<String, String> requestMap, String imagePath, String serverPath, String message) throws FrameworkException {

		String respMessage = null;

		// =================================================================================================================
		// Step.1 判断关键字信息中是否管理该文本内容。有的话优先采用数据库中的回复

		// 获取关键字管理的列表，匹配后返回信息
		List<AutoresponsePojo> autoResponses = wechatDao.findByProperty(AutoresponsePojo.class,
				AutoresponsePojo.ACCOUNT_ID, entity.getId());

		AutoresponsePojo autoResponse = null;
		@SuppressWarnings("unused")
        AutoresponsePojo defaultResp = null;

		for (AutoresponsePojo r : autoResponses) {
			if (super.matchKeyword(r.getKeyword(), content)) {
				logger.info("---------sys_accountId----查询结果----" + r);
				autoResponse = r;
				break;
			} else if (com.hbasesoft.framework.common.utils.CommonUtil.isEmpty(r.getKeyword())) {
				defaultResp = r;
			}
		}
		// 根据系统配置的关键字信息，返回对应的消息
		if (autoResponse != null) {
			respMessage = autoResponse(autoResponse, toUserName, entity, imagePath, serverPath);
		} /*else {
			// Step.2 通过微信扩展接口（支持二次开发，例如：翻译，天气）
			List<ExpandconfigPojo> weixinExpandconfigEntityLst = wechatDao.findByProperty(ExpandconfigPojo.class,
					ExpandconfigPojo.ORG_CODE, entity.getOrgCode());

			boolean noAnswerFlag = true;

			if (CommonUtil.isNotEmpty(weixinExpandconfigEntityLst)) {
				for (ExpandconfigPojo wec : weixinExpandconfigEntityLst) {

//					if (super.matchKeyword(wec.getKeyword(), content)) {
//						noAnswerFlag = false;
//						respMessage = getExpendMsg(wec, msgId, toUserName, entity, content, requestMap);
//						break;
//					}

				}
			}

			// 默认回答
			if (noAnswerFlag && defaultResp != null) {
				respMessage = autoResponse(defaultResp, toUserName, entity, imagePath, serverPath);
			}
		}*/

		return respMessage;
	}

	private String autoResponse(AutoresponsePojo resp, String toUserName, AccountPojo entity, String imagePath,
			String serverPath) throws ServiceException {
		String respMessage = null;
		String resMsgType = resp.getMsgtype();
		if (WechatUtil.REQ_MESSAGE_TYPE_TEXT.equals(resMsgType)) {
			// 根据返回消息key，获取对应的文本消息返回给微信客户端
			respMessage = getTextMsg(resp.getRescontent(), entity.getWeixinAccountid(), toUserName);
		} else if (WechatUtil.RESP_MESSAGE_TYPE_NEWS.equals(resMsgType)) {
			respMessage = super.getNewsItem(resp.getRescontent(), entity.getWeixinAccountid(), toUserName, imagePath,
					serverPath);
		}
		return respMessage;
	}

	@Override
	public void asynProcess(String msgId, String toUserName, AccountPojo entity, String content,
			Map<String, String> requestMap, String imagePath, String serverPath, String message) throws FrameworkException {
	}

}
