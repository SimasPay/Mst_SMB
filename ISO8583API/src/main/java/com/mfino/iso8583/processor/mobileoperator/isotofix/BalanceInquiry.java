package com.mfino.iso8583.processor.mobileoperator.isotofix;

import java.math.BigDecimal;
import java.util.List;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetMDNBillDebtsFromOperator;
import com.mfino.fix.CmFinoFIX.CMGetMDNBillDebtsToOperator;
import com.mfino.iso8583.processor.mobileoperator.INBSISOtoFIXProcessor;
import com.mfino.iso8583.processor.mobileoperator.MobileOperatorISOMessage;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.StringUtilities;

public class BalanceInquiry implements INBSISOtoFIXProcessor {

	public static List<String> merchantPrefixData;
	
	@Override
	public CFIXMsg process(MobileOperatorISOMessage isoMsg, CFIXMsg request) throws Exception {

		CMGetMDNBillDebtsToOperator toOperator = (CMGetMDNBillDebtsToOperator) request;

		CMGetMDNBillDebtsFromOperator fromOperator = new CMGetMDNBillDebtsFromOperator();
		fromOperator.copy(toOperator);
		fromOperator.setResponseCode(Integer.parseInt(isoMsg.getResponseCode()));

		if (!CmFinoFIX.ISO8583_Mobile_Operator_Response_Code_Success.equals(fromOperator.getResponseCode())) {
			fromOperator.setRejectReason(isoMsg.getResponseCode());
			return fromOperator;
		}

		// fromOperator.header().setMsgSeqNum(null);
		fromOperator.header().setSendingTime(DateTimeUtil.getLocalTime());
		String responseData = isoMsg.getTransactionResponseData();
		String billRefNo = responseData.substring(139, 151);
		String billPaymentDate = responseData.substring(151, 159);
		String totalBillDebts = responseData.substring(172, 183);
		String subName = responseData.substring(109, 139);

		fromOperator.setBillReferenceNumber(Long.parseLong(billRefNo));
		fromOperator.setLastBillPaymentDateYYYYMMDD(Long.parseLong(billPaymentDate));
		fromOperator.setTotalBillDebts(new BigDecimal(totalBillDebts));
		fromOperator.setPayerName(subName);

		if (toOperator.getMerchantPrefixCode() != null) {
			if (CmFinoFIX.ISO8583_Mobile_Operator_ProcessingCode_Postpaid_Inquiry.equals(isoMsg.getProcessingCode().toString())) {
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
		return fromOperator;
	}
}
