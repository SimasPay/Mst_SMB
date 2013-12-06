package com.mfino.handlers;




public class MultixResponseObject {
	private String code;
	private String message;
	private String success;
	private Long transactionId;
	private Long parenttransactionid;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public Long getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}
	public Long getParenttransactionid() {
		return parenttransactionid;
	}
	public void setParenttransactionid(Long parenttransactionid) {
		this.parenttransactionid = parenttransactionid;
	}

}
