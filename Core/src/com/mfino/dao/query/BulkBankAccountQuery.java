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
public class BulkBankAccountQuery extends BaseQuery {

    private Date _startDate;
    private Date _endDate;
    private Date _lastUpdatedDateTime;
    private Boolean _isStatusUploadToBankOrCompleted;

    public Boolean isStatusUploadToBankOrCompleted() {
        return _isStatusUploadToBankOrCompleted;
    }

    public void setIsStatusUploadToBankOrCompleted(Boolean _isStatusUploadToBankOrCompleted) {
        this._isStatusUploadToBankOrCompleted = _isStatusUploadToBankOrCompleted;
    }

    public Date getLastUpdateDateTime() {
        return _lastUpdatedDateTime;
    }

    public void setLastUpdatedDateTime(Date _lastUpdatedDateTime) {
        this._lastUpdatedDateTime = _lastUpdatedDateTime;
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
}
