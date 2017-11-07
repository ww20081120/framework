package com.hbasesoft.framework.wechat.executor;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import com.hbasesoft.framework.cache.core.CacheHelper;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.message.core.event.EventData;
import com.hbasesoft.framework.wechat.CacheCodeDef;
import com.hbasesoft.framework.wechat.bean.AccountPojo;
import com.hbasesoft.framework.wechat.bean.ExpandconfigPojo;
import com.hbasesoft.framework.wechat.handler.WechatMessageHandler;
import com.hbasesoft.framework.wechat.service.WechatService;
import com.hbasesoft.framework.wechat.service.impl.WechatServiceImpl;
import com.hbasesoft.framework.wechat.util.WechatUtil;
public class WechatExpandExecutor implements Runnable {
	 
    private EventData data;
 
    public WechatExpandExecutor(EventData data){ 
        this.data = data;
    }
 
    @Override
    public void run() {
    	LoggerUtil.info("start WechatExpandExecutor EventDate=[{0}]", data);
        processCommand();
        LoggerUtil.info("end WechatExpandExecutor EventDate=[{0}]", data);
    }
 
    private void processCommand() {
    	String msgId = data.getParameter("msgId");
		String fromUserName = data.getParameter("fromUserName");
		String content = data.getParameter("content");
		String message = data.getParameter("message");
		String imagePath = data.getParameter("imagePath");
		String serverPath = data.getParameter("serverPath");
		String accountId = data.getParameter("accountId");
		
		Map<String, String> requestMap = WechatUtil.parseXml(message);
		
		WechatService wechatService = (WechatService) ContextHolder.getContext().getBean("WechatServiceImpl");
		List<ExpandconfigPojo> weixinExpandconfigEntityLst = CacheHelper.getCache()
		        .get(CacheCodeDef.EXPAND_CONFIG_CACHE, CacheCodeDef.EXPAND_CONFIG_CACHE);
		if (CommonUtil.isEmpty(weixinExpandconfigEntityLst)) {
		    weixinExpandconfigEntityLst = wechatService.queryAllExpandConfig();
		}
		    
		if (CommonUtil.isNotEmpty(weixinExpandconfigEntityLst)) {
			AccountPojo account = wechatService.getAccountById(accountId);
		    if (account == null) {
		    	LoggerUtil.info("not found accountId=[{0}]", accountId);
		        return;
		    }
			WechatMessageHandler expandHandler = null;
			for (ExpandconfigPojo expandConfig : weixinExpandconfigEntityLst) {
				expandHandler = ContextHolder.getContext().getBean(expandConfig.getClassname(),
						WechatMessageHandler.class);
				
				if (expandHandler != null) {
					expandHandler.asynProcess(msgId, fromUserName, account, content, requestMap,
							imagePath, serverPath, message);
				}
			}
		}
    	
    }
 
    @Override
    public String toString(){
    	return ToStringBuilder.reflectionToString(this,
                ToStringStyle.MULTI_LINE_STYLE);
    }
}