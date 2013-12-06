package com.mfino.zenith.dstv;

import java.util.ArrayList;
import java.util.List;

public class DSTVParameters
{
	
	public String getSWPAYTXNCODE() {
		return SWPAYTXNCODE;
	}
	public void setSWPAYTXNCODE(String sWPAYTXNCODE) {
		SWPAYTXNCODE = sWPAYTXNCODE;
	}
	public String getSWDTIME() {
		return SWDTIME;
	}
	public void setSWDTIME(String sWDTIME) {
		SWDTIME = sWDTIME;
	}
	public String getMerchantID() {
		return MerchantID;
	}
	public void setMerchantID(String merchantID) {
		MerchantID = merchantID;
	}
	public String getTerminalID() {
		return TerminalID;
	}
	public void setTerminalID(String terminalID) {
		TerminalID = terminalID;
	}
	public String getAmount() {
		return Amount;
	}
	public void setAmount(String amount) {
		Amount = amount;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public String getCustServID() {
		return CustServID;
	}
	public void setCustServID(String custServID) {
		CustServID = custServID;
	}
	public String getResponseCode() {
		return ResponseCode;
	}
	public void setResponseCode(String responseCode) {
		ResponseCode = responseCode;
	}
	public String getUID() {
		return UID;
	}
	public void setUID(String uID) {
		UID = uID;
	}
	public String getPWD() {
		return PWD;
	}
	public void setPWD(String pWD) {
		PWD = pWD;
	}
	private String SWPAYTXNCODE;
	private String SWDTIME;
	private String MerchantID;
	private String TerminalID;
	private String Amount;
	private String Description;
	private String CustServID;
	private String ResponseCode;
	private String UID;
	private String PWD;
	private String dateFormat;
	
	public String getDateFormat() {
		return dateFormat;
	}
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
}
