package com.mfino.uicore.fix.processor.impl;

import org.springframework.stereotype.Service;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSAuditLog;
import com.mfino.hibernate.Timestamp;
import com.mfino.uicore.fix.processor.AuditLogProcessor;
import com.mfino.uicore.fix.processor.BaseFixProcessor;

@Service("AuditLogProcessorImpl")
public class AuditLogProcessorImpl extends BaseFixProcessor implements AuditLogProcessor {

	@Override
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSAuditLog realMsg = (CMJSAuditLog) msg;
		
		// Ubah dari hasil query
		realMsg.allocateEntries(5);

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
        
		return realMsg;
	}

}
