/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao.query;

/**
 *
 * @author Raju
 */
public class RegionQuery extends BaseQuery {

    private Long _regionID;
    private Long _companyId;
    private String _regionName;
    private String _regionCode;

    public String getRegionCode() {
        return _regionCode;
    }

    public void setRegionCode(String _regionCode) {
        this._regionCode = _regionCode;
    }

    public Long getCompanyId() {
        return _companyId;
    }

    public void setCompanyId(Long _companyId) {
        this._companyId = _companyId;
    }

    public String getRegionName() {
        return _regionName;
    }

    public void setRegionName(String _regionName) {
        this._regionName = _regionName;
    }
      
    public Long getRegionID() {
        return _regionID;
    }

    public void setRegionID(Long _regionID) {
        this._regionID = _regionID;
    }
}
