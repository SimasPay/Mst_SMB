/**
 * 
 */
package com.mfino.stk.processor;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.query.PocketQuery;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberMDN;
import com.mfino.exceptions.InvalidDataException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.result.Result.ResultType;
import com.mfino.result.XMLResult;
import com.mfino.service.PocketService;
import com.mfino.service.SystemParametersService;
import com.mfino.stk.VisafoneEncryptionDecryption;
import com.mfino.stk.validations.RestrictionsValidator;
import com.mfino.stk.validations.ValidationResult;
import com.mfino.stk.vo.STKRequest;
import com.mfino.transactionapi.constants.ApiConstants;
import com.mfino.transactionapi.handlers.SMSRequestHandler;
import com.mfino.transactionapi.vo.TransactionDetails;


/**
 * This class processes requests which are received in encrypted format.
 * 
 * @author Chaitanya
 *
 */
public class EncryptedRequestProcessor extends RequestProcessor{

	private static Logger log = LoggerFactory.getLogger(EncryptedRequestProcessor.class);
	public static final String	         STK_DELIMETER	         = "*";
	public static final String	         EMONEY_POCKET_TYPE_CODE	= "1";
	public static final String	         BANK_POCKET_TYPE_CODE	 = "2";
	public static final String	         CHANNEL_CODE	         = "6";

	private VisafoneEncryptionDecryption	visafoneEncryptionDecryption;
	
	private SystemParametersService systemParametersService;

	private SMSRequestHandler smsRequestHandler ;
	
	private PocketService pocketService ;
	
	public PocketService getPocketService() {
		return pocketService;
	}

	public void setPocketService(PocketService pocketService) {
		this.pocketService = pocketService;
	}

	private List<RestrictionsValidator> validators;
	
