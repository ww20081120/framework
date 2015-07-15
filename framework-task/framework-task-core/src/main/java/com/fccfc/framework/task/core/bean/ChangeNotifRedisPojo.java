package com.fccfc.framework.task.core.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;

/**
 * <Description> <br>
 * 
 * @author shao.dinghui<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月14日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.task.core.bean <br>
 */
public class ChangeNotifRedisPojo implements Serializable{

	/**
	 * serialVersionUID <br>
	 */
	private static final long serialVersionUID = -5663382627653565750L;

	/** changeNotifId */
	@Column(name = "CHANGE_NOTIF_ID")
	private Integer changeNotifId;

	/** changeNotifId */
	@Column(name = "TABLE_NAME")
	private String tableName;

	/** changeNotifId */
	@Column(name = "KEY_VALUE")
	private String keyValue;

	/** changeNotifId */
	@Column(name = "CREATED_DATE")
	private Date createdDate;

	/** changeNotifId */
	@Column(name = "ACTION_TYPE")
	private String actionType;

	public ChangeNotifRedisPojo() {
	}

	public ChangeNotifRedisPojo(Integer changeNotifId, String tableName,
			String keyValue, Date createDate, String actionType) {
		this.changeNotifId = changeNotifId;
		this.tableName = tableName;
		this.keyValue = keyValue;
		this.createdDate = createDate;
		this.actionType = actionType;
	}

	public Integer getChangeNotifId() {
		return changeNotifId;
	}

	public void setChangeNotifId(Integer changeNotifId) {
		this.changeNotifId = changeNotifId;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
}
