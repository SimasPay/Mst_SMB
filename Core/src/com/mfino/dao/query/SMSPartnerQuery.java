/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

import java.util.Date;

/**
 *
 * @author Srinu
 */
public class SMSPartnerQuery extends BaseQuery{

    private String _PartnerName;
    private Date _StartDate;

    public Date getEndDate() {
        return _EndDate;
    }

    public void setEndDate(Date _EndDate) {
        this._EndDate = _EndDate;
    }

    public Date getStartDate() {
        return _StartDate;
    }

    public void setStartDate(Date _StartDate) {
        this._StartDate = _StartDate;
    }
    private Date _EndDate;

    public String getPartnerName() {
        return _PartnerName;
    }

    public void setPartnerName(String _PartnerName) {
        this._PartnerName = _PartnerName;
    }

}
