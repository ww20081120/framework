package com.fccfc.framework.web.manager.bean.permission;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.fccfc.framework.db.core.BaseEntity;

@Entity(name = "URL_RESOURCE")
public class UrlResourcePojo extends BaseEntity {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/** RESOURCE_ID */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RESOURCE_ID")
	private Long resourceId;

	/** FUNCTION_ID */
	@Column(name = "FUNCTION_ID")
	private Long functionId;

	/** RESOURCE_NAME */
	@Column(name = "RESOURCE_NAME")
	private String resourceName;

	/** URL */
	@Column(name = "URL")
	private String url;

	/** METHOD */
	@Column(name = "METHOD")
	private String method;

	/** EVENT_ID */
	@Column(name = "EVENT_ID")
	private String eventId;

	@Transient
	private String functionName;

	public UrlResourcePojo() {
		super();
	}

	public UrlResourcePojo(Long functionId, String resourceName, String url,
			String method, String eventId) {
		super();
		this.functionId = functionId;
		this.resourceName = resourceName;
		this.url = url;
		this.method = method;
		this.eventId = eventId;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public Long getFunctionId() {
		return functionId;
	}

	public void setFunctionId(Long functionId) {
		this.functionId = functionId;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

}
