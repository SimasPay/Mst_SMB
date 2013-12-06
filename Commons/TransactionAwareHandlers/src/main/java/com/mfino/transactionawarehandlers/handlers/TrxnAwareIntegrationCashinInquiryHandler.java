package com.mfino.transactionawarehandlers.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.fix.CFIXMsg;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.XMLResult;
import com.mfino.transactionapi.handlers.interswitch.impl.IntegrationCashinInquiryHandlerImpl;
import com.mfino.transactionapi.result.xmlresulttypes.money.TransferInquiryXMLResult;
import com.mfino.transactionawarehandlers.interfaces.TrxnAwareIntegrationCashinInquiryInterface;

/**
 * This handler is used for Interswitch cashin Inquiry.It calls the corresponsing methods on the interswitch handler sent to it
 * This handler ensures transaction management happens properly for Interswitch cashin
 * @author Sreenath
 *
 */
public class TrxnAwareIntegrationCashinInquiryHandler implements TrxnAwareIntegrationCashinInquiryInterface {

	private static Logger	log	= LoggerFactory.getLogger(TrxnAwareIntegrationCashinInquiryHandler.class);
	
	@Override
	@Transactional(readOnly=false,propagation=Propagation.REQUIRES_NEW)
	/**
	 * calls the preprocess method on input handler 
	 * @param ichHandler of type IntegrationCashinInquiryHandler
	 * @return WalletConfirmXMLResult
	 */
	public TransferInquiryXMLResult preprocess(FIXMessageHandler ichHandler) {
		log.info("TrxnAwareIntegrationCashinInquiryHandler ::preprocess start" );
		TransferInquiryXMLResult inquiryResult=null;
		if(ichHandler instanceof IntegrationCashinInquiryHandlerImpl && ichHandler!=null){
			inquiryResult = ((IntegrationCashinInquiryHandlerImpl) ichHandler).preprocess();
		}
		else{
			log.error("The handler sent is not of IntegrationCashinInquiryHandler or is null.Returning null");
		}
		log.info("TrxnAwareIntegrationCashinInquiryHandler ::preprocess end" );
		return inquiryResult;
	}

	@Override
	/**
	 * calls the communicate method on input hanlder
	 * @param ichHandler of type IntegrationCashinInquiryHandler
	 * @return CFIXMsg
	 */
	public CFIXMsg communicate(FIXMessageHandler ichHandler) {
		log.info("TrxnAwareIntegrationCashinInquiryHandler ::communicate start" );
		CFIXMsg response = null;
		if(ichHandler instanceof IntegrationCashinInquiryHandlerImpl && ichHandler!=null){
			response = ((IntegrationCashinInquiryHandlerImpl)ichHandler).communicate();
		}
		else{
			log.error("The handler sent is not of IntegrationCashinInquiryHandler or is null.Returning null");
		}
		log.info("TrxnAwareIntegrationCashinInquiryHandler ::communicate end" );
		return response;
	}

	@Override
	@Transactional(readOnly=false,propagation=Propagation.REQUIRES_NEW)
	/**
	 * Calls the postprocess method on the input handler
	 * @param ichHandler of type IntegrationCashinInquiryHandler
	 * @param response
	 * @param result
	 * @return WalletConfirmXMLResult
	 */
	public TransferInquiryXMLResult postprocess(FIXMessageHandler ichHandler,CFIXMsg response,XMLResult result) {
		log.info("TrxnAwareIntegrationCashinInquiryHandler ::postprocess start" );
		TransferInquiryXMLResult inquiryResult = null;
		if(ichHandler instanceof IntegrationCashinInquiryHandlerImpl && ichHandler!=null){
			inquiryResult = ((IntegrationCashinInquiryHandlerImpl)ichHandler).postprocess(response,(TransferInquiryXMLResult)result);
		}
		else{
			log.error("The handler sent is not of IntegrationCashinInquiryHandler or is null.Returning null");
		}
		log.info("TrxnAwareIntegrationCashinInquiryHandler ::postprocess end" );
		return inquiryResult;
	}



}
