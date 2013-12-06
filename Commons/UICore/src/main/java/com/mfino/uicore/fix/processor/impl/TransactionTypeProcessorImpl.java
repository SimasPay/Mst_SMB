package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.TransactionTypeDAO;
import com.mfino.dao.query.TransactionTypeQuery;
import com.mfino.domain.TransactionType;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSTransactionType;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.TransactionTypeProcessor;

@Service("TransactionTypeProcessorImpl")
public class TransactionTypeProcessorImpl extends BaseFixProcessor implements TransactionTypeProcessor{

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		log.info("TransactionTypeProcessor :: process()");
		
		CMJSTransactionType realMsg = (CMJSTransactionType) msg;
		TransactionTypeDAO dao = DAOFactory.getInstance().getTransactionTypeDAO();
		TransactionTypeQuery query = new TransactionTypeQuery();
		
        if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
        	
        } else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
			log.info("TransactionTypeProcessor :: select action");
			log.info("TransactionTypeProcessor :: realMsg.getServiceIDSearch() "+realMsg.getServiceIDSearch()); 
			
			// *FindbugsChange* 
			// Previous -- if((null != realMsg.getServiceIDSearch()) && !("".equals(realMsg.getServiceIDSearch()))){
			if((null != realMsg.getServiceIDSearch())) {
				query.setServiceId(Long.valueOf(realMsg.getServiceIDSearch()));
			}
			
			if (realMsg.getstart() != null) {
				query.setStart(realMsg.getstart());
			}
			if (realMsg.getlimit() != null) {
				query.setLimit(realMsg.getlimit());
			}
        	
			List<TransactionType> results = dao.get(query);
			realMsg.allocateEntries(results.size());

            for (int i = 0; i < results.size(); i++) {
            	TransactionType transactionType = results.get(i);

                CMJSTransactionType.CGEntries entry =   new CMJSTransactionType.CGEntries();

                updateMessage(transactionType, entry);
                realMsg.getEntries()[i] = entry;
            }
            
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());
            
            log.info("TransactionTypeProcessor :: query.getTotal() "+query.getTotal());
            
        } else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
                	
        } else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
        	
        }
		
		return realMsg;
	}
	
	 private void updateMessage(TransactionType t, CMJSTransactionType.CGEntries e) {
		 e.setID(t.getID());
		 e.setMSPID(t.getmFinoServiceProviderByMSPID().getID());
		 if (t.getDisplayName() != null) {
			 e.setTransactionName(t.getDisplayName());
		 }
	 }
}
