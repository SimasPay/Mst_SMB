package com.mfino.iso8583.processor.mobileoperator.fixtoiso;

import org.apache.commons.lang.StringUtils;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetMDNBillDebtsToOperator;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.WrapperISOMessageFactory;
import com.mfino.iso8583.processor.mobileoperator.MobileOperatorISOMessage;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.MfinoUtil;

public class BalanceInquiry implements IFIXtoISOProcessor {

	public WrapperISOMessage process(CFIXMsg cfixmsg) throws Exception {

		MobileOperatorISOMessage isoMsg = (MobileOperatorISOMessage) WrapperISOMessageFactory.newWrapperISOMessage(0x200,
		        CmFinoFIX.ISO8583_Variant_Mobile_Operator_Gateway_Interface);

		if (cfixmsg instanceof CMGetMDNBillDebtsToOperator) {
			CMGetMDNBillDebtsToOperator msg = (CMGetMDNBillDebtsToOperator) cfixmsg;
			isoMsg.setProcessingCode(Integer.parseInt(CmFinoFIX.ISO8583_Mobile_Operator_ProcessingCode_Postpaid_Inquiry));
			Timestamp ts = DateTimeUtil.getLocalTime();
			isoMsg.setLocalTransactionTime(ts);
			isoMsg.setTransmissionTime(ts);
			if (msg.getISO8583_SystemTraceAuditNumber() != null)
				isoMsg.setSTAN(Long.parseLong(msg.getISO8583_SystemTraceAuditNumber()));
			else
				isoMsg.setSTAN(msg.getTransactionID());
			isoMsg.setLocalTransactionDate(ts);
			isoMsg.setSettlementDate(ts);
			if (msg.getISO8583_MerchantType() != null)
				isoMsg.setMerchantType(Integer.parseInt(msg.getISO8583_MerchantType()));
			else
				isoMsg.setMerchantType(Integer.parseInt(MfinoUtil.GetMerchantTypeBySourceApplication(msg.getSourceApplication())));
			if (msg.getISO8583_AcquiringInstIdCode() != null)
				isoMsg.setAcquiringInstitutionIdentificationCode(msg.getISO8583_AcquiringInstIdCode().toString());
			else
				isoMsg.setAcquiringInstitutionIdentificationCode(CmFinoFIX.ISO8583_AcquiringInstIdCode_mFino_To_Smart.toString());
			if (msg.getISO8583_RetrievalReferenceNum() != null) {
				isoMsg.setRRN(msg.getISO8583_RetrievalReferenceNum());
			}
			else
				isoMsg.setRRN(msg.getTransactionID().toString());
			isoMsg.setCardAcceptorNameLocation("SMS SMART");
			if (StringUtils.isBlank(msg.getProductIndicatorCode()))
				isoMsg.setProductIndicator("5999");
			else
				isoMsg.setProductIndicator(msg.getProductIndicatorCode());
			isoMsg.setTransactionCurrencyCode(CmFinoFIX.ISO8583_Sinarmas_CurrencyCode_IDR);
			isoMsg.setTransactionRequestData(msg.getDestMDN());
		}
		else {
			throw new Exception("Not an instance of CMGetMDNBillDebtsToOperator");
		}

		return isoMsg;

	}

}
