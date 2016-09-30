package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import com.mfino.hibernate.Timestamp;

/**
 * ReportParameters generated by hbm2java
 */
@Entity
@Table(name = "REPORT_PARAMETERS")
public class ReportParameters extends Base implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	public static final String FieldName_ParameterName = "parametername";
	private String parametername;
	private String parametervalue;
	private String description;

	public ReportParameters() {
	}

	public ReportParameters(BigDecimal id, Timestamp lastupdatetime,
			String updatedby, Timestamp createtime, String createdby) {
		this.id = id;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
	}

	public ReportParameters(BigDecimal id, Timestamp lastupdatetime,
			String updatedby, Timestamp createtime, String createdby,
			String parametername, String parametervalue, String description) {
		this.id = id;
		this.lastupdatetime = lastupdatetime;
		this.updatedby = updatedby;
		this.createtime = createtime;
		this.createdby = createdby;
		this.parametername = parametername;
		this.parametervalue = parametervalue;
		this.description = description;
	}

	

	@Column(name = "PARAMETERNAME", length = 1020)
	public String getParametername() {
		return this.parametername;
	}

	public void setParametername(String parametername) {
		this.parametername = parametername;
	}

	@Column(name = "PARAMETERVALUE", length = 1020)
	public String getParametervalue() {
		return this.parametervalue;
	}

	public void setParametervalue(String parametervalue) {
		this.parametervalue = parametervalue;
	}

	@Column(name = "DESCRIPTION", length = 1020)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
