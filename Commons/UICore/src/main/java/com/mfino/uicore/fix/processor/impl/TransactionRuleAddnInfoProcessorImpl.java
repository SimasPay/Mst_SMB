package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.TransactionRuleAddnInfoDAO;
import com.mfino.dao.TransactionRuleDAO;
import com.mfino.dao.query.TransactionRuleAddnInfoQuery;
import com.mfino.domain.TransactionRuleAddnInfo;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSTxnRuleAddnInfo;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.TransactionRuleAddnInfoProcessor;

@Service("TransactionRuleAddnInfoProcessorImpl")
public class TransactionRuleAddnInfoProcessorImpl extends BaseFixProcessor implements TransactionRuleAddnInfoProcessor{

	private static Logger log = LoggerFactory.getLogger(TransactionRuleAddnInfoProcessor.class);
	
	private void updateMessage(TransactionRuleAddnInfo txnRule, CMJSTxnRuleAddnInfo.CGEntries e) {
		e.setID(txnRule.getID());
		if(txnRule.getTransactionRule() != null) {
			e.setTransactionRuleID(txnRule.getTransactionRule().getID());
		}		
		if(txnRule.getTxnRuleKey() != null) {
			e.setTxnRuleKey(txnRule.getTxnRuleKey());
		}
		if(txnRule.getTxnRuleValue() != null) {
			e.setTxnRuleValue(txnRule.getTxnRuleValue());
		}
		if(txnRule.getTxnRuleComparator() != null) {
			e.setTxnRuleComparator(txnRule.getTxnRuleComparator());
		}
		e.setRecordVersion(txnRule.getVersion());
	}
	
	private void updateEntity(TransactionRuleAddnInfo txnRule, CMJSTxnRuleAddnInfo.CGEntries e) {
		TransactionRuleDAO trDAO = DAOFactory.getInstance().getTransactionRuleDAO();		
		if(e.getTransactionRuleID() != null) {
			txnRule.setTransactionRule(trDAO.getById(e.getTransactionRuleID()));
		}
		if(StringUtils.isNotBlank(e.getTxnRuleKey())) {
			txnRule.setTxnRuleKey(e.getTxnRuleKey());
		}
		if(StringUtils.isNotBlank(e.getTxnRuleValue())) {
			txnRule.setTxnRuleValue(e.getTxnRuleValue());
		}
		if(StringUtils.isNotBlank(e.getTxnRuleComparator())) {
			txnRule.setTxnRuleComparator(e.getTxnRuleComparator());
		} else { //by default set to 'Equal'
			txnRule.setTxnRuleComparator("Equal");
		}
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSTxnRuleAddnInfo realMsg = (CMJSTxnRuleAddnInfo) msg;
		TransactionRuleAddnInfoDAO dao = DAOFactory.getInstance().getTransactionRuleAddnInfoDao();
		
		if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
			TransactionRuleAddnInfoQuery query = new TransactionRuleAddnInfoQuery();
			int i=0;
			
			if (realMsg.getTransactionRuleID() != null) {
				query.setTransactionRuleID(realMsg.getTransactionRuleID());
			}				
			List<TransactionRuleAddnInfo> lst = dao.get(query);
			if (CollectionUtils.isNotEmpty(lst)) {
				realMsg.allocateEntries(lst.size());
				for (TransactionRuleAddnInfo txnRule: lst){
					CMJSTxnRuleAddnInfo.CGEntries e = new CMJSTxnRuleAddnInfo.CGEntries();
					updateMessage(txnRule, e);
					realMsg.getEntries()[i] = e;
        			i++;
        		}
        	}        	
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(query.getTotal());
		} else if (CmFinoFIX.JSaction_Insert.equals(realMsg.getaction())) {
			CMJSTxnRuleAddnInfo.CGEntries[] entries = realMsg.getEntries();			
			for (CMJSTxnRuleAddnInfo.CGEntries e: entries) {
				TransactionRuleAddnInfo txnRule = new TransactionRuleAddnInfo();
				updateEntity(txnRule, e);
				dao.save(txnRule);
				updateMessage(txnRule, e);
			}
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(entries.length);
		} else if (CmFinoFIX.JSaction_Update.equals(realMsg.getaction())) {
			CMJSTxnRuleAddnInfo.CGEntries[] entries = realMsg.getEntries();			
			for (CMJSTxnRuleAddnInfo.CGEntries e: entries) {
				TransactionRuleAddnInfo txnRule = dao.getById(e.getID());
        		if (!(e.getRecordVersion().equals(txnRule.getVersion()))) {
        			handleStaleDataException();
        		}        		
        		updateEntity(txnRule, e);
				dao.save(txnRule);
        		updateMessage(txnRule, e);
        	}        	
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(entries.length);
			
		} else if (CmFinoFIX.JSaction_Delete.equals(realMsg.getaction())) {
			CMJSTxnRuleAddnInfo.CGEntries[] entries = realMsg.getEntries();
			for (CMJSTxnRuleAddnInfo.CGEntries e: entries) {
				dao.deleteById(e.getID());
			}			
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);			
		}		
		return realMsg;
	}
}
