/**
 * 
 */
package com.mfino.stk.processor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.exceptions.InvalidDataException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.result.Result.ResultType;
import com.mfino.result.XMLResult;
import com.mfino.stk.vo.STKRequest;
import com.mfino.transactionapi.constants.ApiConstants;
import com.mfino.transactionapi.handlers.impl.SMSRequestHandlerImpl;
import com.mfino.transactionapi.handlers.impl.TransactionRequestHandlerImpl;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * This class processes requests which are received in plain text format.
 * 
 * @author Chaitanya
 *
 */
public class PlainTextRequestProcessor extends RequestProcessor{

	public Log	                         log	                 = LogFactory.getLog(this.getClass());
	public static final String	         STK_DELIMETER	         = " ";
	public static final String	         EMONEY_POCKET_TYPE_CODE	= "1";
	public static final String	         BANK_POCKET_TYPE_CODE	 = "2";
	public static final String	         CHANNEL_CODE	        = "10";
	public static final String			 REGISTRATION_IDENTIFIER = "Reg";
	public static final String			UNREGISTEREDCASHOUT_IDENTIFIER = "CO";
	public static final String			 DOB_FORMAT				 = "ddMMyyyy";
		
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
			if (stkRequest == null || StringUtils.isBlank(stkRequest.getRequestMsg())) {
				log.error("Request message is Null");
				result.setMessage("Request message is Null");
				return result;
			}
		}
		catch (Exception e) {
			log.error("Error while decrypting the message from MDN: " + stkRequest.getSourceMDN(), e);
			result.setMessage("Error while decrypting the message from MDN: " + stkRequest.getSourceMDN());
			return result;
		}
		
		TransactionDetails transactionDetails = new TransactionDetails();
		try {
			transactionDetails = parseRequest(stkRequest);
			transactionDetails.setChannelCode(CHANNEL_CODE);
			
			transactionDetails.setResultType(ResultType.XML);

			//Call SMS Request handlet //TODO need to think about the logic here as we need to move to Backend services
			SMSRequestHandlerImpl smsRequestHandler = new SMSRequestHandlerImpl();
			result = smsRequestHandler.process(transactionDetails);
		} catch (InvalidDataException dataEx) {
			log.error(dataEx.getLogMessage());
			result = TransactionRequestHandlerImpl.getXMLError(dataEx.getNotificationCode(), transactionDetails.getSourceMDN(), dataEx.getKeyValueMap());
		}
		
		return result;
	}

	/**
	 * Generates the Transaction Details object
	 * 
	 * @param stkRequest
	 * @return
	 */
	private TransactionDetails parseRequest(STKRequest stkRequest) throws InvalidDataException{
		TransactionDetails transactionDetails = new TransactionDetails();
		transactionDetails.setSourceMDN(stkRequest.getSourceMDN());
		StringTokenizer fields = new StringTokenizer(stkRequest.getRequestMsg(), STK_DELIMETER);
		int count = fields.countTokens();
		
		if(count>0)
		{
			String firstToken = fields.nextToken();
			if(REGISTRATION_IDENTIFIER.equalsIgnoreCase(firstToken.trim()))
			{
				transactionDetails.setTransactionCode(REGISTRATION_IDENTIFIER);
				transactionDetails = getSelfRegistrationDetails(transactionDetails, fields);
			}
			else if(UNREGISTEREDCASHOUT_IDENTIFIER.equalsIgnoreCase(firstToken.trim()))
			{
				transactionDetails.setTransactionCode(UNREGISTEREDCASHOUT_IDENTIFIER);
				transactionDetails = getCashOutDetails(transactionDetails,fields);
			}
		}
		else
		{
			throw new InvalidDataException("Requested String format is not supported", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, ApiConstants.PARAMETER_SRC_MESSAGE);
		}
		transactionDetails = getServiceTransactionDetails(transactionDetails);
		return transactionDetails;
	}
	
	//Airtime and Shopping , Billpay requests need to be handled.
	private TransactionDetails getServiceTransactionDetails(TransactionDetails transactionDetails) {
		if (transactionDetails != null) {
			if (REGISTRATION_IDENTIFIER.equalsIgnoreCase(transactionDetails.getTransactionCode())) { // Self Registration
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_ACCOUNT);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_SUBSCRIBERREGISTRATION);
				transactionDetails.setIsHttps(true);
			}
			else if (UNREGISTEREDCASHOUT_IDENTIFIER.equalsIgnoreCase(transactionDetails.getTransactionCode())){
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_AGENT);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_CASHOUT_UNREGISTERED);
				transactionDetails.setIsHttps(true);
			}
			
		}
		return transactionDetails;
	}

	private TransactionDetails getSelfRegistrationDetails(TransactionDetails transactionDetails, StringTokenizer fields) throws InvalidDataException
	{
		int count = fields.countTokens();
		if(count==3)
		{
			for(int index=1; index<=count; index++)
			{
				String token = fields.nextToken();
				if(index==1)
				{
					transactionDetails.setFirstName(token);
				}
				else if(index==2)
				{
					transactionDetails.setLastName(token);
				}
				else if(index==3)
				{
					SimpleDateFormat dateFormat = new SimpleDateFormat(DOB_FORMAT);
					try {
						Date dob = dateFormat.parse(token);
						transactionDetails.setDateOfBirth(dob);
					} catch (ParseException e) {
						throw new InvalidDataException("Date of Birth format should be: "+DOB_FORMAT, CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, ApiConstants.PARAMETER_DOB);
					}
					
				}
			}
		}
		else
		{
			throw new InvalidDataException("Tokens do not match the prescribed format", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, ApiConstants.PARAMETER_SRC_MESSAGE);
		}
		
		return transactionDetails;
	}
	private TransactionDetails getCashOutDetails(TransactionDetails transactionDetails, StringTokenizer fields) throws InvalidDataException
	{
		int count = fields.countTokens();
		if(count==3)
		{
			for(int index=1; index<=count; index++)
			{
				String token = fields.nextToken();
				if(index==1)
				{
					transactionDetails.setDestMDN(token);
				}
				else if(index==2)
				{
					transactionDetails.setTransferId(Long.parseLong(token));
				}
				else if(index==3)
				{
					transactionDetails.setSecreteCode(token);
				}
			}
		}
		else
		{
			throw new InvalidDataException("Tokens do not match the prescribed format", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, ApiConstants.PARAMETER_SRC_MESSAGE);
		}
		
		return transactionDetails;
	}
}