	@Override
	public XMLResult processRequest(STKRequest stkRequest) {
		XMLResult result = new XMLResult();
		// Get the Decrypted message
		try {
			if (stkRequest != null && StringUtils.isBlank(stkRequest.getSourceMDN())) {
				log.error("Source MDN is Null");
				result.setMessage("Source MDN is Null");
				return result;
			}
			if (stkRequest != null && !StringUtils.isBlank(stkRequest.getRequestMsg())) {
				log.info("Received Message from MDN: " + stkRequest.getSourceMDN() + ".Going for decoding and decryption");
				stkRequest = visafoneEncryptionDecryption.process(stkRequest);
				log.info("Decrypted Message for MDN: " + stkRequest.getSourceMDN() + " --> " + stkRequest.getSecuredDecryptedRequestMsg());
			}
			else {
				log.error("Request message is Null");
				result.setMessage("Request message is Null");
				return result;
			}
		}
		catch (Exception e) {
			log.error("Error while decrypting the message from MDN: " + stkRequest.getSourceMDN(), e);
			result.setMessage("Invalid request received.Please try again");
			return result;
		}

		// Generates the Transaction detail based on the received message
		if (StringUtils.isBlank(stkRequest.getDecryptedRequestMsg())) {
			log.info("Decrypted message is Null");
			result.setMessage("Invalid request received.Please try again");
			return result;
		}
		TransactionDetails transactionDetails = new TransactionDetails();
		try{
			 transactionDetails = parseRequest(stkRequest);
			transactionDetails.setChannelCode(CHANNEL_CODE);
			transactionDetails.setResultType(ResultType.XML);
		}
		catch (InvalidDataException dataEx) {
			log.error(dataEx.getLogMessage());
			result.setMessage("Invalid request received.Please try again");
			return result;
		}


		
		//true as long there is only visafonestk
		if("BS01".equalsIgnoreCase(transactionDetails.getTransactionCode())||"M07".equalsIgnoreCase(transactionDetails.getTransactionCode())){
			log.warn("received a self topup request for mdn="+transactionDetails.getSourceMDN()+".Setting the company to 1 diregarding operatorcode sent in request");
			transactionDetails.setCompanyID("1");
		}
		if("BS02".equalsIgnoreCase(transactionDetails.getTransactionCode()) || "M08".equalsIgnoreCase(transactionDetails.getTransactionCode())){
			log.info("received a topup others request.Mapping the stk operatorcode to internal companyids");
			log.info("Map--> 1 to 2,2 to 3,3 to 4,4 to 1,5 to 5, 6 to 6");
			if(transactionDetails.getCompanyID().equals("1"))
				transactionDetails.setCompanyID("2");
			else if(transactionDetails.getCompanyID().equals("2"))
				transactionDetails.setCompanyID("3");
			else if(transactionDetails.getCompanyID().equals("3"))
				transactionDetails.setCompanyID("4");
			else if(transactionDetails.getCompanyID().equals("4"))
				transactionDetails.setCompanyID("1");
			else if(transactionDetails.getCompanyID().equals("5"))
				transactionDetails.setCompanyID("5");
			else if(transactionDetails.getCompanyID().equals("6"))
				transactionDetails.setCompanyID("6");
			else 
				transactionDetails.setCompanyID("-1");
		}
		if("B03".equalsIgnoreCase(transactionDetails.getTransactionCode())){ // For agent airtime service B03* the request has different companiID mapping
			log.info("received a topup others request.Mapping the stk operatorcode to internal companyids");
			log.info("Map--> 1 to 4,2 to 3,3 to 2,4 to 1,5 to 5, 6 to 6");
			if(transactionDetails.getCompanyID().equals("1"))
				transactionDetails.setCompanyID("4");
			else if(transactionDetails.getCompanyID().equals("2"))
				transactionDetails.setCompanyID("3");
			else if(transactionDetails.getCompanyID().equals("3"))
				transactionDetails.setCompanyID("2");
			else if(transactionDetails.getCompanyID().equals("4"))
				transactionDetails.setCompanyID("1");
			else if(transactionDetails.getCompanyID().equals("5"))
				transactionDetails.setCompanyID("5");
			else if(transactionDetails.getCompanyID().equals("6"))
				transactionDetails.setCompanyID("6");
			else 
				transactionDetails.setCompanyID("-1");
		}
		log.info("validating the request="+stkRequest.getSecuredDecryptedRequestMsg()+" for mdn="+transactionDetails.getSourceMDN());
		ValidationResult vr = new ValidationResult();
		vr.setXmlResult(result);
		for(int i=0;i<validators.size();i++){
			vr = validators.get(i).validator(transactionDetails, vr);
			if(!vr.isValid())
				return vr.getXmlResult();
		}
		log.info("validations passed");

		log.info("calling smsrequesthandler -->");
		
		result = smsRequestHandler.process(transactionDetails);
		
		return result;
	}

