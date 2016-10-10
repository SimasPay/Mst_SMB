/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.KYCFieldsDAO;
import com.mfino.dao.query.KYCFieldsquery;
import com.mfino.domain.KycFields;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSKYCCheckFields;
import com.mfino.fix.CmFinoFIX.CMJSKYCCheckFields.CGEntries;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.KYCFieldsPrcocessor;

/**
 *
 * @author sanjeev
 */

@Service("KYCFieldsPrcocessorImpl")
public class KYCFieldsPrcocessorImpl extends BaseFixProcessor implements KYCFieldsPrcocessor {
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) throws Exception {
        
    	CMJSKYCCheckFields realMsg = (CMJSKYCCheckFields) msg;
    	
        KYCFieldsDAO kycfieldsdao = DAOFactory.getInstance().getKYCFieldsDAO();
    	    
        
        if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {} 
        else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
            
            KYCFieldsquery query = new KYCFieldsquery();
            query.set_kycFieldsLevelID(realMsg.getKYCFieldsLevelID());
            
        	List<KycFields> results = kycfieldsdao.get(query);
        	realMsg.allocateEntries(results.size());
            for (int i = 0; i < results.size(); i++) {
            	KycFields kycfields =results.get(i);
            	CMJSKYCCheckFields.CGEntries entry =  new CMJSKYCCheckFields.CGEntries();
                updateMessage(kycfields, entry);
                realMsg.getEntries()[i] = entry;
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
          realMsg.settotal(results.size());
        } 
        return realMsg;
    }

	private void updateMessage(KycFields kycfields, CGEntries entry) {
		
		if(kycfields.getKycLevel()!=null){
			//entry.setKYCFieldsLevelID(kycfields.getKYCLevelByKYCFieldsLevelID().getID());
	        entry.setKYCFieldsLevelID(kycfields.getKycLevel().getId().longValue());		
		}

		if(kycfields.getKycfieldsname()!=null){
			entry.setKYCFieldsName(kycfields.getKycfieldsname());
		}
		
		
		
	}

//    public void updateEntity(KYCLevel kyclevel, CMJSKYCFieldCheck.CGEntries e) {
//
//        if (e.getKYCLevel()!=null){
//        	kyclevel.setKYCLevel(e.getKYCLevelID());
//     
//        }
//        if (e.getKycLevelName() != null) {
//        	kyclevel.setKYCLevelName(e.getKycLevelName());
//        }
//     }
//
//    public void updateMessage(KYCLevel kyclevel, CMJSKYCFieldCheck.CGEntries e) {
//
//    	if (e.getKYCLevelID()!=null){
//        	kyclevel.setKYCLevel(e.getKYCLevelID());
//              
//        }
//        if ( e.getKycLevelName() != null) {
//        	kyclevel.setKYCLevelName(e.getKycLevelName());
//        }
//      }
}

