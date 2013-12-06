package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.TransactionChargeDAO;
import com.mfino.dao.query.TransactionChargeQuery;
import com.mfino.domain.TransactionCharge;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSCheckTransactionCharge;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.CheckTransactionChargeProcessor;

/**
 *
 * @author Bala Sunku
 */
@Service("CheckTransactionChargeProcessorImpl")
public class CheckTransactionChargeProcessorImpl extends BaseFixProcessor implements CheckTransactionChargeProcessor{

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) {
    	boolean duplicate = false;
        CMJSCheckTransactionCharge realMsg = (CMJSCheckTransactionCharge) msg;
		TransactionChargeDAO tcDAO = DAOFactory.getInstance().getTransactionChargeDAO();
		TransactionChargeQuery query = new TransactionChargeQuery();
		query.setTransactionRuleId(realMsg.getTransactionRuleID());
		query.setChargeTypeId(realMsg.getChargeTypeID());
		List<TransactionCharge> lst = tcDAO.get(query);
		if (CollectionUtils.isNotEmpty(lst) && lst.size() > 0 ) {
			duplicate = true;
		}

        CMJSError err=new CMJSError();

        if(duplicate){
            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            err.setErrorDescription(MessageText._("Transaction Charge is already defined for Transaction Rule and Charge Type."));
        }else {
            err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
            err.setErrorDescription(MessageText._(""));
        }

        return err;
    }

}

