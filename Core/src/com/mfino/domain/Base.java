package com.mfino.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.Type;

import com.mfino.hibernate.Timestamp;

@MappedSuperclass
public class Base {
	
	public static final String FieldName_RecordID = "id";
	public static final String FieldName_LastUpdateTime = "lastupdatetime";
	public static final String FieldName_CreateTime = "createtime";
	public static final String FieldName_CreatedBy = "createdby";
	
	
	protected long version;
	protected Timestamp lastupdatetime;
	protected String updatedby;
	protected Timestamp createtime;
	protected String createdby;
	
	@Version
	@Column(name = "VERSION", nullable = false, precision = 10, scale = 0)
	public long getVersion() {
		return this.version;
	}

	public void setVersion(long version) {
		this.version = version;
	}
	@Type(type = "userDefinedTimeStamp")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LASTUPDATETIME", nullable = false)
	public Timestamp getLastupdatetime() {
		return this.lastupdatetime;
	}

	public void setLastupdatetime(Timestamp lastupdatetime) {
		this.lastupdatetime = lastupdatetime;
	}

	@Column(name = "UPDATEDBY", nullable = false)
	public String getUpdatedby() {
		return this.updatedby;
	}

	public void setUpdatedby(String updatedby) {
		this.updatedby = updatedby;
	}

	@Type(type = "userDefinedTimeStamp")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATETIME", nullable = false)
	public Timestamp getCreatetime() {
		return this.createtime;
	}

	public void setCreatetime(Timestamp createtime) {
		this.createtime = createtime;
	}

	@Column(name = "CREATEDBY", nullable = false)
	public String getCreatedby() {
		return this.createdby;
	}

	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}

}
