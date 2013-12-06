package com.mfino.transactionawarehandlers.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.fix.CFIXMsg;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.XMLResult;
import com.mfino.transactionapi.handlers.interswitch.impl.IntegrationCashinConfirmHandlerImpl;
import com.mfino.transactionapi.result.xmlresulttypes.wallet.WalletConfirmXMLResult;
import com.mfino.transactionawarehandlers.interfaces.TrxnAwareIntegrationCashinConfirmInterface;

/**
 * This handler is used for Interswitch cashin confirmation.It calls the corresponsing methods on the interswitch handler sent to it
 * This handler ensures transaction management happens properly for Interswitch cashin
 * @author Sreenath
 *
 */
public class TrxnAwareIntegrationCashinConfirmHandler implements TrxnAwareIntegrationCashinConfirmInterface {
	
	private static Logger	log	= LoggerFactory.getLogger(TrxnAwareIntegrationCashinConfirmHandler.class);
	
	@Override
	@Transactional(readOnly=false,propagation=Propagation.REQUIRES_NEW)
	/**
	 * calls the preprocess method on input handler 
	 * @param ichHandler of type IntegrationCashinConfirmHandler
	 * @return WalletConfirmXMLResult
	 */
	public WalletConfirmXMLResult preprocess(FIXMessageHandler ichHandler) {
		log.info("TrxnAwareIntegrationCashinConfirmHandler ::preprocess start" );
		WalletConfirmXMLResult confirmResult=null;
		if(ichHandler instanceof IntegrationCashinConfirmHandlerImpl && ichHandler!=null){
			confirmResult = ((IntegrationCashinConfirmHandlerImpl)ichHandler).preprocess();
		}
		else{
			log.error("The handler sent is not of IntegrationCashinConfirmHandler or is null.Returning null");
		}
		log.info("TrxnAwareIntegrationCashinConfirmHandler ::preprocess end" );

		return confirmResult;
	}

	@Override
	/**
	 * calls the communicate method on input hanlder
	 * @param ichHandler of type IntegrationCashinConfirmHandler
	 * @return CFIXMsg
	 */
	public CFIXMsg communicate(FIXMessageHandler ichHandler) {
		log.info("TrxnAwareIntegrationCashinConfirmHandler ::communicate start" );
		CFIXMsg response = null;
		if(ichHandler instanceof IntegrationCashinConfirmHandlerImpl && ichHandler!=null){
			response = ((IntegrationCashinConfirmHandlerImpl)ichHandler).communicate();
		}
		else{
			log.error("The handler sent is not of IntegrationCashinConfirmHandler or is null.Returning null");
		}
		log.info("TrxnAwareIntegrationCashinConfirmHandler ::communicate end" );

		return response;
	}

	@Override
	@Transactional(readOnly=false,propagation=Propagation.REQUIRES_NEW)
	/**
	 * Calls the postprocess method on the input handler
	 * @param ichHandler of type IntegrationCashinConfirmHandler
	 * @param response
	 * @param result
	 * @return WalletConfirmXMLResult
	 */
	public WalletConfirmXMLResult postprocess(FIXMessageHandler ichHandler, CFIXMsg response, XMLResult result) {
		log.info("TrxnAwareIntegrationCashinConfirmHandler ::postprocess start" );
		WalletConfirmXMLResult confirmResult = null;
		if(ichHandler instanceof IntegrationCashinConfirmHandlerImpl && ichHandler!=null){
			confirmResult = ((IntegrationCashinConfirmHandlerImpl)ichHandler).postprocess(response,(WalletConfirmXMLResult)result);
		}
		else{
			log.error("The handler sent is not of IntegrationCashinConfirmHandler or is null.Returning null");
		}
		log.info("TrxnAwareIntegrationCashinConfirmHandler ::postprocess end" );
		return confirmResult;
	}




}
