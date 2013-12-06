/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

import java.util.Date;

/**
 *
 * @author xchen
 */
public class SubscriberMdnQuery extends BaseQuery{
    private Long _id;
    private String _mdn;
    private String _firstName;
    private String _lastName;
    private Date _startRegistrationDate;
    private Date _endRegistrationDate;    
    private Integer _version;
    private String _exactMDN;
    private boolean _associationOrdered;
    private boolean _isSubscriberMDNStatusRetire;
    private boolean _isMDNNotRecycled;
    private Integer[] statusIn;
    private Date statusTimeGE;
    private Date statusTimeLT;
    private Integer statusNE;
    private Integer statusEQ;
    private Integer BankCode;
    private boolean onlySubscribers;
    private Integer state;
    private String accountNumber;
    private Boolean isForceCloseRequested;
    
    public Integer getBankCode() {
        return BankCode;
    }

    public void setBankCode(Integer BankCode) {
        this.BankCode = BankCode;
    }

    public Date getStatusTimeGE() {
        return statusTimeGE;
    }

    public void setStatusTimeGE(Date statusTimeGE) {
        this.statusTimeGE = statusTimeGE;
    }

    public Date getStatusTimeLT() {
        return statusTimeLT;
    }

    public void setStatusTimeLT(Date statusTimeLT) {
        this.statusTimeLT = statusTimeLT;
    }

    public Integer[] getStatusIn() {
        return statusIn;
    }

    public void setStatusIn(Integer[] statusIn) {
        this.statusIn = statusIn;
    }

    public void setSubscriberMDNStatusRetire(boolean isRetired) {
        this._isSubscriberMDNStatusRetire = isRetired;
    }

    public boolean isSubscriberMDNStatusRetire() {
        return _isSubscriberMDNStatusRetire;
    }

    public boolean isAssociationOrdered() {
        return _associationOrdered;
    }

    public void setAssociationOrdered(boolean _associationOrdered) {
        this._associationOrdered = _associationOrdered;
    }
    
    /**
     * @return the _mdn
     */
    public String getMdn() {
        return _mdn;
    }

    /**
     * @param mdn the _mdn to set
     */
    public void setMdn(String mdn) {
        this._mdn = mdn;
    }

    /**
     * @return the _firstName
     */
    public String getFirstName() {
        return _firstName;
    }

    /**
     * @param firstName the _firstName to set
     */
    public void setFirstName(String firstName) {
        this._firstName = firstName;
    }

    /**
     * @return the _lastName
     */
    public String getLastName() {
        return _lastName;
    }

    /**
     * @param lastName the _lastName to set
     */
    public void setLastName(String lastName) {
        this._lastName = lastName;
    }

    public Date getEndRegistrationDate() {
        return _endRegistrationDate;
    }

    public void setEndRegistrationDate(Date _endRegistrationDate) {
        this._endRegistrationDate = _endRegistrationDate;
    }

    public Date getStartRegistrationDate() {
        return _startRegistrationDate;
    }

    public void setStartRegistrationDate(Date _startRegistrationDate) {
        this._startRegistrationDate = _startRegistrationDate;
    }

    /**
     * @return the _id
     */
    public Long getId() {
        return _id;
    }

    /**
     * @param id the _id to set
     */
    public void setId(Long id) {
        this._id = id;
    }

    public Integer getVersion() {
        return _version;
    }

    public void setVersion(Integer _version) {
        this._version = _version;
    }

    public String getExactMDN() {
        return _exactMDN;
    }

    public void setExactMDN(String _exactMDN) {
        this._exactMDN = _exactMDN;
    }

    public void setMDNNotRecycled(boolean _isMDNRecycled) {
        this._isMDNNotRecycled = _isMDNRecycled;
    }

    public boolean isMDNNotRecycled() {
        return _isMDNNotRecycled;
    }

    public void setStatusNE(Integer statusNE) {
      this.statusNE = statusNE;
    }

    public Integer getStatusNE() {
      return statusNE;
    }

    public void setStatusEQ(Integer statusEQ) {
      this.statusEQ = statusEQ;
    }

    public Integer getStatusEQ() {
      return statusEQ;
    }

	public void setOnlySubscribers(boolean onlySubscribers) {
		this.onlySubscribers = onlySubscribers;
	}

	public boolean isOnlySubscribers() {
		return onlySubscribers;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getState() {
		return state;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public Boolean getIsForceCloseRequested() {
		return isForceCloseRequested;
	}

	public void setIsForceCloseRequested(Boolean isForceCloseRequested) {
		this.isForceCloseRequested = isForceCloseRequested;
	}
}
