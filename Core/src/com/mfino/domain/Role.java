package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Role generated by hbm2java
 */
@Entity
@Table(name = "ROLE")
public class Role extends Base implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	public static final String FieldName_EnumCode = "enumcode";
	public static final String FieldName_EnumValue = "enumvalue";
	public static final String FieldName_DisplayText = "displaytext";
	public static final String FieldName_IsSystemUser = "issystemuser";
	public static final String FieldName_PriorityLevel = "prioritylevel";
	private String enumcode;
	private String enumvalue;
	private String displaytext;
	private Boolean issystemuser;
	private Integer prioritylevel;
	private Long id;

	public Role() {
	}

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "id_Sequence")
	@SequenceGenerator(name = "id_Sequence", sequenceName = "role_ID_SEQ")
	@Column(name = "ID", unique = true, nullable = false, scale = 0)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "ENUMCODE", length = 1020)
	public String getEnumcode() {
		return this.enumcode;
	}

	public void setEnumcode(String enumcode) {
		this.enumcode = enumcode;
	}

	@Column(name = "ENUMVALUE", length = 1020)
	public String getEnumvalue() {
		return this.enumvalue;
	}

	public void setEnumvalue(String enumvalue) {
		this.enumvalue = enumvalue;
	}

	@Column(name = "DISPLAYTEXT", length = 1020)
	public String getDisplaytext() {
		return this.displaytext;
	}

	public void setDisplaytext(String displaytext) {
		this.displaytext = displaytext;
	}

	@Column(name = "ISSYSTEMUSER")
	public Boolean getIssystemuser() {
		return this.issystemuser;
	}

	public void setIssystemuser(Boolean issystemuser) {
		this.issystemuser = issystemuser;
	}

	@Column(name = "PRIORITYLEVEL", precision = 3, scale = 0)
	public Integer getPrioritylevel() {
		return this.prioritylevel;
	}

	public void setPrioritylevel(Integer prioritylevel) {
		this.prioritylevel = prioritylevel;
	}

}
