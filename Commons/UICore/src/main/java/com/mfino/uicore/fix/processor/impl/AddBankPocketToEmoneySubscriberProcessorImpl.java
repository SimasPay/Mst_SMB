package com.mfino.uicore.fix.processor.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberUpgradeDataDAO;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberUpgradeData;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSAddBankPocketToEmoneySubscriber;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.PocketService;
import com.mfino.uicore.fix.processor.AddBankPocketToEmoneySubscriberProcessor;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.util.ConfigurationUtil;

@Service("AddBankPocketToEmoneySubscriberProcessorImpl")
public class AddBankPocketToEmoneySubscriberProcessorImpl extends BaseFixProcessor implements
AddBankPocketToEmoneySubscriberProcessor {
	
	private SubscriberUpgradeDataDAO subscriberUpgradeDataDAO = DAOFactory.getInstance().getSubscriberUpgradeDataDAO();

	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		log.info("In AddBankPocketToEmoneySubscriberProcessorImpl Process method");
		
		CMJSAddBankPocketToEmoneySubscriber realMsg = (CMJSAddBankPocketToEmoneySubscriber) msg;
		CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
		error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		
		
        
        int count=  subscriberUpgradeDataDAO.getCountByMdnId(realMsg.getMDNID());
        
		if(count == 0) {
			Pocket bankpocketchecking= pocketService.getByCardPan(realMsg.getAccountNumber());
			if(bankpocketchecking==null){
			
			SubscriberUpgradeData subscriberUpgradeData=new SubscriberUpgradeData();
			subscriberUpgradeData.setMdnId(realMsg.getMDNID());
			subscriberUpgradeData.setBankAccountNumber(realMsg.getAccountNumber());
			subscriberUpgradeData.setApplicationId(realMsg.getApplicationID());
			subscriberUpgradeData.setCreatedby(getLoggedUserName());
			subscriberUpgradeData.setCreatetime(new Timestamp());
			subscriberUpgradeData.setSubActivity(CmFinoFIX.SubscriberActivity_Enable_MBanking_For_Emoney_Subscriber);
			subscriberUpgradeData.setSubsActivityStatus(CmFinoFIX.SubscriberActivityStatus_Initialized);
			subscriberUpgradeDataDAO.save(subscriberUpgradeData);
			log.info("AddBankPocketToEmoneySubscriberProcessorImpl Process method Completed");
			realMsg.setsuccess(Boolean.TRUE);
        	return realMsg;
			}else{
				error.setErrorDescription(MessageText._("Entered AccountNumber already exits in the system.Your request cannot be processed"));
	    		error.setsuccess(Boolean.FALSE);
	        	return error;
			}
		} else{
    		error.setErrorDescription(MessageText._(ConfigurationUtil.getSubscriberActivityActiveMessage()));
    		error.setsuccess(Boolean.FALSE);
        	return error;
    	}
		
	}
}
