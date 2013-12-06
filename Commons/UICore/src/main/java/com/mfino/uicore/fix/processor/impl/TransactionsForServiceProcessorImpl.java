package com.mfino.uicore.fix.processor.impl;

import java.util.Iterator;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.ServiceTransactionDAO;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSTransactionsForService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.TransactionsForServiceProcessor;

@Service("TransactionsForServiceProcessorImpl")
public class TransactionsForServiceProcessorImpl extends BaseFixProcessor implements TransactionsForServiceProcessor{

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSTransactionsForService realMsg = (CMJSTransactionsForService) msg;
		ServiceTransactionDAO dao = DAOFactory.getInstance().getServiceTransactionDAO();
    	int size = 0;
    	int i = 0;
		
		Map<Long, String> map = dao.getTransactions(realMsg.getServiceID());
    	size = map.size();
    	
    	realMsg.allocateEntries(size);
    	
    	Iterator<Long> it = map.keySet().iterator();
    	while (it.hasNext()) {
    		CMJSTransactionsForService.CGEntries e = new CMJSTransactionsForService.CGEntries();
    		Long ttId = it.next();
    		e.setTransactionTypeID(ttId);
    		e.setTransactionName(map.get(ttId));
    		realMsg.getEntries()[i] = e;
    		i++;
    	}

    	realMsg.setsuccess(CmFinoFIX.Boolean_True);
    	realMsg.settotal(size);
		
		return realMsg;
	}
}
