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
	
	private String info1;
	private String info2;
	private String info3;
	
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

	public String getInfo1() {
		return info1;
	}

	public void setInfo1(String info1) {
		this.info1 = info1;
	}

	public String getInfo2() {
		return info2;
	}

	public void setInfo2(String info2) {
		this.info2 = info2;
	}

	public String getInfo3() {
		return info3;
	}

	public void setInfo3(String info3) {
		this.info3 = info3;
	}
}
