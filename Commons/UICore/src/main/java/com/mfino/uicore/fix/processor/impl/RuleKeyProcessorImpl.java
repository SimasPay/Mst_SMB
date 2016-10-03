package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.RuleKeyDAO;
import com.mfino.dao.query.RuleKeyQuery;
import com.mfino.domain.RuleKey;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSRuleKey;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.RuleKeyProcessor;

@Service("RuleKeyProcessorImpl")
public class RuleKeyProcessorImpl extends BaseFixProcessor implements RuleKeyProcessor{

	private static Logger log = LoggerFactory.getLogger(RuleKeyProcessor.class);
	
	private void updateMessage(RuleKey ruleKey, CMJSRuleKey.CGEntries e) {
		if(ruleKey.getId() != null) {
			e.setID(ruleKey.getId().longValue());
		}
		if(ruleKey.getTxnrulekey() != null) {
			e.setTxnRuleKey(ruleKey.getTxnrulekey());
		}		
		if(ruleKey.getTxnrulekeycomparision() != null) {
			e.setTxnRuleKeyComparision(ruleKey.getTxnrulekeycomparision());
		}			
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSRuleKey realMsg = (CMJSRuleKey) msg;
		RuleKeyDAO dao = DAOFactory.getInstance().getRuleKeyDao();
		
		if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
			RuleKeyQuery query = new RuleKeyQuery();
			int i=0;
			
			if (realMsg.getServiceID() != null) {
				query.setServiceID(realMsg.getServiceID());
			}
			if (realMsg.getTransactionTypeID() != null) {
				query.setTransactionTypeID(realMsg.getTransactionTypeID());
			}
			if (realMsg.getTxnRuleKeyType() != null) {
				query.setTxnRuleKeyType(realMsg.getTxnRuleKeyType());
			}					
			List<RuleKey> lst = dao.get(query);
			if (CollectionUtils.isNotEmpty(lst)) {
				realMsg.allocateEntries(lst.size());
				for (RuleKey ruleKey: lst){
					CMJSRuleKey.CGEntries e = new CMJSRuleKey.CGEntries();
					updateMessage(ruleKey, e);
					realMsg.getEntries()[i] = e;
        			i++;
        		}
        	}        	
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(query.getTotal());
		} 		
		return realMsg;
	}
}
