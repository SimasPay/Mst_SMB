package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * EnumText generated by hbm2java
 */
@Entity
@Table(name = "ENUM_TEXT", uniqueConstraints = @UniqueConstraint(columnNames = {
		"LANGUAGE", "TAGID", "ENUMCODE" }))
public class EnumText extends Base implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	public static final String FieldName_TagID = "tagid";
	public static final String FieldName_EnumCode = "enumcode";
	public static final String FieldName_TagName = "tagname";
	public static final String FieldName_Language = "language";
	public static final String FieldName_EnumValue = "enumvalue";
	public static final String FieldName_DisplayText = "displaytext";
	
	private long language;
	private String tagname;
	private long tagid;
	private String enumcode;
	private String enumvalue;
	private String displaytext;

	public EnumText() {
	}

	

	@Column(name = "LANGUAGE", nullable = false, precision = 10, scale = 0)
	public long getLanguage() {
		return this.language;
	}

	public void setLanguage(long language) {
		this.language = language;
	}

	@Column(name = "TAGNAME", nullable = false, length = 1020)
	public String getTagname() {
		return this.tagname;
	}

	public void setTagname(String tagname) {
		this.tagname = tagname;
	}

	@Column(name = "TAGID", nullable = false, precision = 10, scale = 0)
	public long getTagid() {
		return this.tagid;
	}

	public void setTagid(long tagid) {
		this.tagid = tagid;
	}

	@Column(name = "ENUMCODE", nullable = false, length = 1020)
	public String getEnumcode() {
		return this.enumcode;
	}

	public void setEnumcode(String enumcode) {
		this.enumcode = enumcode;
	}

	@Column(name = "ENUMVALUE", nullable = false, length = 1020)
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

}
