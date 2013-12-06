package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.TransactionRuleDAO;
import com.mfino.dao.query.TransactionRuleQuery;
import com.mfino.domain.TransactionRule;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSCheckTransactionRule;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.CheckTransactionRuleProcessor;

/**
 *
 * @author Bala Sunku
 */
@Service("CheckTransactionRuleProcessorImpl")
public class CheckTransactionRuleProcessorImpl extends BaseFixProcessor implements CheckTransactionRuleProcessor{

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) {
    	boolean duplicate = false;
        CMJSCheckTransactionRule realMsg = (CMJSCheckTransactionRule) msg;
		TransactionRuleDAO dao = DAOFactory.getInstance().getTransactionRuleDAO();
		TransactionRuleQuery query = new TransactionRuleQuery();
		query.setServiceProviderId(realMsg.getServiceProviderID());
		query.setServiceId(realMsg.getServiceID());
		query.setTransactionTypeId(realMsg.getTransactionTypeID());
		query.setChannelCodeId(realMsg.getChannelCodeID());
		query.setSourceGroup(realMsg.getSourceGroup());
		query.setDestinationGroup(realMsg.getDestinationGroup());
		query.setExactMatch(true);
		List<TransactionRule> lst = dao.get(query);
		if (CollectionUtils.isNotEmpty(lst) && lst.size() > 0 ) {
			duplicate = true;
		}

        CMJSError err=new CMJSError();

        if(duplicate){
            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            err.setErrorDescription(MessageText._("Transaction Rule is already defined for given details."));
        }else {
            err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
            err.setErrorDescription(MessageText._(""));
        }

        return err;
    }
    
}

