package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.FundEventsDAO;
import com.mfino.dao.query.FundEventsQuery;
import com.mfino.domain.FundEvents;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSFundEvents;
import com.mfino.fix.CmFinoFIX.CMJSFundEvents.CGEntries;
import com.mfino.service.EnumTextService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.FundEventsProcessor;

@Service("FundEventsProcessorImpl")
public class FundEventsProcessorImpl extends BaseFixProcessor implements FundEventsProcessor{
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSFundEvents realMsg = (CMJSFundEvents) msg;
		FundEventsDAO fundEventsDAO = DAOFactory.getInstance().getFundEventsDAO();
		
		if(CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())){
			
		}
		else if(CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())){
			FundEventsQuery query = new FundEventsQuery();
			if(realMsg.getstart() != null){
				query.setStart(realMsg.getstart());
			}
			if(realMsg.getlimit() != null)
			{
				query.setLimit(realMsg.getlimit());
			}
			List<FundEvents> results = fundEventsDAO.get(query);
			realMsg.allocateEntries(results.size());
			for (int i = 0; i < results.size(); i++) {
				FundEvents fundEvents = results.get(i);
				CMJSFundEvents.CGEntries entry = new CMJSFundEvents.CGEntries();
				updateMessage(fundEvents, entry);
				realMsg.getEntries()[i] = entry;
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(query.getTotal());
			return realMsg;
		}
		else if(CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())){
			
		}
		else if(CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())){
			
		}
		return realMsg;

	}

	private void updateMessage(FundEvents fundEvents, CGEntries e) {
		e.setOnFailedAttemptsExceeded(fundEvents.getId().longValue());
		e.setOnFailedAttemptsExceededText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_FundEventType, null, fundEvents.getFundeventtype()));
		e.setOnFundAllocationTimeExpiry(fundEvents.getId().longValue());
		e.setOnFundAllocationTimeExpiryText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_FundEventType, null, fundEvents.getFundeventtype()));
		if(!CmFinoFIX.FundEventType_Reversal.equals(fundEvents.getFundeventtype())){
			e.setGenerationOfOTPOnFailure(fundEvents.getId().longValue());
			e.setGenerationOfOTPOnFailureText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_FundEventType, null, fundEvents.getFundeventtype()));
		}
		
				
	}

}
