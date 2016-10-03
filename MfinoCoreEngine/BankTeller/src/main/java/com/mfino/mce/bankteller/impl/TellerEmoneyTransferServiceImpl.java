package com.mfino.mce.bankteller.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankTellerCashIn;
import com.mfino.fix.CmFinoFIX.CMBankTellerCashInConfirm;
import com.mfino.fix.CmFinoFIX.CMBankTellerMoneyTransferFromBank;
import com.mfino.mce.bankteller.TellerBackendResponse;
import com.mfino.mce.bankteller.TellerEmoneyTransferService;
import com.mfino.mce.core.CoreDataWrapper;
import com.mfino.mce.core.MCEMessage;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class TellerEmoneyTransferServiceImpl implements TellerEmoneyTransferService {
	Log log = LogFactory.getLog(TellerEmoneyTransferServiceImpl.class);
	private CoreDataWrapper coreDataWrapper;

	@Override
	public CFIXMsg generateCashInInquiry(TellerBackendResponse response) {
		ServiceChargeTxnLog sctl = coreDataWrapper.getSCTLById(response.getServiceChargeTransactionLogID());
		CMBankTellerCashIn cashIn = new CMBankTellerCashIn();
		
		cashIn.m_pHeader.setMsgSeqNum(UniqueNumberGen.getNextNum());
		cashIn.header().setSendingTime(DateTimeUtil.getLocalTime());
		cashIn.setServiceName(ServiceAndTransactionConstants.SERVICE_TELLER);
		cashIn.setSourceApplication(response.getSourceApplication());
        cashIn.setSourceMDN(response.getSourceMDN());
        cashIn.setDestMDN(sctl.getDestmdn());
        cashIn.setAmount(sctl.getTransactionamount().subtract(sctl.getCalculatedcharge()));
        cashIn.setServiceChargeTransactionLogID(response.getServiceChargeTransactionLogID());
        cashIn.setCharges(sctl.getCalculatedcharge());
        cashIn.setPin(response.getPin());
        cashIn.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_TELLER_CASHIN_EMONEYTOEMONEY);
        cashIn.setServletPath(CmFinoFIX.ServletPath_BankAccount);
        cashIn.setTransactionID(sctl.getTransactionid().longValue());
        cashIn.setSourcePocketID(response.getDestPocketID());
        cashIn.setDestPocketID(response.getEndDestPocketID()); 
        cashIn.setServiceChargeTransactionLogID(sctl.getId().longValue());
        cashIn.setIsInDirectCashIn(false);
        cashIn.setUICategory(CmFinoFIX.TransactionUICategory_Teller_Cashin_Subscriber);
		return cashIn;
	}

	@Override
	public CFIXMsg generateCashInConfirm(TellerBackendResponse response) {
		CMBankTellerCashInConfirm cashInConfirm = new CMBankTellerCashInConfirm();
		cashInConfirm.m_pHeader.setMsgSeqNum(UniqueNumberGen.getNextNum());
		cashInConfirm.header().setSendingTime(DateTimeUtil.getLocalTime());
		ServiceChargeTxnLog sctl = coreDataWrapper.getSCTLById(response.getServiceChargeTransactionLogID());
		cashInConfirm.setServiceName(ServiceAndTransactionConstants.SERVICE_TELLER);
		cashInConfirm.setSourceApplication(response.getSourceApplication());
		cashInConfirm.setSourceMDN(response.getSourceMDN());
		cashInConfirm.setDestMDN(sctl.getDestmdn());
		cashInConfirm.setPin(response.getPin());
		cashInConfirm.setServletPath(CmFinoFIX.ServletPath_BankAccount);
		cashInConfirm.setTransactionID(response.getTransactionID());
		cashInConfirm.setTransferID(response.getTransferID());
		cashInConfirm.setSourcePocketID(response.getSourcePocketID());
		cashInConfirm.setDestPocketID(response.getDestPocketID()); 
		cashInConfirm.setServiceChargeTransactionLogID(sctl.getId().longValue());
		cashInConfirm.setIsInDirectCashIn(false);
		cashInConfirm.setConfirmed(CmFinoFIX.Boolean_True);
        return cashInConfirm;
	}
	
	public CoreDataWrapper getCoreDataWrapper() {
		return coreDataWrapper;
	}

	public void setCoreDataWrapper(CoreDataWrapper coreDataWrapper) {
		this.coreDataWrapper = coreDataWrapper;
	}

	@Override
	public CFIXMsg processMessage(MCEMessage mceMessage) 
	{
		if(mceMessage.getRequest() instanceof CMBankTellerMoneyTransferFromBank)
		{
			return generateCashInInquiry((TellerBackendResponse)mceMessage.getResponse());
		}
		//FIXME: check and fix this incoming message
		else if(mceMessage.getRequest() instanceof CMBankTellerCashIn)
		{
			return generateCashInConfirm((TellerBackendResponse)mceMessage.getResponse());
		}
		else
		{
			log.warn("received a message which cannot be handled here, some bug in routing logic or code");
		}
			
		return null;
	}

	

}
