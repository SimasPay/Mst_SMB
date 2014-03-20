/**
 * 
 */
package com.mfino.transactionapi.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.Transaction;
import com.mfino.exceptions.InvalidDataException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.result.XMLResult;
import com.mfino.service.SystemParametersService;
import com.mfino.transactionapi.handlers.payment.BSIMBillInquiryHandler;
import com.mfino.transactionapi.handlers.payment.BillInquiryHandler;
import com.mfino.transactionapi.handlers.payment.BillPayConfirmHandler;
import com.mfino.transactionapi.handlers.payment.BillPayInquiryHandler;
import com.mfino.transactionapi.handlers.payment.GetThirdPartyDataHandler;
import com.mfino.transactionapi.handlers.payment.QRPaymentConfirmHandler;
import com.mfino.transactionapi.handlers.payment.QRPaymentInquiryHandler;
import com.mfino.transactionapi.service.BaseAPIService;
import com.mfino.transactionapi.service.PaymentAPIService;
import com.mfino.transactionapi.service.TransactionRequestValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * All Transactions Related to Payment Service 
 * BillPay
 * 
 * @author Bala Sunku
 *
 */
@Service("PaymentAPIServiceImpl")
public class PaymentAPIServiceImpl extends BaseAPIService implements PaymentAPIService{

	@Autowired
	@Qualifier("BillInquiryHandlerImpl")
	private BillInquiryHandler billInquiryHandler;

	@Autowired
	@Qualifier("BillPayInquiryHandlerImpl")
	private BillPayInquiryHandler billPayInquiryHandler;

	@Autowired
	@Qualifier("BillPayConfirmHandlerImpl")
	private BillPayConfirmHandler billPayConfirmHandler;

	@Autowired
	@Qualifier("BSIMBillInquiryHandlerImpl")
	private BSIMBillInquiryHandler bsimBillInquiryHandler;

	@Autowired
	@Qualifier("GetThirdPartyDataHandlerImpl")
	private GetThirdPartyDataHandler getThirdPartyDataHandler;

	@Autowired
	@Qualifier("TransactionRequestValidationServiceImpl")
	private TransactionRequestValidationService transactionRequestValidationService;

	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("QRPaymentInquiryHandlerImpl")
	private QRPaymentInquiryHandler qrPaymentInquiryHandler;

	@Autowired
	@Qualifier("QRPaymentConfirmHandlerImpl")
	private QRPaymentConfirmHandler qrPaymentConfirmHandler;

	private String serviceName = ServiceAndTransactionConstants.SERVICE_PAYMENT;

