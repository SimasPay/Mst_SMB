package com.mfino.bsm.uangku.iso8583.processor.fixtoiso;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.packager.PostPackager;

import com.mfino.bsm.uangku.iso8583.utils.DateTimeFormatter;
import com.mfino.crypto.CryptographyService;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.util.DateTimeUtil;

public class BalanceInquiryToBankProcessor extends BankRequestProcessor {

	public BalanceInquiryToBankProcessor() {
		try {
			isoMsg.setMTI("0200");
		}
		catch (ISOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {
		// super.process(fixmsg);
		CMBalanceInquiryToBank request = (CMBalanceInquiryToBank) fixmsg;
		Timestamp ts = DateTimeUtil.getLocalTime();

		try {
			isoMsg.set(2, request.getSourceCardPAN());

			/*String processingCode = GetConstantCodes.getTransactionType(request);
			if (CmFinoFIX.BankAccountType_Saving.toString().equals(request.getSourceBankAccountType()))
				processingCode = processingCode + constantFieldsMap.get("SAVINGS_ACCOUNT");
			else if (CmFinoFIX.BankAccountType_Checking.toString().equals(request.getSourceBankAccountType()))
				processingCode = processingCode + constantFieldsMap.get("CHECKING_ACCOUNT");
			processingCode = processingCode + constantFieldsMap.get("UNSPECIFIED_ACCOUNT");
*/
			if (TPM_UseBankNewCodes != 0)
				isoMsg.set(3,CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Balance_Inquiry1);//3
			else
				isoMsg.set(3,CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Balance_Inquiry);

			isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts)); // 7
			Long transactionID = request.getTransactionID();
			transactionID = transactionID % 1000000;
			isoMsg.set(11, transactionID.toString());// 11
			isoMsg.set(12, DateTimeFormatter.getHHMMSS(ts)); // 12
			isoMsg.set(13, DateTimeFormatter.getMMDD(ts)); // 13
			isoMsg.set(15, DateTimeFormatter.getMMDD(ts)); // 15
			isoMsg.set(18, CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone); // 18
			isoMsg.set(27, CmFinoFIX.ISO8583_AuthorizationIdentificationResponseLength_Sinarmas.toString()); // 27
			isoMsg.set(32, constantFieldsMap.get("32"));
			isoMsg.set(33, CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas.toString());// 33
			isoMsg.set(35, request.getSourceCardPAN());
			isoMsg.set(37, request.getTransactionID().toString());
			isoMsg.set(42, request.getSourceMDN());
			isoMsg.set(43, constantFieldsMap.get("43"));
			isoMsg.set(47, request.getTransactionID().toString());
			isoMsg.set(52, CryptographyService.buildEncryptedPINBlock16(request.getPin(), request.getSourceCardPAN(), null)); // 
//			isoMsg.set(52, request.getPin()); 
		}
		catch (ISOException ex) {

		}
		return isoMsg;
	}

	public static void main(String[] args) throws Exception {

		Timestamp ts = new Timestamp();
		System.out.println(String.format("%Tm%<Td%<TH%<TM%<TS", ts));
		System.out.println(String.format("%Tm%<Td", ts));
		System.out.println(String.format("%Ty%<Tm", ts));
		System.out.println(String.format("%TH%<TM%<TS", ts));
		System.out.println(String.format("%TC%<Ty%<Tm%<Td", ts));

		CMBalanceInquiryToBank msg = new CMBalanceInquiryToBank();
		msg.setSourceCardPAN("55555555");
		msg.setSourceBankAccountType(CmFinoFIX.BankAccountType_Saving.toString());
		msg.setTransactionID(12345l);

		BalanceInquiryToBankProcessor toZ = new BalanceInquiryToBankProcessor();
		ISOMsg isoMsg = toZ.process(msg);

		ISOPackager packager = new PostPackager();
		isoMsg.setPackager(packager);
		System.out.println(new String(isoMsg.pack()));

	}

}