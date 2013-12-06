/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

/**
 *
 * @author Diwakar
 */
public class CompanyQuery extends BaseQuery {
    private String _companyCode;
    private String _companyName;

    public String getCompanyCode() {
        return _companyCode;
    }

    public void setCompanyCode(String _companyCode) {
        this._companyCode = _companyCode;
    }

    public String getCompanyName() {
        return _companyName;
    }

    public void setCompanyName(String _companyName) {
        this._companyName = _companyName;
    }
}