	public XMLResult handleRequest(TransactionDetails transactionDetails) throws InvalidDataException {
		XMLResult xmlResult = null;

		String sourceMessage =transactionDetails.getSourceMessage();
		String transactionName = transactionDetails.getTransactionName();

		if (ServiceAndTransactionConstants.TRANSACTION_BILL_PAY_INQUIRY.equalsIgnoreCase(transactionName)) {
			String billerCode = systemParametersService.getString(SystemParameterKeys.STARTIMES_BILLER_CODE);
			transactionRequestValidationService.validateBillPayInquiryDetails(transactionDetails);			
			if(billerCode!=null && billerCode.equals(transactionDetails.getBillerCode())){
				if (StringUtils.isBlank(sourceMessage)) {
					sourceMessage = ServiceAndTransactionConstants.MESSAGE_STARTIMES_PAYMENT;
				}
				transactionDetails.setSourceMessage(sourceMessage);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_STARTIMES_PAYMENT);
				transactionDetails.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_STARTIMES_PAYMENT);				
			}
			else{

				if (StringUtils.isBlank(sourceMessage)) {
					sourceMessage = ServiceAndTransactionConstants.MESSAGE_BILL_PAY;
				}
				transactionDetails.setSourceMessage(sourceMessage);
				transactionDetails.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_BILL_PAY);
				if((StringUtils.isNotBlank(transactionDetails.getPaymentMode())) && (CmFinoFIX.PaymentMode_ZeroAmount.equalsIgnoreCase(transactionDetails.getPaymentMode()) 
						|| CmFinoFIX.PaymentMode_PackageType.equalsIgnoreCase(transactionDetails.getPaymentMode()))){
					xmlResult = (XMLResult) bsimBillInquiryHandler.handle(transactionDetails);
					return xmlResult;
				}else{
					transactionDetails.setOnBehalfOfMDN(transactionDetails.getBillNum());//changes done as per ticket NUM 3396					
				}
			}
			xmlResult = (XMLResult) billPayInquiryHandler.handle(transactionDetails);
		}
		else if(ServiceAndTransactionConstants.TRANSACTION_BILL_PAY.equalsIgnoreCase(transactionName)) {
			transactionRequestValidationService.validateBillPayConfirmDetails(transactionDetails);
			xmlResult = (XMLResult) billPayConfirmHandler.handle(transactionDetails);

		}else if(ServiceAndTransactionConstants.TRANSACTION_BILL_INQUIRY.equalsIgnoreCase(transactionName)) {
			transactionRequestValidationService.validateBillInquiryDetails(transactionDetails);
			String billerCode = systemParametersService.getString(SystemParameterKeys.STARTIMES_BILLER_CODE);
			if(billerCode!=null && billerCode.equals(transactionDetails.getBillerCode())){
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_STARTIMES_QUERY_BALANCE);
				transactionDetails.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_STARTIMES_QUERY_BALANCE);
			}
			xmlResult = (XMLResult) billInquiryHandler.handle(transactionDetails);

		}
		else if (ServiceAndTransactionConstants.TRANSACTION_FRSC_PAYMENT_INQUIRY.equalsIgnoreCase(transactionName)) {
			transactionRequestValidationService.validateFRSCPaymentInquiryDetails(transactionDetails);

			if (StringUtils.isBlank(sourceMessage)) {
				sourceMessage = ServiceAndTransactionConstants.MESSAGE_FRSC_PAYMENT;
				transactionDetails.setSourceMessage(sourceMessage);
			}

			String frscCode = systemParametersService.getString(SystemParameterKeys.FRSC_PAYMENT_CODE);

			transactionDetails.setBillerCode(frscCode);
			transactionDetails.setSourceMessage(sourceMessage);
			transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_FRSC_PAYMENT);			
			xmlResult = (XMLResult) billPayInquiryHandler.handle(transactionDetails);
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_FRSC_PAYMENT.equalsIgnoreCase(transactionName)) {
			transactionRequestValidationService.validateFRSCPaymentConfirmDetails(transactionDetails);

			String frscCode = systemParametersService.getString(SystemParameterKeys.FRSC_PAYMENT_CODE);
			transactionDetails.setBillerCode(frscCode);
			xmlResult = (XMLResult) billPayConfirmHandler.handle(transactionDetails);
		}else if (ServiceAndTransactionConstants.TRANSACTION_QR_PAYMENT_INQUIRY.equalsIgnoreCase(transactionName)) {
			transactionRequestValidationService.validateBillPayInquiryDetails(transactionDetails);
			String flashizCode = systemParametersService.getString(SystemParameterKeys.FLASHIZ_BILLER_CODE);
			sourceMessage = ServiceAndTransactionConstants.MESSAGE_QR_PAYMENT;
			transactionDetails.setBillerCode(flashizCode);
			transactionDetails.setSourceMessage(sourceMessage);
			transactionDetails.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_QR_PAYMENT);
			xmlResult = (XMLResult) qrPaymentInquiryHandler.handle(transactionDetails);
		}else if (ServiceAndTransactionConstants.TRANSACTION_QR_PAYMENT.equalsIgnoreCase(transactionName)) {
			transactionRequestValidationService.validateBillPayConfirmDetails(transactionDetails);
			String flashizCode = systemParametersService.getString(SystemParameterKeys.FLASHIZ_BILLER_CODE);
			transactionDetails.setBillerCode(flashizCode);
			xmlResult = (XMLResult) qrPaymentConfirmHandler.handle(transactionDetails);
		}
		else if(ServiceAndTransactionConstants.TRANSACTION_GET_THIRD_PARTY_DATA.equals(transactionName)){

			xmlResult = (XMLResult) getThirdPartyDataHandler.handle(transactionDetails);

		}else{
			xmlResult = new XMLResult();
			xmlResult.setLanguage(CmFinoFIX.Language_English);
			xmlResult.setTransactionTime(new Timestamp());
			xmlResult.setNotificationCode(CmFinoFIX.NotificationCode_TransactionNotAvailable);
		}
		return xmlResult;
	}
	
	private boolean isAmountZero(TransactionDetails transactionDetails){
		if((transactionDetails.getAmount()!=null)&&(!transactionDetails.getAmount().equals("0"))){
			return false;
		}
		return true;
	}
	private boolean isPaymentModeNotEmpty(TransactionDetails transactionDetails){
		if((StringUtils.isNotBlank(transactionDetails.getPaymentMode()))){
			return true;
		}
		return false;
		}
}