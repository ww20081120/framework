package com.hbasesoft.framework.wechat.bean.resp;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * 
 * <Description> <br> 
 *  
 * @author zhasiwei<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2019年5月30日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.wechat.bean.resp <br>
 */
public class CreateQrcodeResp extends BaseEntity {

	/**
	 * serialVersionUID <br>
	 */
	private static final long serialVersionUID = 5759498554020262072L;
	
	private String ticket;
	
	private String url;

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	

}
