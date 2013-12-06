package com.mfino.iso8583.processor.bank.fixtoiso;

import static com.mfino.fix.CmFinoFIX.ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas;
import static com.mfino.fix.CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas;
import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Transfer_To_Other;
import static com.mfino.fix.CmFinoFIX.ISO8583_ProcessingCode_Sinarmas_Transfer_To_Other1;

import java.io.IOException;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.WrapperISOMessageFactory;
import com.mfino.iso8583.processor.bank.SinarmasISOMessage;
import com.mfino.util.DateTimeUtil;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.IsoValue;

public class MoneyTransferReversal extends BankRequest implements IFIXtoISOProcessor {

	public MoneyTransferReversal() throws IOException {
		isoMsg = (SinarmasISOMessage) WrapperISOMessageFactory.newWrapperISOMessage(0x400);
	}

	public int	TPM_UseNewBankCodes	= 0;

	@Override
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {
		super.process(fixmsg);
		CMMoneyTransferReversalToBank msg = (CMMoneyTransferReversalToBank) fixmsg;
		Timestamp ts = DateTimeUtil.getLocalTime();
		if (TPM_UseNewBankCodes != 0)
			isoMsg.setProcessingCode(Integer.parseInt(ISO8583_ProcessingCode_Sinarmas_Transfer_To_Other1));// 3
		else
			isoMsg.setProcessingCode(Integer.parseInt(ISO8583_ProcessingCode_Sinarmas_Transfer_To_Other));
		isoMsg.setTransactionAmount(msg.getAmount().toString());// 4
		Long stan = Long.parseLong(msg.getBankSystemTraceAuditNumber());
		stan = stan % 1000000;
		String paddedSTAN = WrapperISOMessage.padOnLeft(stan.toString(), '0', 6);
		isoMsg.setSTAN(Long.parseLong(msg.getBankSystemTraceAuditNumber()));
		isoMsg.setRRN(msg.getBankRetrievalReferenceNumber());
		isoMsg.setTransmissionTime(msg.getTransferTime());
		isoMsg.setLocalTransactionDate(msg.getTransferTime());
		isoMsg.setLocalTransactionTime(msg.getTransferTime());
		isoMsg.setSettlementDate(ts);// 15
		isoMsg.setAccountIdentification1(msg.getSourceCardPAN());
		isoMsg.setAccountIdentification2(msg.getDestCardPAN());
		String reversalInfoStr = "0200" + paddedSTAN;
		IsoValue<Timestamp> isoValue = new IsoValue<Timestamp>(IsoType.DATE10, ts);
		reversalInfoStr = reversalInfoStr + isoValue.toString();
		reversalInfoStr = reversalInfoStr + ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas.toString();
		reversalInfoStr = reversalInfoStr + ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas.toString();
		isoMsg.setSinarmasReversalInfo(reversalInfoStr);
		return isoMsg;
	}
}
