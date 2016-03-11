package com.mfino.uicore.fix.processor.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.domain.ChannelCode;
import com.mfino.exceptions.InvalidDataException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSAgentClosingInquiry;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.i18n.MessageText;
import com.mfino.service.ChannelCodeService;
import com.mfino.transactionapi.constants.ApiConstants;
import com.mfino.transactionapi.handlers.agent.AgentClosingInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.SubscriberAccountClosingXMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.uicore.fix.processor.AgentClosingInquiryProcessor;
import com.mfino.uicore.fix.processor.BaseFixProcessor;

@Service("AgentClosingInquiryProcessorImpl")
public class AgentClosingInquiryProcessorImpl extends BaseFixProcessor implements AgentClosingInquiryProcessor{
	
	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private ChannelCodeService channelCodeService;
	
	
	@Autowired
	@Qualifier("AgentClosingInquiryHandlerImpl")
	private AgentClosingInquiryHandler agentClosingInquiryHandler;
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception  {
		
		CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
		CMJSAgentClosingInquiry realMsg = (CMJSAgentClosingInquiry) msg;
		
		errorMsg.setsuccess(false);

		if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {

		} else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
			

		} else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
			
			log.info("Procesing the Close Account Request for " + realMsg.getDestMDN());
			
			if (realMsg.getDestMDN() == null ) {
				log.error("Dest MDN is null");
				errorMsg.setErrorDescription(MessageText._("Invalid Agent MDN"));
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				return errorMsg;
			}
			
			errorMsg = validateAgentClosingInquiry(realMsg,errorMsg);
			
			if(CmFinoFIX.ErrorCode_Generic.equals(errorMsg.getErrorCode())){
				return errorMsg;
			}
			
			return errorMsg;

		} else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {

		}

		return realMsg;
	}
	
	private CMJSError validateAgentClosingInquiry(CMJSAgentClosingInquiry realMsg, CMJSError errorMsg) throws InvalidDataException {
		
		
		TransactionDetails td =new TransactionDetails();
		
		ChannelCode channelCode = getChannelCode(String.valueOf(CmFinoFIX.SourceApplication_WebAPI));
		td.setCc(channelCode);
		td.setDestMDN(realMsg.getDestMDN());
		td.setSystemIntiatedTransaction(true);
		
		SubscriberAccountClosingXMLResult result = (SubscriberAccountClosingXMLResult)agentClosingInquiryHandler.handle(td);
		
		if(null != result) {
			
			if(String.valueOf(CmFinoFIX.NotificationCode_AgentClosingInquirySuccess).equals(result.getCode())) {
			
				errorMsg.setsuccess(true);
				errorMsg.setTransferID(result.getSctlID());
				errorMsg.setDestMDN(realMsg.getDestMDN());
				errorMsg.setDestinationUserName(result.getName());
				
			} else if(String.valueOf(CmFinoFIX.NotificationCode_SubscriberHasAccountBalance).equals(result.getCode())) {
				
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
				errorMsg.setErrorDescription("Transaction Failed. Agent balance is available.");
				
			} else if(String.valueOf(CmFinoFIX.NotificationCode_MDNIsNotActive).equals(result.getCode())) {
				
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
				errorMsg.setErrorDescription("Transaction Failed. Agent is not active.");
				
			}  else {
				
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
				errorMsg.setErrorDescription("Transaction Failed. Please retry once again");
			}
		}
		
		return errorMsg;
	}
	
	private ChannelCode getChannelCode(String channelCode) throws InvalidDataException{
		
		ChannelCode cc = channelCodeService.getChannelCodeByChannelCode(channelCode);
		
		if(cc==null){
			throw new InvalidDataException("Invalid ChannelID", CmFinoFIX.NotificationCode_InvalidData, ApiConstants.PARAMETER_CHANNEL_ID);
		}
		
		return cc;
	}
}