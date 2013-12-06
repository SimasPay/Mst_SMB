package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.RuleKeyDAO;
import com.mfino.dao.query.RuleKeyQuery;
import com.mfino.domain.RuleKey;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSRuleKeyComparision;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.RuleKeyComparisionProcessor;

@Service("RuleKeyComparisionProcessorImpl")
public class RuleKeyComparisionProcessorImpl extends BaseFixProcessor implements RuleKeyComparisionProcessor{

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSRuleKeyComparision realMsg = (CMJSRuleKeyComparision) msg;
		RuleKeyDAO dao = DAOFactory.getInstance().getRuleKeyDao();
		
		if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
			RuleKeyQuery query = new RuleKeyQuery();
			int i=0;
			
			if (realMsg.getRuleKeyID() != null) {
				query.setId(realMsg.getRuleKeyID());
			}				
			List<RuleKey> lst = dao.get(query);
			int total = 0;
			if (CollectionUtils.isNotEmpty(lst)) {
				String txnRuleKeyComparision = lst.get(0).getTxnRuleKeyComparision();
				if(txnRuleKeyComparision != null) {
					String [] comparisions = txnRuleKeyComparision.split(",");
					total = comparisions.length;
					realMsg.allocateEntries(total);
					for (String comparision: comparisions){
						CMJSRuleKeyComparision.CGEntries e = new CMJSRuleKeyComparision.CGEntries();
						e.setTxnRuleKeyComparision(comparision);
						realMsg.getEntries()[i] = e;
	        			i++;
	        		}
				}				
        	}        	
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(total);
		}	
		return realMsg;
	}
}
