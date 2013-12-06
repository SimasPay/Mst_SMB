package com.mfino.orbit.communicator;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.domain.NoISOResponseMsg;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.orbit.OrbitConstants;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;
import com.techzultants.orbit.ws.FtRequestBean;
import com.techzultants.orbit.ws.FtResponseBean;

public class FundTransferReversalCommunicator extends OrbitCommunicator {

	private static Logger log = LoggerFactory.getLogger(FundTransferReversalCommunicator.class);

	@Override
	public MCEMessage process(MCEMessage mceMessage) {
		log.info("process():BEGIN");
		CMMoneyTransferReversalToBank toBank = (CMMoneyTransferReversalToBank) mceMessage.getResponse();
		CMMoneyTransferReversalFromBank fromBank = new CMMoneyTransferReversalFromBank();
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
			ftRequestBean.setNarration("FundTransferReversal");
			ftRequestBean.setReversalFlag(OrbitConstants.REVERSALFLAG_YES);
			ftRequestBean.setOriginalReferenceNumber(toBank.getServiceChargeTransactionLogID().toString());
			
			log.info("Sending FundTransferReversal request with ReferenceNumber:"	+ ftRequestBean.getReferenceNumber());			
			FtResponseBean response = orbitService.fundsTransfer(ftRequestBean);
			
			if (response != null) {
				log.info("Received responseCode:" + response.getResponseCode()+ " for FundTransferReversal request with ReferenceNumber:"	+ ftRequestBean.getReferenceNumber());
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
		}		
		log.info("process():END");
		return mceMessage;
	}

}
