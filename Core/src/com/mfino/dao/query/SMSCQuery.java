/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

/**
 *
 * @author Maruthi
 */
public class SMSCQuery extends BaseQuery{

    private String shortCode;
    private String longNumber;
    private Long partnerID;
    private String smartfrenSMSCID;
    private String otherLocalOperatorSMSCID;
    private Integer charging;
    private String header;
    private String footer;
 
	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}

	public String getShortCode() {
		return shortCode;
	}

	public void setLongNumber(String longNumber) {
		this.longNumber = longNumber;
	}

	public String getLongNumber() {
		return longNumber;
	}

	public void setPartnerID(Long partnerID) {
		this.partnerID = partnerID;
	}

	public Long getPartnerID() {
		return partnerID;
	}

	public void setSmartfrenSMSCID(String smartfrenSMSCID) {
		this.smartfrenSMSCID = smartfrenSMSCID;
	}

	public String getSmartfrenSMSCID() {
		return smartfrenSMSCID;
	}

	public void setOtherLocalOperatorSMSCID(String otherLocalOperatorSMSCID) {
		this.otherLocalOperatorSMSCID = otherLocalOperatorSMSCID;
	}

	public String getOtherLocalOperatorSMSCID() {
		return otherLocalOperatorSMSCID;
	}

	public void setCharging(Integer charging) {
		this.charging = charging;
	}

	public Integer getCharging() {
		return charging;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getHeader() {
		return header;
	}

	public void setFooter(String footer) {
		this.footer = footer;
	}

	public String getFooter() {
		return footer;
	}
}