	/**
	 * Generates the Transaction Details object
	 * 
	 * @param stkRequest
	 * @return
	 * @throws InvalidDataException 
	 */
	private TransactionDetails parseRequest(STKRequest stkRequest) throws InvalidDataException {
		TransactionDetails transactionDetails = new TransactionDetails();
		transactionDetails.setSourceMDN(stkRequest.getSourceMDN());
		StringTokenizer fields = new StringTokenizer(stkRequest.getDecryptedRequestMsg(), STK_DELIMETER);
		for (int i = 0; fields.hasMoreTokens(); i++) {
			String field = fields.nextToken();
			if (i == 0) {
				transactionDetails.setTransactionCode(field);
			}
			else if (field.startsWith("S")) {
				transactionDetails.setSeqNum(getFieldValue(field, true));
			}
			else if (field.startsWith("P")) {
				transactionDetails.setSourcePIN(getFieldValue(field, true));
			}
			else if (field.startsWith("N")) {
				transactionDetails.setNewPIN(getFieldValue(field, true));
			}
			else if (field.startsWith("C")) {
				transactionDetails.setConfirmPIN(getFieldValue(field, true));
			}
			else if (field.startsWith("A")) {
				if ("R00".equals(transactionDetails.getTransactionCode())) {
					transactionDetails.setApplicationId(getFieldValue(field, true));
				} 
				else {
					transactionDetails.setAmount(new BigDecimal(getFieldValue(field, true)));
				}
			}
			else if (field.startsWith("BC")) {
				transactionDetails.setDestBankCode(getFieldValue(field.substring(1), true));
			}
			else if(field.startsWith("OC")){
				transactionDetails.setCompanyID(""+ field.charAt(field.length()-1));
			}
			else if (field.startsWith("B")) {
				transactionDetails.setBillNum(getFieldValue(field, true));
			}
			else if (field.startsWith("D")) {
				if ("TS07".equals(transactionDetails.getTransactionCode())) {
					transactionDetails.setDestAccountNumber(getFieldValue(field, true));
				}
				else if ("BS01".equals(transactionDetails.getTransactionCode()) || "M07".equals(transactionDetails.getTransactionCode())
						|| "BS03".equals(transactionDetails.getTransactionCode()) ) {
					transactionDetails.setDestMDN(transactionDetails.getSourceMDN());
				}
				else if ("B01".equals(transactionDetails.getTransactionCode())) {
					transactionDetails.setBillerCode(getFieldValue(field, true));
				}
				else if ("B03".equals(transactionDetails.getTransactionCode())||"M08".equals(transactionDetails.getTransactionCode())) {
					transactionDetails.setCompanyID(""+ field.charAt(field.length()-1));
				}
				else if ("RS01".equals(transactionDetails.getTransactionCode())) {
					transactionDetails.setDateOfBirth(getDate(getFieldValue(field, true)));
				}
				else {
					transactionDetails.setDestMDN(getFieldValue(field, true));
				}
			}
			else if (field.startsWith("F")) {
				if("B04".equals(transactionDetails.getTransactionCode())) {
					transactionDetails.setSecreteCode(getFieldValue(field, true));
				}
				else{
				transactionDetails.setFirstName(getFieldValue(field, true));
				}
			}
			else if (field.startsWith("L")) {
				transactionDetails.setLastName(getFieldValue(field, true));
			}
			else if (field.startsWith("O")) {
				transactionDetails.setActivationOTP(getFieldValue(field, true));
			}
			else if (field.startsWith("M")) {
				transactionDetails.setDestMDN(getFieldValue(field, true));
			}
			else { 
				if ("R00".equals(transactionDetails.getTransactionCode())) {
					Date date = getDate(getFieldValue(field, false));

					transactionDetails.setDateOfBirth(date);
				}
				else if (field.startsWith("0")) {
					transactionDetails.setActivationOTP(getFieldValue(field, true));
				}
			}
		}
		transactionDetails = getServiceTransactionDetails(transactionDetails);
		return transactionDetails;
	}
	
