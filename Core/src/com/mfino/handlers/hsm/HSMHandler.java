package com.mfino.handlers.hsm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.constants.GeneralConstants;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMHSMEcryptComponentsRequest;
import com.mfino.fix.CmFinoFIX.CMHSMEcryptComponentsResponse;
import com.mfino.fix.CmFinoFIX.CMHSMKeyExchangeRequest;
import com.mfino.fix.CmFinoFIX.CMHSMKeyExchangeResponse;
import com.mfino.fix.CmFinoFIX.CMHSMOffsetForATMRequest;
import com.mfino.fix.CmFinoFIX.CMHSMOffsetForATMResponse;
import com.mfino.fix.CmFinoFIX.CMHSMOffsetRequest;
import com.mfino.fix.CmFinoFIX.CMHSMOffsetResponse;
import com.mfino.fix.CmFinoFIX.CMHSMPINValidationRequest;
import com.mfino.fix.CmFinoFIX.CMHSMPINValidationResponse;
import com.mfino.fix.CmFinoFIX.CMHSMPinBlockRequest;
import com.mfino.fix.CmFinoFIX.CMHSMPinBlockResponse;
import com.mfino.handlers.FIXMessageHandler;

/**
 * Handler for talking to HSM, currently this handler can talk to hsm for the following messages
 * 1. Offset Generation given hashed PIN and mobile number, which is used as account number
 * 2. PIN validation using hashed pin, offset and mdn
 * 
 * @author POCHADRI
 *
 */
public class HSMHandler extends FIXMessageHandler
{
	private static Logger log = LoggerFactory.getLogger(HSMHandler.class);
	public HSMHandler()
	{
		
	}
	
	/**
	 * Validate PIN
	 * @param mdn mobile number
	 * @param hPin hashed pin
	 * @param offset offset agianst which HSM would compare
	 * @return
	 */
	public String validatePIN(String mdn, String hPin, String offset)
	{
		CMHSMPINValidationRequest pinValidationRequest = new CMHSMPINValidationRequest();
		pinValidationRequest.setSourceMDN(mdn);
		pinValidationRequest.setHPin(hPin);
		pinValidationRequest.setoffset(offset);
		CFIXMsg response =  super.process(pinValidationRequest);
		if(response instanceof CMHSMPINValidationResponse)
			return generateResponse((CMHSMPINValidationResponse) response);
		else
		{
			log.error("Obtained an invalid response for the pin valditaion request, response obtained is "+ response.getClass().getName());
			return GeneralConstants.LOGIN_RESPONSE_INTERNAL_ERROR;
		}
	}
	
	private String generateResponse(CMHSMPINValidationResponse response)
	{
		String hsmResponse = response.getHSMResponseCode();
		log.info("response from hsm:"+hsmResponse);
		//TODO
		if(hsmResponse.equals(GeneralConstants.LOGIN_RESPONSE_SUCCESS) || hsmResponse.equals(GeneralConstants.LOGIN_RESPONSE_FAILED)){
			return hsmResponse;
		}
		else{
			return GeneralConstants.LOGIN_RESPONSE_INTERNAL_ERROR;
		}
	}
	
	/**
	 * Handle reponse that this handler can do remaining send them to super class
	 * ideally they should not come
	 */
	public CFIXMsg handleResponse(CFIXMsg pMsg) 
	{
		if(pMsg instanceof CMHSMPINValidationResponse ||
			pMsg instanceof CMHSMOffsetResponse	 ||
			pMsg instanceof CMHSMKeyExchangeResponse ||
			pMsg instanceof CMHSMPinBlockResponse ||
			pMsg instanceof CMHSMEcryptComponentsResponse)
			return pMsg;
		else 
			return super.handleResponse(pMsg);
	}
	
	/**
	 * Generate offset given mdn and hashed pin, mdn is used as account number
	 * @param mdn mobile number
	 * @param hPin  hashed pin
	 * @return
	 */
	public String generateOffset(String mdn, String hPin) throws Exception
	{
		CMHSMOffsetRequest pinValidationRequest = new CMHSMOffsetRequest();
		pinValidationRequest.setSourceMDN(mdn);
		pinValidationRequest.setHPin(hPin);
		
		CFIXMsg response =  super.process(pinValidationRequest);
		if(response instanceof CMHSMOffsetResponse)
		{
			CMHSMOffsetResponse offsetResponse = (CMHSMOffsetResponse) response;
			if("00".equals(offsetResponse.getHSMResponseCode()))
			{
				return ((CMHSMOffsetResponse) response).getoffset();
			}
			throw new Exception("Error generating offset");
		}
		else
		{
			log.error("Obtained an invalid response for the pin valditaion request, response obtained is "+ response.getClass().getName());
			return null;
		}
	}

