package com.mfino.uicore.fix.processor.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.ChargeDefinitionDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.TransactionChargeDAO;
import com.mfino.domain.ChargeDefinition;
import com.mfino.domain.TransactionCharge;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSCheckDependantChargeType;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.CheckDependantChargeTypeProcessor;

/**
 *
 * @author Bala Sunku
 */
@Service("CheckDependantChargeTypeProcessorImpl")
public class CheckDependantChargeTypeProcessorImpl extends BaseFixProcessor implements CheckDependantChargeTypeProcessor{

	 @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) {
    	boolean exists = false;
        CMJSCheckDependantChargeType realMsg = (CMJSCheckDependantChargeType) msg;
		ChargeDefinitionDAO cdDAO = DAOFactory.getInstance().getChargeDefinitionDAO();
		TransactionChargeDAO tcDAO = DAOFactory.getInstance().getTransactionChargeDAO();
		ChargeDefinition cd = cdDAO.getById(realMsg.getChargeDefinitionID());
		if (cd != null) {
			if (cd.getChargeTypeByDependantChargeTypeID() != null) {
				TransactionCharge tc = tcDAO.getTransactionCharge(realMsg.getTransactionRuleID(), 
						cd.getChargeTypeByDependantChargeTypeID().getID());
				exists = (tc != null && !(tc.getID().equals(realMsg.getTransactionChargeID()))) ? true : false;				
			} else {
				exists = true;
			}
		}

        CMJSError err=new CMJSError();

        if(!exists){
            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            err.setErrorDescription(MessageText._("Please define the Transaction charge for this rule with the Charge type '" + cd.getChargeTypeByDependantChargeTypeID().getName() + "'."));
        }else {
            err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
            err.setErrorDescription(MessageText._(""));
        }

        return err;
    }

}

