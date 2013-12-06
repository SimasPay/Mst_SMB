/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao.query;

import java.util.Date;

/**
 *
 * @author Raju
 */
public class BulkUploadQuery extends BaseQuery {
	  //Before Correcting errors reported by Findbugs:
		//A field Long _ID and its setters and getters
	
	  //After Correcting the errors reported by Findbugs:deleted the _ID field and its setters and getters.Also changed its references where
		//it is used to the base query _id field.changes to the BulkUploadDao,BulkUploadProcessor
	
	
    private Date _startDate;
    private Date _endDate;
    private Integer _FileType;
    private Integer _FileStatus;
    private Long _mdnID;
    private boolean _associationOrdered;
    private Date _deliveryDate;
    private Date paymentDate;
    private String nameSearch;
    private Long userId;

    public boolean isAssociationOrdered() {
        return _associationOrdered;
    }

    public void setAssociationOrdered(boolean _associationOrdered) {
        this._associationOrdered = _associationOrdered;
    }

    public Long getMdnID() {
        return _mdnID;
    }

    public void setMdnID(Long _mdnID) {
        this._mdnID = _mdnID;
    }

    public Integer getFileStatus() {
        return _FileStatus;
    }

    public void setFileStatus(Integer _FileStatus) {
        this._FileStatus = _FileStatus;
    }

    public Integer getFileType() {
        return _FileType;
    }

    public void setFileType(Integer _FileType) {
        this._FileType = _FileType;
    }

    public void setEndDate(Date _endDate) {
        this._endDate = _endDate;
    }

    public void setStartDate(Date _startDate) {
        this._startDate = _startDate;
    }

    public Date getEndDate() {
        return _endDate;
    }

    public Date getStartDate() {
        return _startDate;
    }

    public void setDeliveryDate(Date _deliveryDate) {
        this._deliveryDate = _deliveryDate;
    }

    public Date getDeliveryDate() {
        return _deliveryDate;
    }

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getNameSearch() {
		return nameSearch;
	}

	public void setNameSearch(String nameSearch) {
		this.nameSearch = nameSearch;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
