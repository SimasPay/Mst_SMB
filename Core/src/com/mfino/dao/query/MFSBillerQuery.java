/**
 * 
 */
package com.mfino.dao.query;

/**
 * @author Bala Sunku
 *
 */
public class MFSBillerQuery extends BaseQuery {
	
	private String billerName;
	private String exactBillerName;
	private String billerCode;
	private String billerType;
	
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
}