	public String generateOffsetForATMRequest(String sourceMDN,String accountNumber,String encryptedPin) throws Exception {

		CMHSMOffsetForATMRequest offsetRequest = new CMHSMOffsetForATMRequest();
		log.info("HSMHandler :: generateOffsetForATMRequest :: sourceMDN="+sourceMDN);
		log.info("HSMHandler :: generateOffsetForATMRequest :: encryptedPin="+encryptedPin);
		offsetRequest.setAccountNumber(accountNumber);
		offsetRequest.setHPin(encryptedPin);
		offsetRequest.setSourceMDN(sourceMDN);
		CFIXMsg response =  super.process(offsetRequest);
		if(response instanceof CMHSMOffsetForATMResponse)
		{
			CMHSMOffsetForATMResponse offsetResponse = (CMHSMOffsetForATMResponse) response;
			if("00".equals(offsetResponse.getHSMResponseCode()))
			{

				log.info("HSMHandler :: generateOffsetForATMRequest :: offset="+((CMHSMOffsetResponse) response).getoffset());
				return ((CMHSMOffsetResponse) response).getoffset();
			}
			throw new Exception("Error generating offset");
		}
		else
		{
			log.error("Obtained an invalid response for the generateOffsetForATMRequest request, response obtained is "+ response.getClass().getName());
			return null;
		}
	}
	
	public String handleKeyExchangeRequest(String encryptedWorkingKey) throws Exception{
		CMHSMKeyExchangeRequest keyExchangeRequest = new CMHSMKeyExchangeRequest();
		log.info("HSMHandler :: handleKeyExchangeRequest :: encryptedWorkingKey="+encryptedWorkingKey);
		keyExchangeRequest.setISO8583_EncryptedWorkingKey(encryptedWorkingKey);
		CFIXMsg response = super.process(keyExchangeRequest);
		if(response instanceof CMHSMKeyExchangeResponse)
		{
			CMHSMKeyExchangeResponse keyExchangeResponse = (CMHSMKeyExchangeResponse) response;
			if("00".equals(keyExchangeResponse.getHSMResponseCode()))
			{

				log.info("HSMHandler :: handleKeyExchangeRequest :: zpkunderlmk="+((CMHSMKeyExchangeResponse) response).getISO8583_EncryptedWorkingKey());
				return ((CMHSMKeyExchangeResponse) response).getISO8583_EncryptedWorkingKey();
			}
			throw new Exception("Error in getting zpkunderlmk");
		}
		else
		{
			log.error("Obtained an invalid response for the handleKeyExchangeRequest request, response obtained is "+ response.getClass().getName());
			return null;
		}
	}
	
	/**
	 * Generate the Pin block for the given clear pin through HSM
	 * @param accountNumber
	 * @param hPin
	 * @return
	 * @throws Exception
	 */
	public String generatePinBlock(String accountNumber, String hPin) throws Exception {
		log.info("HSMHandler :: generatePinBlock for accountnumber: " + accountNumber);
		CMHSMPinBlockRequest pinBlockRequest = new CMHSMPinBlockRequest();
		
		// Convert the given card pan into 12 digit excluding the last check digit.
		String spad = "000000000000";
		String tempPan = accountNumber;
		if (accountNumber.length() <= 12)
			tempPan = accountNumber.substring(0, accountNumber.length() - 1);
		else
			tempPan = accountNumber.substring(accountNumber.length() - 1 - 12, accountNumber.length() - 1);
		if (tempPan.length() < 12)
			tempPan = spad.substring(tempPan.length()) + tempPan;
		
		pinBlockRequest.setAccountNumber(tempPan);
		pinBlockRequest.setHPin(hPin);
		
		CFIXMsg response =  super.process(pinBlockRequest);
		if(response instanceof CMHSMPinBlockResponse)
		{
			CMHSMPinBlockResponse pinBlockResponse = (CMHSMPinBlockResponse) response;
			if("00".equals(pinBlockResponse.getHSMResponseCode())) {
				return ((CMHSMPinBlockResponse) response).getPinBlock();
			}
			throw new Exception("Error generating pinblock");
		}
		else
		{
			log.error("Obtained an invalid response for pin block generation request, response obtained is "+ response.getClass().getName());
			return null;
		}
	}
	
	public String generateEncryptedComponents() throws Exception{
		log.info("HSMHandler :: generateEncryptedComponents ");
		CMHSMEcryptComponentsRequest encryptComponentsRequest = new CMHSMEcryptComponentsRequest();
		CFIXMsg response = super.process(encryptComponentsRequest);
		if(response instanceof CMHSMEcryptComponentsResponse)
		{
			CMHSMEcryptComponentsResponse encryptComponentsResponse = (CMHSMEcryptComponentsResponse) response;
			if("00".equals(encryptComponentsResponse.getHSMResponseCode())) {
				return ((CMHSMEcryptComponentsResponse) response).getKey1();
			}
			throw new Exception("Error generating encrypted component from individual components");
		}
		else
		{
			log.error("Obtained an invalid response for generating encrypted components request, response obtained is "+ response.getClass().getName());
			return null;
		}
	}
}
