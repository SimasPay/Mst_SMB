/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

import java.util.Date;
/**
 *
 * @author Maruthi
 */
public class BillerQuery extends BaseQuery  {
  private String _billerName;
    private Integer _bankcode;
    private Integer _billerCode;
    private String _billerType;
    private Date _startDate;

    public Integer getBankcode() {
        return _bankcode;
    }

    public void setBankcode(Integer _bankcode) {
        this._bankcode = _bankcode;
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

    public void setBillerName(String _billerName) {
        this._billerName = _billerName;
    }

    public String getBillerName() {
        return _billerName;
    }

    public void setBillerCode(Integer _billerCode) {
        this._billerCode = _billerCode;
    }

    public Integer getBillerCode() {
        return _billerCode;
    }

    public void setBillerType(String _billerType) {
        this._billerType = _billerType;
    }

    public String getBillerType() {
        return _billerType;
    }
}
