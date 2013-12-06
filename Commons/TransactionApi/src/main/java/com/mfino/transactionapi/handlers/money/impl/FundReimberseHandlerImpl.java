/**
 * 
 */
package com.mfino.transactionapi.handlers.money.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccount;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccountConfirmation;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.transactionapi.handlers.money.FundReimberseHandler;
import com.mfino.transactionapi.result.xmlresulttypes.money.MoneyTransferXMLResult;

/**
 * @author Maruthi
 * 
 */
@Service("FundReimberseHandlerImpl")
public class FundReimberseHandlerImpl extends FIXMessageHandler implements FundReimberseHandler{

	private static Logger log = LoggerFactory.getLogger(FundReimberseHandlerImpl.class);

	public Result handle(CMBase request) {
		XMLResult result = new MoneyTransferXMLResult();
		CFIXMsg response = null ;
		log.info("FundReimberseHandlerImpl :: Handling request for fund reimberse");
		if(request instanceof CMBankAccountToBankAccount){
			response= super.process((CMBankAccountToBankAccount)request);
		}
		else if(request instanceof CMBankAccountToBankAccountConfirmation){
			response= super.process((CMBankAccountToBankAccountConfirmation)request);
		}
		result.setMultixResponse(response);
		return result;
	}
}
