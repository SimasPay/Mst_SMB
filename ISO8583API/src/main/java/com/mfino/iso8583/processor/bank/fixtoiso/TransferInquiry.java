package com.mfino.iso8583.processor.bank.fixtoiso;

import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Transfer_CashIn_Inquiry;
import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Transfer_CashIn_Inquiry1;
import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Transfer_CashOut_Inquiry;
import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Transfer_CashOut_Inquiry1;
import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Transfer_Inquiry;
import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Transfer_Inquiry1;
import static com.mfino.fix.CmFinoFIX.TransactionUICategory_Dompet_EMoney_Trf;
import static com.mfino.fix.CmFinoFIX.TransactionUICategory_EMoney_CashIn;
import static com.mfino.fix.CmFinoFIX.TransactionUICategory_EMoney_CashOut;
import static com.mfino.fix.CmFinoFIX.TransactionUICategory_EMoney_Dompet_Trf;
import static com.mfino.fix.CmFinoFIX.TransactionUICategory_EMoney_Purchase;

import java.io.IOException;

import com.mfino.crypto.CryptographyService;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.WrapperISOMessageFactory;
import com.mfino.iso8583.processor.bank.SinarmasISOMessage;
import com.mfino.util.DateTimeUtil;

public class TransferInquiry extends BankRequest implements IFIXtoISOProcessor {

	public TransferInquiry() throws IOException {
		isoMsg = (SinarmasISOMessage) WrapperISOMessageFactory.newWrapperISOMessage(0x200);
	}

	public int	TPM_UseBankNewCodes	= 0;

	@Override
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {
		super.process(fixmsg);
		CMTransferInquiryToBank msg = (CMTransferInquiryToBank) fixmsg;
		if (TPM_UseBankNewCodes != 0)
			isoMsg.setProcessingCode(Integer.parseInt(ISO8583_ProcessingCode_Sinarmas_Transfer_Inquiry1));
		else
			isoMsg.setProcessingCode(Integer.parseInt(ISO8583_ProcessingCode_Sinarmas_Transfer_Inquiry));

		if (msg.getUICategory().equals(TransactionUICategory_EMoney_CashIn) 
				|| msg.getUICategory().equals(TransactionUICategory_Dompet_EMoney_Trf)) {
			if (TPM_UseBankNewCodes != 0)
				isoMsg.setProcessingCode(Integer.parseInt(ISO8583_ProcessingCode_Sinarmas_Transfer_CashIn_Inquiry1));
			else
				isoMsg.setProcessingCode(Integer.parseInt(ISO8583_ProcessingCode_Sinarmas_Transfer_CashIn_Inquiry));
		}
		else if (msg.getUICategory().equals(TransactionUICategory_EMoney_Purchase) 
				|| msg.getUICategory().equals(TransactionUICategory_EMoney_CashOut)
		        || msg.getUICategory().equals(TransactionUICategory_EMoney_Dompet_Trf)) {
			if (TPM_UseBankNewCodes != 0)
				isoMsg.setProcessingCode(Integer.parseInt(ISO8583_ProcessingCode_Sinarmas_Transfer_CashOut_Inquiry1));
			else
				isoMsg.setProcessingCode(Integer.parseInt(ISO8583_ProcessingCode_Sinarmas_Transfer_CashOut_Inquiry));
		}
		else {
			if (TPM_UseBankNewCodes != 0)
				isoMsg.setProcessingCode(Integer.parseInt(ISO8583_ProcessingCode_Sinarmas_Transfer_Inquiry1));
			else
				isoMsg.setProcessingCode(Integer.parseInt(ISO8583_ProcessingCode_Sinarmas_Transfer_Inquiry));
		}
		isoMsg.setTransactionAmount(msg.getAmount().toString());
		Timestamp ts = DateTimeUtil.getLocalTime();
		isoMsg.setSettlementDate(ts);
		isoMsg.setEncryptedPin(CryptographyService.buildEncryptedPINBlock16(null, null, null));
		isoMsg.setAccountIdentification1(msg.getSourceCardPAN());
		isoMsg.setAccountIdentification2(msg.getDestCardPAN());
		return isoMsg;
	}
}
