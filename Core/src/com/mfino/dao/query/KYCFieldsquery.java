/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao.query;

import com.mfino.dao.KYCLevelDAO;
import com.mfino.domain.KycLevel;
public class KYCFieldsquery extends BaseQuery {

    KYCLevelDAO kycLevelDAO = new KYCLevelDAO();
    private Long _KYCFieldsLevelID;
    private String _KYCFieldsName;
    private KycLevel kycLevel;
        
   
	public Long getkycFieldsLevelID() {
        return _KYCFieldsLevelID;
    }
        

    public void set_kycFieldsLevelID(Long _kycFieldsLevelID) {
        this._KYCFieldsLevelID = _kycFieldsLevelID;
    }


    public String getKycFieldsName() {
        return _KYCFieldsName;
    }

    public void set_KycFieldsName(String _KycFieldsName) {
        this._KYCFieldsName = _KycFieldsName;
    }


	public void setKycLevel(KycLevel kycLevel) {
		this.kycLevel = kycLevel;
	}


	public KycLevel getKycLevel() {
		return kycLevel;
	}

}
