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
public class BulkUploadFileQuery extends BaseQuery{

    private Date _startDate;
    private Date _endDate;
    private Integer _uploadFileStatusSearch;

    public Integer getUploadFileStatusSearch() {
        return _uploadFileStatusSearch;
    }

    public void setUploadFileStatusSearch(Integer _uploadFileStatusSearch) {
        this._uploadFileStatusSearch = _uploadFileStatusSearch;
    }

//    public Long getID() {
//        return _ID;
//    }
//
//    public void setID(Long _ID) {
//        this._ID = _ID;
//    }

    public Integer getRecordType() {
        return _RecordType;
    }

    public void setRecordType(Integer _RecordType) {
        this._RecordType = _RecordType;
    }

    public Integer getUploadStatus() {
        return _UploadStatus;
    }

    public void setUploadStatus(Integer _UploadStatus) {
        this._UploadStatus = _UploadStatus;
    }

    public boolean isAssociationOrdered() {
        return _associationOrdered;
    }

    public void setAssociationOrdered(boolean _associationOrdered) {
        this._associationOrdered = _associationOrdered;
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
    private Integer _RecordType;
//    private Long _ID;
    private Integer _UploadStatus;
    private boolean _associationOrdered;

}
