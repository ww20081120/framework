package com.fccfc.framework.web.manager.bean.permission;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.fccfc.framework.config.core.bean.DirectoryPojo;
import com.fccfc.framework.db.core.BaseEntity;

/**
 * 
 * <Description> <br> 
 *  
 * @author 胡攀<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月20日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.manager.bean.menu <br>
 */
@Entity(name = "FUNCTION")
public class FunctionPojo extends BaseEntity {

	/**
	 * serialVersionUID <br>
	 */
	private static final long serialVersionUID = 1L;

	/** 功能点标识 */
	/** FUNCTION_ID */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "FUNCTION_ID")
	private Long functionId;

	/** 目录代码 */
	/** DIRECTORY_CODE */
	@Column(name = "DIRECTORY_CODE")
	private String directoryCode;

	/** 功能点名称 */
	/** FUNCTION_NAME */
	@Column(name = "FUNCTION_NAME")
	private String functionName;

	/** 创建时间 */
	/** CREATE_TIME */
	@Column(name = "CREATE_TIME")
	private Date createTime;

	/** 描述 */
	/** REMARK */
	@Column(name = "REMARK")
	private String remark;
	
	@Transient
	private String directoryName;
	
	public FunctionPojo() {
		super();
	}

	public FunctionPojo(String directoryCode,
			String functionName, Date createTime, String remark) {
		super();
		this.directoryCode = directoryCode;
		this.functionName = functionName;
		this.createTime = createTime;
		this.remark = remark;
	}


	/** 目录 */
	@Transient
	private List<DirectoryPojo> directory;

	public Long getFunctionId() {
		return functionId;
	}

	public void setFunctionId(Long functionId) {
		this.functionId = functionId;
	}

	public String getDirectoryCode() {
		return directoryCode;
	}

	public void setDirectoryCode(String directoryCode) {
		this.directoryCode = directoryCode;
	}

	
	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<DirectoryPojo> getDirectory() {
		return directory;
	}

	public void setDirectory(List<DirectoryPojo> directory) {
		this.directory = directory;
	}

	public String getDirectoryName() {
		return directoryName;
	}

	public void setDirectoryName(String directoryName) {
		this.directoryName = directoryName;
	}
	

}
