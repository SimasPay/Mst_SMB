package com.mfino.iso8583.processor.bank.fixtoiso;

import static com.mfino.fix.CmFinoFIX.ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas;
import static com.mfino.fix.CmFinoFIX.ISO8583_AuthorizationIdentificationResponseLength_Sinarmas;
import static com.mfino.fix.CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas;
import static com.mfino.fix.CmFinoFIX.ISO8583_MerchantType_Delivery_Channel_For_Mobile_Phone;
import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Topup_Other;
import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Topup_Other1;
import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Topup_Self;
import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Topup_Self1;

import java.io.IOException;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankAccountTopupReversalToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.WrapperISOMessageFactory;
import com.mfino.iso8583.processor.bank.SinarmasISOMessage;
import com.mfino.util.DateTimeUtil;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.IsoValue;

public class BankTopupReversal extends BankRequest implements IFIXtoISOProcessor {

	public int	TPM_UseNewBankCodes	= 0;

	public BankTopupReversal() throws IOException {
		isoMsg = (SinarmasISOMessage) WrapperISOMessageFactory.newWrapperISOMessage(0x400);
	}

	@Override
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {
		super.process(fixmsg);
		
		CMBankAccountTopupReversalToBank msg = (CMBankAccountTopupReversalToBank) fixmsg;
		if (msg.getSourceMDN().equals(msg.getDestMDN())) {
			if (TPM_UseNewBankCodes != 0)
				isoMsg.setProcessingCode(Integer.parseInt(ISO8583_ProcessingCode_Sinarmas_Topup_Self1));
			else
				isoMsg.setProcessingCode(Integer.parseInt(ISO8583_ProcessingCode_Sinarmas_Topup_Self));
		}
		else {
			if (TPM_UseNewBankCodes != 0)
				isoMsg.setProcessingCode(Integer.parseInt(ISO8583_ProcessingCode_Sinarmas_Topup_Other1));
			else
				isoMsg.setProcessingCode(Integer.parseInt(ISO8583_ProcessingCode_Sinarmas_Topup_Other));
		}
		isoMsg.setTransactionAmount(msg.getAmount().toString());
		Long stan = Long.parseLong(msg.getBankSystemTraceAuditNumber());
		stan = stan%1000000;
		String paddedSTAN = WrapperISOMessage.padOnLeft(stan.toString(),'0',6);
		isoMsg.setSTAN(Long.parseLong(msg.getBankSystemTraceAuditNumber()));
		isoMsg.setRRN(msg.getBankRetrievalReferenceNumber());
		Timestamp ts = DateTimeUtil.getLocalTime();
		isoMsg.setLocalTransactionTime(msg.getTransferTime());
		isoMsg.setTransmissionTime(msg.getTransferTime());
		isoMsg.setLocalTransactionDate(msg.getTransferTime());
		isoMsg.setSettlementDate(ts);
		String reversalInfoStr = "0200" + paddedSTAN;
		IsoValue<Timestamp> isoValue = new IsoValue<Timestamp>(IsoType.DATE10, ts);
		reversalInfoStr = reversalInfoStr + isoValue.toString();
		reversalInfoStr = reversalInfoStr + ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas.toString();
		reversalInfoStr = reversalInfoStr + ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas.toString();;
		isoMsg.setSinarmasReversalInfo(reversalInfoStr);//FIXME			
		return isoMsg;
	}
}
