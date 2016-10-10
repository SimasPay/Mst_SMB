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
import com.mfino.dao.KYCLevelDAO;
import com.mfino.domain.KycLevel;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSKYCCheck;
import com.mfino.fix.CmFinoFIX.CMJSKYCCheck.CGEntries;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.KYCProcessor;

/**
 *
 * @author sanjeev
 */

@Service("KYCProcessorImpl")
public class KYCProcessorImpl extends BaseFixProcessor implements KYCProcessor{

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CMJSKYCCheck realMsg = (CMJSKYCCheck) msg;
    	
        KYCLevelDAO kycleveldao = DAOFactory.getInstance().getKycLevelDAO();
      
        
        if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
//            CMJSKYCCheck.CGEntries[] entries = realMsg.getEntries();
//        	
//            for (CMJSKYCCheck.CGEntries e : entries) {
//                KYCLevel kyclevel = kycleveldao.getById(e.getKYCLevelID());
//               // KYCField kycfield = kycfieldsdao.getById(e.)            	
//                // Check for Stale Data
//                
//               //if(!e.getRecordVersion().equals(bank.getVersion()))
//               //{
//               //     handleStaleDataException();
//               //}
//
//               // updateEntity(bank, e);
//               // bankdao.save(bank);
//                //updateMessage(bank, e);
//              
//            }
//
//            realMsg.setsuccess(CmFinoFIX.Boolean_True);
//            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
            
        	List<KycLevel> results = kycleveldao.getAll();
        	realMsg.allocateEntries(results.size());
            for (int i = 0; i < results.size(); i++) {
                KycLevel kyclevel =results.get(i);
                CMJSKYCCheck.CGEntries entry =  new CMJSKYCCheck.CGEntries();
                updateMessage(kyclevel, entry);
                realMsg.getEntries()[i] = entry;
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
          realMsg.settotal(realMsg.getEntries().length);
        }
        /* else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
            CMJSKYCCheck.CGEntries[] entries = realMsg.getEntries();

            for (CMJSKYCCheck.CGEntries e : entries) {
                Bank bank = new Bank();
                updateEntity(bank, e);
                bankdao.save(bank);
                updateMessage(bank, e);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        }else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
        }*/
        return realMsg;
    }

	private void updateMessage(KycLevel kyclevel, CGEntries entry) {
		entry.setID(kyclevel.getId().longValue());
		if(kyclevel.getKyclevel()!=null){
			entry.setKYCLevel(kyclevel.getKyclevel().longValue());
		}
		if(kyclevel.getKyclevelname()!=null){
			entry.setKYCLevelName(kyclevel.getKyclevelname());
		}
		
	}

//    public void updateEntity(KYCLevel kyclevel, CMJSKYCCheck.CGEntries e) {
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
//    public void updateMessage(KYCLevel kyclevel, CMJSKYCCheck.CGEntries e) {
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

