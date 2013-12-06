package com.mfino.transactionapi.service.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.IntegrationPartnerMapping;
import com.mfino.domain.MFSBiller;
import com.mfino.exceptions.InvalidDataException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.result.XMLResult;
import com.mfino.service.IntegrationPartnerMappingService;
import com.mfino.transactionapi.handlers.payment.BillPayConfirmHandler;
import com.mfino.transactionapi.handlers.payment.BillPayInquiryHandler;
import com.mfino.transactionapi.service.BaseAPIService;
import com.mfino.transactionapi.service.BuyAPIService;
import com.mfino.transactionapi.service.TransactionRequestValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Sasi
 *
 */
@Service("BuyAPIServiceImpl")
public class BuyAPIServiceImpl extends BaseAPIService implements BuyAPIService{
	
	@Autowired
	@Qualifier("BillPayInquiryHandlerImpl")
	private BillPayInquiryHandler billPayInquiryHandler;

	@Autowired
	@Qualifier("BillPayConfirmHandlerImpl")
	private BillPayConfirmHandler billPayConfirmHandler;
	
	@Autowired
	@Qualifier("IntegrationPartnerMappingServiceImpl")
	private IntegrationPartnerMappingService integrationPartnerMappingService;

	@Autowired
	@Qualifier("TransactionRequestValidationServiceImpl")
	private TransactionRequestValidationService transactionRequestValidationService;

	public static final String IN_CODE_VISAFONE = "1";
	
	protected Logger log = LoggerFactory.getLogger(this.getClass());

	public XMLResult handleRequest(TransactionDetails transactionDetails) throws InvalidDataException {
		log.info("BuyAPIService :: handleRequest BEGIN");
		XMLResult xmlResult = null;
		String sourceMessage = transactionDetails.getSourceMDN();
		String transactionName = transactionDetails.getTransactionName();
		
		String inCode = transactionDetails.getCompanyID();

	
		log.info("BuyApiService handleRequest() inCode = "+inCode);
	
		String IN_ID = SystemParameterKeys.IN_PARTNER_SUFFIX + inCode;
		IntegrationPartnerMapping partnerMap = integrationPartnerMappingService.getByInstitutionID(IN_ID);
		MFSBiller biller = null;
		String billerCode = null;

		if(partnerMap == null){
			log.error("Institution ID " + IN_ID + " doesn't exist.");
			xmlResult = new XMLResult();
			xmlResult.setInstitutionID(IN_ID);
			xmlResult.setNotificationCode(CmFinoFIX.NotificationCode_Integration_InvalidInstituionID);
			return xmlResult;	
		}
		
		biller = partnerMap.getMFSBiller();
		billerCode = biller.getMFSBillerCode();
		transactionDetails.setBillerCode(billerCode);
		
		log.info("BuyApiService handleRequest() partnerCode="+billerCode);
		transactionDetails.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE);
		if (ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE_INQUIRY.equalsIgnoreCase(transactionName)) {
			transactionRequestValidationService.validateAirtimePurchaseInquiryDetails(transactionDetails);

			if (StringUtils.isBlank(sourceMessage)) {
				sourceMessage = ServiceAndTransactionConstants.MESSAGE_AIRTIME_PURCHASE;
			}

			log.info("BuyAPIService :: handleRequest() TRANSACTION_AIRTIME_PURCHASE_INQUIRY partnerCode="+billerCode);
			transactionDetails.setOnBehalfOfMDN(transactionDetails.getBillNum());//changes done as per ticket NUM 3396
			xmlResult = (XMLResult) billPayInquiryHandler.handle(transactionDetails);
		}
		else if(ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE.equalsIgnoreCase(transactionName)) {
			transactionRequestValidationService.validateAirtimePurchaseDetails(transactionDetails);

			if (StringUtils.isBlank(sourceMessage)) {
				sourceMessage = ServiceAndTransactionConstants.MESSAGE_AIRTIME_PURCHASE;
			}

			log.info("BuyAPIService :: handleRequest() TRANSACTION_AIRTIME_PURCHASE partnerCode="+billerCode);

			xmlResult = (XMLResult) billPayConfirmHandler.handle(transactionDetails);
		}

		log.info("BuyAPIService :: handleRequest END");
		return xmlResult;
	}
}
