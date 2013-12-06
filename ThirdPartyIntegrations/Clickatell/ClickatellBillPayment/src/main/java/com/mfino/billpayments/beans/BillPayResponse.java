package com.mfino.billpayments.beans;

import com.mfino.mce.core.util.BackendResponse;

/**
 * @author Sasi
 *
 */
public class BillPayResponse extends BackendResponse {
	
	private Integer response;
	
	private String inResponseCode;
	private String inTxnId;
	private String biller;
	
	public String getInResponseCode() {
		return inResponseCode;
	}
	
	public void setInResponseCode(String inResponseCode) {
		this.inResponseCode = inResponseCode;
	}
	
	public String getInTxnId() {
		return inTxnId;
	}
	
	public void setInTxnId(String inTxnId) {
		this.inTxnId = inTxnId;
	}
	
	public String getBiller() {
		return biller;
	}
	
	public void setBiller(String biller) {
		this.biller = biller;
	}

	public Integer getResponse() {
		return response;
	}

	public void setResponse(Integer response) {
		this.response = response;
	}
}
