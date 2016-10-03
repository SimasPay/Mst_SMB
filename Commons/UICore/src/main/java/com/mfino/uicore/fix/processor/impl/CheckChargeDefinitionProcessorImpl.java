package com.mfino.uicore.fix.processor.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.ChargeDefinitionDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.TransactionRuleDAO;
import com.mfino.domain.ChargeDefinition;
import com.mfino.domain.TransactionRule;
import com.mfino.domain.TransactionType;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSCheckChargeDefinition;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.CheckChargeDefinitionProcessor;

/**
 *
 * @author Bala Sunku
 */
@Service("CheckChargeDefinitionProcessorImpl")
public class CheckChargeDefinitionProcessorImpl extends BaseFixProcessor implements CheckChargeDefinitionProcessor{

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) {
    	boolean isError = false;
    	CMJSCheckChargeDefinition realMsg = (CMJSCheckChargeDefinition) msg;
		ChargeDefinitionDAO cdDAO = DAOFactory.getInstance().getChargeDefinitionDAO();
		TransactionRuleDAO trDAO = DAOFactory.getInstance().getTransactionRuleDAO();
		ChargeDefinition cd = cdDAO.getById(realMsg.getChargeDefinitionID());
		TransactionRule tr = trDAO.getById(realMsg.getTransactionRuleID());
		if(cd!=null && tr!=null)
		{
			TransactionType tt = tr.getTransactionType();
			if (tt!=null && "SubscriberRegistration".equals(tt.getTransactionname()) && (cd.getIschargefromcustomer() != 0) ){
				isError = true;
			} else {
				isError = false;
			}
		}

        CMJSError err=new CMJSError();
        if(isError){
            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            err.setErrorDescription(MessageText._("Please add the Charge Definition with Fixed Calculation type and Charge from Funding Partner."));
        }else {
            err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
            err.setErrorDescription(MessageText._(""));
        }

        return err;
    }

}

