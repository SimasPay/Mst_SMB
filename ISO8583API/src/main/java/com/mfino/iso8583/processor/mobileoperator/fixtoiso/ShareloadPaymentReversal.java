package com.mfino.iso8583.processor.mobileoperator.fixtoiso;

import static com.mfino.fix.CmFinoFIX.ISO8583_Mobile_Operator_ProcessingCode_Postpaid_Payment;
import static com.mfino.fix.CmFinoFIX.ISO8583_Sinarmas_CurrencyCode_IDR;
import static com.mfino.fix.CmFinoFIX.*;

import org.apache.commons.lang.StringUtils;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMCommodityTransferReversalToOperator;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.WrapperISOMessageFactory;
import com.mfino.iso8583.processor.mobileoperator.MobileOperatorISOMessage;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.MfinoUtil;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.IsoValue;

public class ShareloadPaymentReversal implements IFIXtoISOProcessor {

	@Override
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {
		
		MobileOperatorISOMessage isoMsg = (MobileOperatorISOMessage) WrapperISOMessageFactory.newWrapperISOMessage(0x400,
		        ISO8583_Variant_Mobile_Operator_Gateway_Interface);
		if(fixmsg instanceof CMCommodityTransferReversalToOperator) {
			
			CMCommodityTransferReversalToOperator request = (CMCommodityTransferReversalToOperator)fixmsg;
			
			isoMsg.setProcessingCode(Integer.parseInt(ISO8583_Mobile_Operator_ProcessingCode_Transfer));
			Timestamp ts = DateTimeUtil.getLocalTime();
			isoMsg.setTransmissionTime(ts);
			isoMsg.setLocalTransactionTime(ts);
			isoMsg.setSTAN(Long.parseLong(request.getISO8583_SystemTraceAuditNumber()));
			isoMsg.setLocalTransactionDate(ts);
			if(!StringUtils.isBlank( request.getISO8583_MerchantType()))
				isoMsg.setMerchantType(Integer.parseInt(request.getISO8583_MerchantType()));
			else
				isoMsg.setMerchantType(Integer.parseInt(MfinoUtil.GetMerchantTypeBySourceApplication(request.getSourceApplication())));
			isoMsg.setAcquiringInstitutionIdentificationCode(request.getISO8583_AcquiringInstIdCode().toString());
			isoMsg.setRRN(request.getISO8583_RetrievalReferenceNum());
			if(!StringUtils.isBlank(request.getProductIndicatorCode()))
				isoMsg.setProductIndicator(request.getProductIndicatorCode());
			else
				isoMsg.setProductIndicator("5300");
			String data = WrapperISOMessage.padOnRight(request.getSourceMDN(), ' ', 13);
			data = data +WrapperISOMessage.padOnRight(request.getDestMDN(), ' ', 13);
			isoMsg.setTransactionRequestData(data);
			
			String paddedSTAN = WrapperISOMessage.padOnLeft(request.getOriginalTransactionSTAN(), '0', 6);
			String orig = "0200" + paddedSTAN;
			IsoValue<Timestamp> isoValue = new IsoValue<Timestamp>(IsoType.DATE10, ts);
			orig = orig + isoValue.toString();
			orig = orig+WrapperISOMessage.padOnLeft(request.getISO8583_AcquiringInstIdCode().toString(),'0',11);
			isoMsg.setOriginalPaymentTransactionData(orig);
			isoMsg.setSTAN(Long.parseLong(request.getOriginalTransactionSTAN()));
		}
		else {
			throw new Exception("Not an instance of CMCommodityTransferReversalToOperator");
		}
		return isoMsg;
	}
}
