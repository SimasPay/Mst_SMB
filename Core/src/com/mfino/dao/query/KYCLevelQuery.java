/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao.query;


/**
 *
 * @author Sanjeev
 */
public class KYCLevelQuery extends BaseQuery {

    
    private Integer _KYCLevel;
    private String _KYCLevelName;
        
    public Integer getKycLevel() {
        return _KYCLevel;
    }

    public void set_KycLevel(Integer _KycLevel) {
        this._KYCLevel = _KycLevel;
    }


    public String getKycLevelName() {
        return _KYCLevelName;
    }

    public void set_KycLevelName(String _KycLevelName) {
        this._KYCLevelName = _KycLevelName;
    }

}
