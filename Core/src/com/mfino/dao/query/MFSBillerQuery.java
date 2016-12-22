/**
 * 
 */
package com.mfino.dao.query;

import java.util.Date;

/**
 * @author Bala Sunku
 *
 */
public class MFSBillerQuery extends BaseQuery {
	
	private String billerName;
	private String exactBillerName;
	private String billerCode;
	private String billerType;
	
	private Date   startRegistrationDate;	
	private Date   endRegistrationDate;  
	
	public String getBillerName() {
		return billerName;
	}
	public void setBillerName(String billerName) {
		this.billerName = billerName;
	}
	public String getExactBillerName() {
		return exactBillerName;
	}
	public void setExactBillerName(String exactBillerName) {
		this.exactBillerName = exactBillerName;
	}
	public String getBillerCode() {
		return billerCode;
	}
	public void setBillerCode(String billerCode) {
		this.billerCode = billerCode;
	}
	public String getBillerType() {
		return billerType;
	}
	public void setBillerType(String billerType) {
		this.billerType = billerType;
	}
	public Date getStartRegistrationDate() {
		return startRegistrationDate;
	}
	public void setStartRegistrationDate(Date startRegistrationDate) {
		this.startRegistrationDate = startRegistrationDate;
	}
	
	public Date getEndRegistrationDate() {
		return endRegistrationDate;
	}
	public void setEndRegistrationDate(Date endRegistrationDate) {
		this.endRegistrationDate = endRegistrationDate;
	}
}
