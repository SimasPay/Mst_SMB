package com.mfino.zte.iso8583.processor.isotofix;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetMDNBillDebtsFromOperator;
import com.mfino.fix.CmFinoFIX.CMGetMDNBillDebtsToOperator;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;
import com.mfino.zte.iso8583.processor.ZTEISOtoFixProcessor;
import com.mfino.zte.iso8583.utils.StringUtilities;

public class AccountInquiryFromZTEProcessor implements ZTEISOtoFixProcessor {
	 
	public Log log = LogFactory.getLog(this.getClass());
	
	public static List<String> merchantPrefixData;
	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException{

		CMGetMDNBillDebtsToOperator toOperator = (CMGetMDNBillDebtsToOperator) request;

		CMGetMDNBillDebtsFromOperator fromOperator = new CMGetMDNBillDebtsFromOperator();
		fromOperator.copy(toOperator);
		fromOperator.setResponseCode(Integer.parseInt(isoMsg.getString(39)));

		if (!CmFinoFIX.ISO8583_Mobile_Operator_Response_Code_Success.equals(fromOperator.getResponseCode())) {
			fromOperator.setRejectReason(isoMsg.getString(39));
			return fromOperator;
		}

		// fromOperator.header().setMsgSeqNum(null);
		try{
		String responseData = isoMsg.getString(62);
		fromOperator.setPaymentInquiryDetails(responseData);
		String billRefNo = responseData.substring(139, 151);
		String billPaymentDate = responseData.substring(151, 159);
		String totalBillDebts = responseData.substring(172, 183);
		String subName = responseData.substring(109, 139);
		
		if(StringUtils.isNotBlank(billRefNo))
		fromOperator.setBillReferenceNumber(Long.parseLong(billRefNo.trim()));
		
		if(StringUtils.isNotBlank(billPaymentDate))
		fromOperator.setLastBillPaymentDateYYYYMMDD(Long.parseLong(billPaymentDate.trim()));
		
		if(StringUtils.isNotBlank(totalBillDebts))
		fromOperator.setTotalBillDebts(new BigDecimal(totalBillDebts.trim()));
		
		fromOperator.setPayerName(subName);

		if (toOperator.getMerchantPrefixCode() != null) {
			if (CmFinoFIX.ISO8583_Mobile_Operator_ProcessingCode_Postpaid_Inquiry.equals(isoMsg.getString(39))) {
				String subType = responseData.substring(66, 67);
				int configuredTopupCode = 0;
				int configuredPaymentCode = 0;
				
				String configData=null;
				String code=null;
				String serviceName=null;
				for(String str:merchantPrefixData) {
					configData = str;
					String[] s = StringUtilities.tokenizeString(configData, ',');
					code = s[0];
					if(s[1].length()>0)
						serviceName = s[1];
					if(CmFinoFIX.VAServiceName_PREPAID_TOPUP.equals(serviceName))
						configuredTopupCode = Integer.parseInt(code);
					else if(CmFinoFIX.VAServiceName_POSTPAID_BILL_PAYMENT.equals(serviceName))
						configuredPaymentCode = Integer.parseInt(code);
					if("1".equals(subType) && !toOperator.getMerchantPrefixCode().equals(configuredPaymentCode)) {
						fromOperator.setResponseCode(CmFinoFIX.ISO8583_Mobile_Operator_Response_Code_ReceiverNotAPrepaidAccount);
						fromOperator.setRejectReason(CmFinoFIX.ISO8583_Mobile_Operator_Response_Code_ReceiverNotAPrepaidAccount.toString());
					}
					else if("0".equals(subType) && !toOperator.getMerchantPrefixCode().equals(configuredTopupCode)) {
						fromOperator.setResponseCode(CmFinoFIX.ISO8583_Mobile_Operator_Response_Code_ReceiverNotAPrepaidAccount);
						fromOperator.setRejectReason(CmFinoFIX.ISO8583_Mobile_Operator_Response_Code_ReceiverNotAPrepaidAccount.toString());
					}
					
				}
				
			}
		}
		}
		catch(Exception e){
			log.error("AccountInquiryFromZTEProcessor Error ",e);
			fromOperator.setResponseCode(CmFinoFIX.ISO8583_Mobile_Operator_Response_Code_Unidentified_Error_Code);
			fromOperator.setRejectReason(CmFinoFIX.ISO8583_Mobile_Operator_Response_Code_Unidentified_Error_Code.toString());
		}
		
		fromOperator.header().setSendingTime(DateTimeUtil.getLocalTime());
		fromOperator.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		return fromOperator;
	}
}