	//Airtime and Shopping , Billpay requests need to be handled.
	private TransactionDetails getServiceTransactionDetails(TransactionDetails transactionDetails) {
		if (transactionDetails != null) {
			if ("AS01".equals(transactionDetails.getTransactionCode())) { // Subscriber Activation
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_ACTIVATION);
				transactionDetails.setIsHttps(true);
			}
			else if ("M01".equals(transactionDetails.getTransactionCode())) { // Agent Activation
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_AGENTACTIVATION);
				transactionDetails.setIsHttps(true);
			}
			else if ("MS01".equals(transactionDetails.getTransactionCode()) || "M02".equals(transactionDetails.getTransactionCode())) { // Change Pin
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_CHANGEPIN);
			}
			else if ("MS02".equals(transactionDetails.getTransactionCode()) || "M03".equals(transactionDetails.getTransactionCode()) || 
					"M04".equals(transactionDetails.getTransactionCode())) { // Check Balance
				if ("01".equals(transactionDetails.getDestMDN())) {
					transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
				}
				else {
					transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_BANK);
					transactionDetails.setSourcePocketCode(BANK_POCKET_TYPE_CODE);
				}
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_CHECKBALANCE);
			}
			else if ("MS03".equals(transactionDetails.getTransactionCode()) || "M10".equals(transactionDetails.getTransactionCode())) { // Mini Statement
				if ("01".equals(transactionDetails.getDestMDN())) {
					transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
				}
				else {
					transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_BANK);
					transactionDetails.setSourcePocketCode(BANK_POCKET_TYPE_CODE);
				}
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_HISTORY);
			}
			else if ("MS04".equals(transactionDetails.getTransactionCode())) { // Cash Out
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_CASHOUT);
				transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_CASH_OUT);
				transactionDetails.setSourcePocketCode(EMONEY_POCKET_TYPE_CODE);
				transactionDetails.setPartnerCode(transactionDetails.getDestMDN());
			}
			else if ("MS05".equals(transactionDetails.getTransactionCode())) { // Cash Out at atm
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_CASHOUT_AT_ATM);
				transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_CASH_OUT);
				transactionDetails.setSourcePocketCode(EMONEY_POCKET_TYPE_CODE);
				transactionDetails.setPartnerCode(transactionDetails.getDestMDN());
			}
			else if ("TS01".equals(transactionDetails.getTransactionCode()) || "T01".equals(transactionDetails.getTransactionCode())) { // Transfers to Others Emoney to Emoney 
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER);
				transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_MOBILE_TRANSFER);
				transactionDetails.setSourcePocketCode(EMONEY_POCKET_TYPE_CODE);
				transactionDetails.setDestPocketCode(EMONEY_POCKET_TYPE_CODE);
			}
			else if ("TS02".equals(transactionDetails.getTransactionCode())) { // Self Bank to Emoney Transfer
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_BANK);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER);
				transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_MOBILE_TRANSFER);
				transactionDetails.setSourcePocketCode(BANK_POCKET_TYPE_CODE);
				transactionDetails.setDestPocketCode(EMONEY_POCKET_TYPE_CODE);
				transactionDetails.setDestMDN(transactionDetails.getSourceMDN());
			}
			else if ("M05".equals(transactionDetails.getTransactionCode())) { // Self Bank to Emoney Transfer if 0 and bank to emoney if 1)
				if(transactionDetails.getDestMDN().equals("0")||transactionDetails.getDestMDN().equals("00")){ //code is set into destination mdn.
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_BANK);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER);
				transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_MOBILE_TRANSFER);
				transactionDetails.setSourcePocketCode(BANK_POCKET_TYPE_CODE);
				transactionDetails.setDestPocketCode(EMONEY_POCKET_TYPE_CODE);
				transactionDetails.setDestMDN(transactionDetails.getSourceMDN());
			}
				if(transactionDetails.getDestMDN().equals("1")||transactionDetails.getDestMDN().equals("01")){
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER);
				transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_MOBILE_TRANSFER);
				transactionDetails.setSourcePocketCode(EMONEY_POCKET_TYPE_CODE);
				transactionDetails.setDestPocketCode(BANK_POCKET_TYPE_CODE);
				transactionDetails.setDestMDN(transactionDetails.getSourceMDN());
				}
			}
			else if ("TS03".equals(transactionDetails.getTransactionCode()) ) { // Self Emoney to Bank Transfer 
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER);
				transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_MOBILE_TRANSFER);
				transactionDetails.setSourcePocketCode(EMONEY_POCKET_TYPE_CODE);
				transactionDetails.setDestPocketCode(BANK_POCKET_TYPE_CODE);
				transactionDetails.setDestMDN(transactionDetails.getSourceMDN());
			}
			else if ("TS04".equals(transactionDetails.getTransactionCode())||"T02".equals(transactionDetails.getTransactionCode())) { // Transfers to Others Emoney to Bank
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER);
				transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_MOBILE_TRANSFER);
				transactionDetails.setSourcePocketCode(EMONEY_POCKET_TYPE_CODE);
				transactionDetails.setDestPocketCode(BANK_POCKET_TYPE_CODE);
				transactionDetails = setDestMDNForBankTransfer(transactionDetails);
			}			
			else if ("TS05".equals(transactionDetails.getTransactionCode()) ||"T03".equals(transactionDetails.getTransactionCode())) { // Transfers to Others Bank to Emoney
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_BANK);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER);
				transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_MOBILE_TRANSFER);
				transactionDetails.setSourcePocketCode(BANK_POCKET_TYPE_CODE);
				transactionDetails.setDestPocketCode(EMONEY_POCKET_TYPE_CODE);
			}
			else if ("TS06".equals(transactionDetails.getTransactionCode()) || "T04".equals(transactionDetails.getTransactionCode())) { // Transfers to Others Bank to Bank
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_BANK);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER);
				transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_MOBILE_TRANSFER);
				transactionDetails.setSourcePocketCode(BANK_POCKET_TYPE_CODE);
				transactionDetails.setDestPocketCode(BANK_POCKET_TYPE_CODE);
				transactionDetails = setDestMDNForBankTransfer(transactionDetails);
			}
			else if ("TS07".equals(transactionDetails.getTransactionCode())) { // Transfers to Others Bank to Bank (Inter Bank Transfers)
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_BANK);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_INTERBANK_TRANSFER);
				transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_INTERBANK_TRANSFER);
				transactionDetails.setSourcePocketCode(BANK_POCKET_TYPE_CODE);
			}
			else if ("A01".equals(transactionDetails.getTransactionCode())) { // Agent to Agent Transfer Emoney
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_AGENT_TO_AGENT_TRANSFER);
				transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_AGENT_AGENT_TRANSFER);
				transactionDetails.setSourcePocketCode(EMONEY_POCKET_TYPE_CODE);
				transactionDetails.setPartnerCode(transactionDetails.getDestMDN());
			}
			else if ("R00".equals(transactionDetails.getTransactionCode())) { // Subscriber Registration
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_AGENT);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_SUBSCRIBERREGISTRATION);
				transactionDetails.setAccountType("1"); // Setting the Default UnBanked Account type
			}
			else if ("B00".equals(transactionDetails.getTransactionCode())) { // Cash In
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_AGENT);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_CASHIN);
				transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_CASH_IN);
				transactionDetails.setDestPocketCode(EMONEY_POCKET_TYPE_CODE);
			}
			else if ("BS04".equals(transactionDetails.getTransactionCode())||"M11".equals(transactionDetails.getTransactionCode())) { // Shopping 
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_SHOPPING);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_PURCHASE);
				transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_PURCHASE);
				transactionDetails.setSourcePocketCode(EMONEY_POCKET_TYPE_CODE);
				transactionDetails.setDestPocketCode(EMONEY_POCKET_TYPE_CODE);
				transactionDetails.setPartnerCode(transactionDetails.getDestMDN());
			}
			else if ("BS01".equals(transactionDetails.getTransactionCode()) || "BS02".equals(transactionDetails.getTransactionCode()) ||
					"M07".equals(transactionDetails.getTransactionCode()) ||"BS03".equals(transactionDetails.getTransactionCode())) { // Buy (Air time purchase)
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_BUY);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE);
				transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_AIRTIME_PURCHASE);
				transactionDetails.setSourcePocketCode(EMONEY_POCKET_TYPE_CODE);
