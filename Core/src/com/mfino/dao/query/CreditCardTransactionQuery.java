/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao.query;

import java.util.Date;

/**
 *
 * @author ADMIN
 */
public class CreditCardTransactionQuery extends BaseQuery {

    private Date _startDate;
    private String _authId;
    private String transStatus;
    private String operation;
    private Integer ccFailureReason;   
    private Boolean noOrder;
    public Integer getCcFailureReason() {
		return ccFailureReason;
	}

	public void setCcFailureReason(Integer ccFailureReason) {
		this.ccFailureReason = ccFailureReason;
	}

	public String getAuthId() {
        return _authId;
    }

    public void setAuthId(String _authId) {
        this._authId = _authId;
    }

    public String getBankReferenceNumber() {
        return _bankReferenceNumber;
    }

    public void setBankReferenceNumber(String _bankReferenceNumber) {
        this._bankReferenceNumber = _bankReferenceNumber;
    }

    public Long getTransactionId() {
        return _transactionId;
    }

    public void setTransactionId(Long _transactionId) {
        this._transactionId = _transactionId;
    }
    private Long _transactionId;
    private String _bankReferenceNumber;

    public String getDestMdn() {
        return _destMdn;
    }

    public void setDestMdn(String _destMdn) {
        this._destMdn = _destMdn;
    }

    public Date getEndDate() {
        return _endDate;
    }

    public void setEndDate(Date _endDate) {
        this._endDate = _endDate;
    }

    public Date getStartDate() {
        return _startDate;
    }

    public void setStartDate(Date _startDate) {
        this._startDate = _startDate;
    }
    public void setTransStatus(String transStatus) {
		this.transStatus = transStatus;
	}

	public String getTransStatus() {
		return transStatus;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getOperation() {
		return operation;
	}
	
	public void setNoOrder(Boolean noOrder) {
		this.noOrder = noOrder;
	}

	public Boolean getNoOrder() {
		return noOrder;
	}
	private Date _endDate;
    private String _destMdn;
}
