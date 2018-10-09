package com.mfino.uicore.fix.processor.impl;

import org.springframework.stereotype.Service;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSAuditLog;
import com.mfino.fix.CmFinoFIX.CMJSUsers;
import com.mfino.hibernate.Timestamp;
import com.mfino.uicore.fix.processor.AuditLogProcessor;
import com.mfino.uicore.fix.processor.BaseFixProcessor;

import com.mfino.domain.AuditLog;
import java.util.List;

import com.mfino.dao.query.AuditLogQuery;
import com.mfino.dao.AuditLogDAO;
import com.mfino.dao.DAOFactory;

@Service("AuditLogProcessorImpl")
public class AuditLogProcessorImpl extends BaseFixProcessor implements AuditLogProcessor {

	@Override
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		log.info("@kris DumpFields:"+msg.DumpFields());

		CMJSAuditLog realMsg = (CMJSAuditLog) msg;
		log.info("@kris FieldName_CreatedBySearch:"+realMsg.getCreatedBySearch());
		
//		log.info("@kris: CMJSAuditLog DumpFields:"+realMsg.DumpFields());
		
//		CMJSUsers realMsg = (CMJSUsers) msg;
//		log.info("@kris: CMJSUsers DumpFields:"+realMsg.DumpFields());
//		log.info("@kris: realMsg.getaction(): "+realMsg.getaction());
		
		AuditLogDAO dao = DAOFactory.getInstance().getAuditLogDAO();
		AuditLogQuery query=new AuditLogQuery();
		
		
		if(realMsg.getCreatedBySearch() != null){

			query.setCreatedBy(realMsg.getCreatedBySearch());
		}
		
		List<AuditLog>results=dao.get(query);
		
		if (results != null) {
			log.info("@kris AuditLog results is not null");
			realMsg.allocateEntries(results.size());
			for (int i = 0; i <results.size(); i++) {
				CMJSAuditLog.CGEntries entry = new CMJSAuditLog.CGEntries();
				AuditLog al = results.get(i);
	        	entry.setRecordID(al.getId());
	        	entry.setAction(al.getJsaction());
	        	entry.setCreatedBy(al.getCreatedby());
	        	entry.setCreateTime(al.getCreatetime());
	        	entry.setRecordVersion(al.getVersion());
	        	String entity=al.getMessagename().replace("com.mfino.fix.CmFinoFIX$CMJS","");
	        	entry.setMessageName(entity);
	            realMsg.getEntries()[i] = entry;
			}
		}
		realMsg.setsuccess(CmFinoFIX.Boolean_True);
        realMsg.settotal(query.getTotal());
        
		/*
		// Ubah dari hasil query
		//realMsg.allocateEntries(5);
        for (int i = 0; i < 5; i++) {
        	CMJSAuditLog.CGEntries entry = new CMJSAuditLog.CGEntries();
        	entry.setRecordID(Long.valueOf(i+""));
        	entry.setAction("default");
        	entry.setCreatedBy("user"+i);
        	entry.setCreateTime(new Timestamp());
        	entry.setRecordVersion(i);
        	entry.setMessageName("com.mfino.fix.CmFinoFIX$CMJSSubscriberEdit");
            realMsg.getEntries()[i] = entry;
        }
        realMsg.setsuccess(CmFinoFIX.Boolean_True);
        realMsg.settotal(5);
        */
		return realMsg;
	}

}