//				transactionDetails.setCompanyID("1"); // Setting the CompanyId as 1 for Visafone Top-up
			}
			else if ("B01".equals(transactionDetails.getTransactionCode())) { // Agent Bill Pay (DSTV) 
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_AGENT);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_AGENT_BILL_PAY);
				transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_AGENT_BILL_PAY);
				transactionDetails.setSourcePocketCode(EMONEY_POCKET_TYPE_CODE);
			}
			else if ("B02".equals(transactionDetails.getTransactionCode()) ) { // Buy (Air time purchase Agent Service)
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_AGENT);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE);
				transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_AIRTIME_PURCHASE);
				transactionDetails.setSourcePocketCode(EMONEY_POCKET_TYPE_CODE);
				transactionDetails.setCompanyID("1"); // Setting the CompanyId as 1 for Visafone Top-up
			}			
			else if ("B03".equals(transactionDetails.getTransactionCode()) ) { // Air time purchase Agent Service)
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_AGENT);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE);
				transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_AIRTIME_PURCHASE);
				transactionDetails.setSourcePocketCode(EMONEY_POCKET_TYPE_CODE);
			}
			else if ("B04".equals(transactionDetails.getTransactionCode()) ) { // Agent : cashout to unregistered
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_AGENT);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_CASHOUT_UNREGISTERED);
				transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_CASH_OUT_UNREGISTERED);
				transactionDetails.setSourcePocketCode(EMONEY_POCKET_TYPE_CODE);
			}			
			else if ("PS01".equals(transactionDetails.getTransactionCode()) || "M09".equals(transactionDetails.getTransactionCode()) ) { // Bill Pay (DSTV) 
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_PAYMENT);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_BILL_PAY);
				transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_BILL_PAY);
				transactionDetails.setSourcePocketCode(EMONEY_POCKET_TYPE_CODE);
				transactionDetails.setBillerCode(transactionDetails.getDestMDN());
			}
			else if ("RS01".equals(transactionDetails.getTransactionCode())) { // Subscriber self Registration
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_SUBSCRIBERREGISTRATION);
				transactionDetails.setAccountType("1"); // Setting the Default UnBanked Account type
			}
			else if ("M08".equals(transactionDetails.getTransactionCode()) ) { // Buy Airtime by agent self
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_BUY);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE);
				transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_AIRTIME_PURCHASE);
				transactionDetails.setSourcePocketCode(EMONEY_POCKET_TYPE_CODE);
			}
		}
		return transactionDetails;
	}

	private String getFieldValue(String value, boolean ignoreFirstChar) {
		String result = StringUtils.EMPTY;
		if (StringUtils.isNotBlank(value)) {
			if (ignoreFirstChar) {
				if (value.endsWith("#")) {
					result = value.substring(1, value.length() - 1);
				}
				else {
					result = value.substring(1);
				}
			}
			else {
				if (value.endsWith("#")) {
					result = value.substring(0, value.length() - 1);
				}
				else {
					result = value.substring(0);
				}
			}
		}

		return result;
	}
	
	/**
	 * Calculate the Destination MDN in case of Bank Transfers as the user provides only the Bank Account number.
	 * @param txnDetails
	 */
	private TransactionDetails setDestMDNForBankTransfer(TransactionDetails txnDetails) {
		txnDetails.setDestinationBankAccountNo(txnDetails.getDestMDN());
		String accountNo = txnDetails.getDestinationBankAccountNo();
//		PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
		PocketQuery query = new PocketQuery();
		query.setCardPan(accountNo);
		List<Pocket> pockets = pocketService.get(query);
		if( CollectionUtils.isNotEmpty(pockets)) {
			Pocket pocket = pockets.get(0);
			SubscriberMDN subscriberMDN = pocket.getSubscriberMDNByMDNID();
			txnDetails.setDestMDN(subscriberMDN.getMDN());
		} else {
			txnDetails.setDestMDN(systemParametersService.getString(SystemParameterKeys.PLATFORM_DUMMY_SUBSCRIBER_MDN));
		}
		return txnDetails;
	}
	public Date getDate(String dateStr) throws InvalidDataException {
		Date dateOfBirth;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
			dateFormat.setLenient(false);
			dateOfBirth = dateFormat.parse(dateStr);
		} catch (ParseException e) {
			log.error("Exception in Registration: Invalid Date",e);
			throw new InvalidDataException("Invalid Date", CmFinoFIX.NotificationCode_InvalidData, ApiConstants.PARAMETER_DOB);
		}		
		return dateOfBirth;
	}

	public List<RestrictionsValidator> getValidators() {
	    return validators;
    }

	public void setValidators(List<RestrictionsValidator> validators) {
	    this.validators = validators;
    }

	public SystemParametersService getSystemParametersService() {
		return systemParametersService;
	}

	public void setSystemParametersService(
			SystemParametersService systemParametersService) {
		this.systemParametersService = systemParametersService;
	}

	public SMSRequestHandler getSmsRequestHandler() {
		return smsRequestHandler;
	}

	public void setSmsRequestHandler(SMSRequestHandler smsRequestHandler) {
		this.smsRequestHandler = smsRequestHandler;
	}

	public VisafoneEncryptionDecryption getVisafoneEncryptionDecryption() {
		return visafoneEncryptionDecryption;
	}

	public void setVisafoneEncryptionDecryption(VisafoneEncryptionDecryption visafoneEncryptionDecryption) {
		this.visafoneEncryptionDecryption = visafoneEncryptionDecryption;
	}

}
