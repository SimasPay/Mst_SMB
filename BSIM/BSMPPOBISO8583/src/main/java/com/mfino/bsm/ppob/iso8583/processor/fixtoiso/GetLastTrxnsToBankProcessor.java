package com.mfino.bsm.ppob.iso8583.processor.fixtoiso;

import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Get_Last_Transactions;
import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Get_Last_Transactions1;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.bsm.ppob.iso8583.utils.DateTimeFormatter;
import com.mfino.crypto.CryptographyService;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.util.DateTimeUtil;

public class GetLastTrxnsToBankProcessor extends BankRequestProcessor {

	public GetLastTrxnsToBankProcessor() {
		try {
			isoMsg.setMTI("0200");
		}
		catch (ISOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {
		super.process(fixmsg);

		CMGetLastTransactionsToBank msg = (CMGetLastTransactionsToBank) fixmsg;
		Timestamp ts = DateTimeUtil.getLocalTime();

		try {
			isoMsg.set(2, msg.getSourceCardPAN());// msg.getSourceCardPAN()

			if (TPM_UseBankNewCodes != 0)
				isoMsg.set(3,ISO8583_ProcessingCode_Sinarmas_Get_Last_Transactions1);
			else
				isoMsg.set(3,ISO8583_ProcessingCode_Sinarmas_Get_Last_Transactions);
			
			isoMsg.set(4, constantFieldsMap.get("ZERO_AMOUNT")); // 4
			isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts)); // 7
			Long transactionID = msg.getTransactionID();
			transactionID = transactionID % 1000000;
			isoMsg.set(11, transactionID.toString());// 11
			isoMsg.set(12, DateTimeFormatter.getHHMMSS(ts)); // 12
			isoMsg.set(13, DateTimeFormatter.getMMDD(ts)); // 13
			isoMsg.set(18, CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone); // 18
			isoMsg.set(27, CmFinoFIX.ISO8583_AuthorizationIdentificationResponseLength_Sinarmas.toString()); // 27
			isoMsg.set(32, CmFinoFIX.ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas.toString());// 32
			isoMsg.set(33, CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas.toString());// 33
			isoMsg.set(35, msg.getSourceCardPAN());
			isoMsg.set(37, msg.getTransactionID().toString());
			isoMsg.set(42, msg.getSourceMDN());
			isoMsg.set(43, constantFieldsMap.get("43"));
			isoMsg.set(47, msg.getTransactionID().toString());
			isoMsg.set(52, CryptographyService.buildEncryptedPINBlock16(msg.getPin(), msg.getSourceCardPAN(), null)); // 
		}
		catch (ISOException ex) {

		}
		return isoMsg;
	}
}
