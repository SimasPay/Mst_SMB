/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

/**
 *
 * @author Raju
 */
public class BulkUploadEntryQuery  extends BaseQuery {
    private Long _bulkid;

    private Integer bulkUploadLineNumber;
    private Integer status;
    private Boolean isUnRegistered;
    private Integer[] uploadLineNumbers;
    
    public Integer getBulkUploadLineNumber() {
		return bulkUploadLineNumber;
	}

	public void setBulkUploadLineNumber(Integer bulkUploadLineNumber) {
		this.bulkUploadLineNumber = bulkUploadLineNumber;
	}

    public Long getBulkid() {
        return _bulkid;
    }

    public void setBulkid(Long _bulkid) {
        this._bulkid = _bulkid;
    }

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Boolean getIsUnRegistered() {
		return isUnRegistered;
	}

	public void setIsUnRegistered(Boolean isUnRegistered) {
		this.isUnRegistered = isUnRegistered;
	}

	public Integer[] getUploadLineNumbers() {
		return uploadLineNumbers;
	}

	public void setUploadLineNumbers(Integer[] uploadLineNumbers) {
		this.uploadLineNumbers = uploadLineNumbers;
	}

    

  
    

}
