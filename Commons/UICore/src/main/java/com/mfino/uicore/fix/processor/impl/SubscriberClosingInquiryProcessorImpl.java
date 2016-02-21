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
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSSubscriberClosingInquiry;
import com.mfino.i18n.MessageText;
import com.mfino.service.ChannelCodeService;
import com.mfino.transactionapi.constants.ApiConstants;
import com.mfino.transactionapi.handlers.account.SubscriberClosingInquiryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.SubscriberAccountClosingXMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.SubscriberClosingInquiryProcessor;

@Service("SubscriberClosingInquiryProcessorImpl")
public class SubscriberClosingInquiryProcessorImpl extends BaseFixProcessor implements SubscriberClosingInquiryProcessor{
	
	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private ChannelCodeService channelCodeService;
	
	
	@Autowired
	@Qualifier("SubscriberClosingInquiryHandlerImpl")
	private SubscriberClosingInquiryHandler subscriberClosingInquiryHandler;
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception  {
		
		CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
		CMJSSubscriberClosingInquiry realMsg = (CMJSSubscriberClosingInquiry) msg;
		
		errorMsg.setsuccess(false);

		if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {

		} else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
			

		} else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
			
			log.info("Procesing the Close Account Request for " + realMsg.getDestMDN());
			
			if (realMsg.getDestMDN() == null ) {
				log.error("Dest MDN is null");
				errorMsg.setErrorDescription(MessageText._("Invalid Subscriber MDN"));
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				return errorMsg;
			}
			
			errorMsg = validateSubscriberClosingInquiry(realMsg,errorMsg);
			
			if(CmFinoFIX.ErrorCode_Generic.equals(errorMsg.getErrorCode())){
				return errorMsg;
			}
			
			return errorMsg;

		} else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {

		}

		return realMsg;
	}
	
	private CMJSError validateSubscriberClosingInquiry(CMJSSubscriberClosingInquiry realMsg, CMJSError errorMsg) throws InvalidDataException {
		
		
		TransactionDetails td =new TransactionDetails();
		
		ChannelCode channelCode = getChannelCode(String.valueOf(CmFinoFIX.SourceApplication_WebAPI));
		td.setCc(channelCode);
		td.setDestMDN(realMsg.getDestMDN());
		td.setSystemIntiatedTransaction(true);
		
		SubscriberAccountClosingXMLResult result = (SubscriberAccountClosingXMLResult)subscriberClosingInquiryHandler.handle(td);
		
		if(null != result) {
			
			if(String.valueOf(CmFinoFIX.NotificationCode_SubscriberClosingInquirySuccess).equals(result.getCode())) {
			
				errorMsg.setsuccess(true);
				errorMsg.setTransferID(result.getSctlID());
				errorMsg.setDestMDN(realMsg.getDestMDN());
				errorMsg.setDestinationUserName(result.getName());
				
			} else if(String.valueOf(CmFinoFIX.NotificationCode_SubscriberHasAccountBalance).equals(result.getCode())) {
				
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
				errorMsg.setErrorDescription("Transaction Failed. Subscriber balance is available.");
				
			} else if(String.valueOf(CmFinoFIX.NotificationCode_MDNIsNotActive).equals(result.getCode())) {
				
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
				errorMsg.setErrorDescription("Transaction Failed. Subscriber is not active.");
				
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
