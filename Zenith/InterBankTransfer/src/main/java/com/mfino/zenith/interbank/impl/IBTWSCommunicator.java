package com.mfino.zenith.interbank.impl;

import static com.mfino.zenith.interbank.impl.IBTConstants.FUNDS_TRANSFER_WS_METHOD_NAME;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.mfino.fix.CmFinoFIX.CMInterBankFundsTransfer;
import com.mfino.fix.CmFinoFIX.CMInterBankMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMInterBankMoneyTransferToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.mce.core.ws.WSCommunicator;
import com.mfino.util.UniqueNumberGen;

/**
 * @author Sasi
 *
 */
public class IBTWSCommunicator extends WSCommunicator {
	
	public IBTWSCommunicator(){
		
	}
	
	@Override
	public List<Object> getParameterList(MCEMessage mceMessage) {
		
		List<Object> paramList = new ArrayList<Object>();
		CMInterBankMoneyTransferToBank moneyTransferToBank = (CMInterBankMoneyTransferToBank)mceMessage.getResponse();
		CMInterBankFundsTransfer interBankFundsTransfer = (CMInterBankFundsTransfer)mceMessage.getRequest();

		IBTWSData wsRequest = new IBTWSData();
		
		wsRequest.setSessionId("SessionID_PlaceHolder");
		wsRequest.setDestinationBankCode(moneyTransferToBank.getDestBankCode());
		wsRequest.setChannelCode(moneyTransferToBank.getTerminalID());
		wsRequest.setAccountName("AccountName_PlaceHolder");
		wsRequest.setAccountNumber(moneyTransferToBank.getDestAccountNumber());
		wsRequest.setOriginatorName(moneyTransferToBank.getSourceAccountNumber());
		wsRequest.setNarration(moneyTransferToBank.getNarration());
		wsRequest.setPaymentReference(""+moneyTransferToBank.getTransferID());
		wsRequest.setAmount(""+moneyTransferToBank.getAmount().setScale(2, RoundingMode.HALF_EVEN));
		
		String requestXML = IBTParser.getXML(wsRequest, FUNDS_TRANSFER_WS_METHOD_NAME);
		
		log.debug("IBTWSCommunicator :: getParameterList() : xmlData="+requestXML);
		
		paramList.add(requestXML);
		return paramList;
	}

	@Override
	public MCEMessage constructReplyMessage(List<Object> wsResponse, MCEMessage requestMceMessage) {
		Object wsResponseElement = wsResponse.get(0);
		log.info("Got response from web service:" +wsResponseElement);

		CMInterBankMoneyTransferToBank moneyTransferToBank = (CMInterBankMoneyTransferToBank)requestMceMessage.getResponse();
		CMInterBankMoneyTransferFromBank moneyTransferFromBank = new CMInterBankMoneyTransferFromBank();
		moneyTransferFromBank.header().setSendingTime(new Timestamp());
		moneyTransferFromBank.m_pHeader.setMsgSeqNum(UniqueNumberGen.getNextNum());
		
		moneyTransferFromBank.copy(moneyTransferToBank);
		
		IBTWSData responseData = null;
		
		if(!(wsResponseElement.equals(MCEUtil.SERVICE_UNAVAILABLE))){
			responseData = IBTParser.getIBTWSResponseData((String)wsResponseElement);
			
		}
		else{
			moneyTransferFromBank.setResponseCode(MCEUtil.SERVICE_UNAVAILABLE);
		}
		
		MCEMessage  mceResponse =  new MCEMessage();
		
		if(responseData != null){
			moneyTransferFromBank.setResponseCode(responseData.getResponseCode());
			moneyTransferFromBank.setNIBResponseCode(responseData.getResponseCode());
			
			moneyTransferFromBank.setSessionID(responseData.getSessionId());
			moneyTransferFromBank.setDestAccountName(responseData.getAccountName());
			moneyTransferFromBank.setDestBankCode(responseData.getDestinationBankCode());
			moneyTransferFromBank.setChannelCode(responseData.getChannelCode());
			moneyTransferFromBank.setDestAccountNumber(responseData.getAccountNumber());
			
			moneyTransferFromBank.setSourceAccountName(responseData.getOriginatorName());
			moneyTransferFromBank.setNarration(responseData.getNarration());
			moneyTransferFromBank.setPaymentReference(responseData.getPaymentReference());
			moneyTransferFromBank.setResponseCode(responseData.getResponseCode());
		}
		else{
			moneyTransferFromBank.setSourceApplication(moneyTransferToBank.getSourceApplication());
			moneyTransferFromBank.setSourceMDN(moneyTransferToBank.getSourceMDN());
			moneyTransferFromBank.setDestBankCode(moneyTransferToBank.getDestBankCode());
			moneyTransferFromBank.setDestAccountNumber(moneyTransferToBank.getDestAccountNumber());
		}
		moneyTransferFromBank.setServiceChargeTransactionLogID(moneyTransferToBank.getServiceChargeTransactionLogID());
		moneyTransferFromBank.setTerminalID(moneyTransferToBank.getTerminalID());
		moneyTransferFromBank.setSourceAccountNumber(moneyTransferToBank.getSourceAccountNumber());
		
		mceResponse.setRequest(moneyTransferToBank);
		mceResponse.setResponse(moneyTransferFromBank);
		
		return mceResponse;
	}

	@Override
	public String getMessageName(MCEMessage mceMessage) {
		return "fundtransfersingleitem2_dc";
	}
}
