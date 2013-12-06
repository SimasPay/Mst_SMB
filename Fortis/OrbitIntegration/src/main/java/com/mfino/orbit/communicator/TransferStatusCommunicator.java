package com.mfino.orbit.communicator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.domain.NoISOResponseMsg;
import com.mfino.fix.CmFinoFIX.CMBankTellerMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMBankTellerMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.mce.core.MCEMessage;
import com.mfino.orbit.NoResponseException;
import com.mfino.orbit.OrbitConstants;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;
import com.techzultants.orbit.ws.FtStatusBean;

public class TransferStatusCommunicator extends OrbitCommunicator {

	private static Logger log = LoggerFactory.getLogger(TransferStatusCommunicator.class);

	@Override
	public MCEMessage process(MCEMessage mceMessage) throws NoResponseException {
		log.info("process():BEGIN");
		NoISOResponseMsg getTransactionStatus = (NoISOResponseMsg) mceMessage.getResponse();
		String responseCode = OrbitConstants.SYSERROR;
		try {
			log.info("Sending request to get TransactionStatus with referenceNumber:"+ getTransactionStatus.getServiceChargeTransactionLogID());
			FtStatusBean ftStatusBean = orbitService.getFTStatus(getTransactionStatus.getServiceChargeTransactionLogID().toString());
			if (ftStatusBean != null) {
				responseCode = ftStatusBean.getResponseCode();
				if (OrbitConstants.SUCCESS.equals(responseCode)) {
						CMMoneyTransferFromBank fromBank = new CMMoneyTransferFromBank();
						fromBank.copy((CMMoneyTransferToBank) mceMessage.getRequest());
						fromBank.setResponseCode(ftStatusBean.getTransactionStatus());
						fromBank.header().setSendingTime(DateTimeUtil.getLocalTime());
						fromBank.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
						mceMessage.setResponse(fromBank);
						//teller case
						if(mceMessage.getRequest() instanceof CMBankTellerMoneyTransferToBank){
							CMBankTellerMoneyTransferFromBank tellerFromBank = (CMBankTellerMoneyTransferFromBank) fromBank;
							tellerFromBank.setIsInDirectCashIn(((CMBankTellerMoneyTransferToBank)mceMessage.getRequest()).getIsInDirectCashIn());
							mceMessage.setResponse(tellerFromBank);
						}
						//reversal
						if(ftStatusBean.isReversalFlag())
							mceMessage.setResponse((CMMoneyTransferReversalFromBank)fromBank);
						
						log.info("ReferenceNumber:"+getTransactionStatus.getServiceChargeTransactionLogID()+"  TransactionStatus:"+ftStatusBean.getTransactionStatus());
				}else{
					log.info("Failure Response"+ftStatusBean.getResponseCode()+"  for getTransferStatus with referenceNumber:"+getTransactionStatus.getServiceChargeTransactionLogID());
					throw new NoResponseException("Failure Response"+ftStatusBean.getResponseCode()+" for getTransferStatus with referenceNumber:"+getTransactionStatus.getServiceChargeTransactionLogID());
				}
			}else{
				log.info("Ëmpty Response for getTransferStatus with referenceNumber:"+getTransactionStatus.getServiceChargeTransactionLogID());
				throw new NoResponseException("Empty response for getTransferStatus with referenceNumber:"+getTransactionStatus.getServiceChargeTransactionLogID());
			}
		} catch (Exception e) {
			responseCode = handleWSCommunicationException(e);
			throw new NoResponseException("No Response for getTransferStatus with referenceNumber:"+getTransactionStatus.getServiceChargeTransactionLogID());
		}
		log.info("process():END");
		return mceMessage;
	}

}
