/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao.query;

import java.util.Date;

/**
 *
 * @author srinu
 */
public class LOPQuery extends BaseQuery {

    private Long _lopid;
    private Date _startDate;
    private Long _mdnid;
    private String _userName;
    private String _dctName;
    private Boolean _CommissionChanged;

    public Boolean isCommissionChanged() {
        return _CommissionChanged;
    }

    public void setCommissionChanged(Boolean _CommissionChanged) {
        this._CommissionChanged = _CommissionChanged;
    }

    public String getDctName() {
        return _dctName;
    }

    public void setDctName(String _dctName) {
        this._dctName = _dctName;
    }


    public Long getMdnid() {
        return _mdnid;
    }

    public void setMdnid(Long _mdnid) {
        this._mdnid = _mdnid;
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
    private Date _endDate;

    public String getDistributorname() {
        return _distributorname;
    }

    public void setDistributorname(String _distributorname) {
        this._distributorname = _distributorname;
    }

    public Long getLopid() {
        return _lopid;
    }

    public void setLopid(Long _lopid) {
        this._lopid = _lopid;
    }

    public String getLopstatus() {
        return _lopstatus;
    }

    public void setLopstatus(String _lopstatus) {
        this._lopstatus = _lopstatus;
    }
    private String _distributorname;
    private String _lopstatus;
    private String _distributornameLike;

    public String getDistributornameLike() {
        return _distributornameLike;
    }

    public void setDistributornameLike(String _distributornameLike) {
        this._distributornameLike = _distributornameLike;
    }

    public void setUserName(String _userName) {
        this._userName = _userName;
    }

    public String getUserName() {
        return _userName;
    }


}
