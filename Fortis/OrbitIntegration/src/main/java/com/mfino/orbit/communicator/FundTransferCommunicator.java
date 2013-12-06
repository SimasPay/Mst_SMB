package com.mfino.orbit.communicator;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.domain.NoISOResponseMsg;
import com.mfino.fix.CmFinoFIX.CMBankTellerMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMBankTellerMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.orbit.OrbitConstants;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;
import com.techzultants.orbit.ws.FtRequestBean;
import com.techzultants.orbit.ws.FtResponseBean;

public class FundTransferCommunicator extends OrbitCommunicator {

	private static Logger log = LoggerFactory.getLogger(FundTransferCommunicator.class);

	@Override
	public MCEMessage process(MCEMessage mceMessage) {
		log.info("process():BEGIN");
		CMMoneyTransferToBank toBank = (CMMoneyTransferToBank) mceMessage.getResponse();
		CMMoneyTransferFromBank fromBank = new CMMoneyTransferFromBank();
		fromBank.copy(toBank);
		String responseCode = OrbitConstants.SYSERROR;;
		
		try {
			FtRequestBean ftRequestBean = new FtRequestBean();
			ftRequestBean.setAmount(toBank.getAmount());
			ftRequestBean.setCharges(BigDecimal.ZERO); //no charges set in toBank 
			ftRequestBean.setCreditAccount(toBank.getDestCardPAN());
			ftRequestBean.setDebitAccount(toBank.getSourceCardPAN());
			ftRequestBean.setHashValue(getHashValue(toBank.getSourceCardPAN(),toBank.getDestCardPAN(), toBank.getAmount()));
			ftRequestBean.setReferenceNumber(toBank.getServiceChargeTransactionLogID().toString());
			ftRequestBean.setTranDate(df.format(toBank.getTransferTime()));
			ftRequestBean.setNarration("FundTransfer");
			ftRequestBean.setReversalFlag(OrbitConstants.REVERSALFLAG_NO);
			ftRequestBean.setOriginalReferenceNumber(null);
			
			log.info("Sending FundTransfer request with ReferenceNumber:"	+ ftRequestBean.getReferenceNumber());			
			FtResponseBean response = orbitService.fundsTransfer(ftRequestBean);
			
			if (response != null) {
				log.info("Received responseCode:" + response.getResponseCode()+ " for FundTransfer request with ReferenceNumber:"	+ ftRequestBean.getReferenceNumber());
				responseCode = response.getResponseCode();
			}else{
				log.info("Empty reponse received");
			}
		} catch (NoSuchAlgorithmException e) {
			log.error("Error", e);
		} catch (Exception e) {
			responseCode = handleWSCommunicationException(e);
		}
		mceMessage.setRequest(toBank);
		if(MCEUtil.SERVICE_TIME_OUT.equals(responseCode)){
			NoISOResponseMsg noResponse = new NoISOResponseMsg();
			noResponse.copy(toBank);
			noResponse.header().setSendingTime(DateTimeUtil.getLocalTime());
			noResponse.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
			mceMessage.setResponse(noResponse);			
		}else{
			fromBank.setResponseCode(responseCode);
			fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
			fromBank.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
			mceMessage.setResponse(fromBank);
			//teller case
			if(mceMessage.getResponse() instanceof CMBankTellerMoneyTransferToBank){
				CMBankTellerMoneyTransferFromBank tellerFromBank = (CMBankTellerMoneyTransferFromBank) fromBank;
				tellerFromBank.setIsInDirectCashIn(((CMBankTellerMoneyTransferToBank)toBank).getIsInDirectCashIn());
				mceMessage.setResponse(tellerFromBank);
			}
		}	
		
		
		
		log.info("process():END");
		return mceMessage;
	}

}
