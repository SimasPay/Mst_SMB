package com.mfino.hsm.thales7.processor;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.space.Space;
import org.jpos.space.SpaceFactory;
import org.jpos.space.TSpace;

import com.mfino.constants.GeneralConstants;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMHSMBase;
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
import com.mfino.fix.CmFinoFIX.CMHSMRequestBase;
import com.mfino.fix.CmFinoFIX.CMHSMResponseBase;
import com.mfino.hsm.thales7.command.ThalesCommandImplementor;
import com.mfino.hsm.thales7.core.ThalesCore;
import com.mfino.hsm.thales7.core.ThalesMsg;
import com.mfino.mce.core.MCEMessage;

/**
 * Processor used for interacting with HSM
 * Can handle the following messages
 * 1. offset generation request
 * 2. pin validation request 
 * 
 * @author POCHADRI
 *
 */

public class ThalesProcessor
{
	public Log log = LogFactory.getLog(this.getClass());
	
	ThalesCommandImplementor commandImplementor ;

	public ThalesCommandImplementor getCommandImplementor() {
		return commandImplementor;
	}


	public void setCommandImplementor(ThalesCommandImplementor commandImplementor) {
		this.commandImplementor = commandImplementor;
	}


	/**
	 * We get the message here for communicating to hsm
	 * @param hsmMessage hsm message for telling
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CMHSMResponseBase processMessage(MCEMessage mceMessage) 
	
	{
		CFIXMsg hsmMessage = mceMessage.getRequest();
		if(hsmMessage instanceof CMHSMKeyExchangeRequest){
			return processMessageInternal((CMHSMKeyExchangeRequest) hsmMessage);
		}
		else if(hsmMessage instanceof CMHSMPinBlockRequest) {
			return processMessageInternal((CMHSMPinBlockRequest)hsmMessage);
		}
		else if(hsmMessage instanceof CMHSMEcryptComponentsRequest) {
			return processMessageInternal((CMHSMEcryptComponentsRequest)hsmMessage);
		}
		else if(hsmMessage instanceof CMHSMOffsetForATMRequest)
			return processMessageInternal((CMHSMOffsetForATMRequest) hsmMessage);
		else if(hsmMessage instanceof CMHSMOffsetRequest)
			return processMessageInternal((CMHSMOffsetRequest) hsmMessage);
		else if (hsmMessage instanceof CMHSMPINValidationRequest)
			return processMessageInternal((CMHSMPINValidationRequest)hsmMessage);
		else
		{
			log.error("This message cannot be processed by Thales Processor");
			CMHSMResponseBase response = new CMHSMResponseBase();
			response.setHSMResponseCode("07");
			return response;
		}
	}
	
	
	private CMHSMKeyExchangeResponse processMessageInternal(CMHSMKeyExchangeRequest keyExchangeRequest) {
		 
		CMHSMKeyExchangeResponse response = new CMHSMKeyExchangeResponse();
		try
		{
			String zpkunderlmk = commandImplementor.commandFA(keyExchangeRequest.getISO8583_EncryptedWorkingKey());
			response.setISO8583_EncryptedWorkingKey(zpkunderlmk);
			response.setHSMResponseCode("00");
		}
		catch(Exception e)
		{
			log.error("Error talking to hsm",e);
			response.setHSMResponseCode("06");
		}
		return response;
	
	}


	private CMHSMOffsetResponse processMessageInternal(CMHSMOffsetRequest offRequest)
	{ 
		CMHSMOffsetResponse response = new CMHSMOffsetResponse();
		try
		{
			String offset = commandImplementor.createOffset(offRequest.getSourceMDN(),offRequest.getHPin());
			response.setoffset(offset);
			response.setHSMResponseCode("00");
		}
		catch(Exception e)
		{
			log.error("Error talking to hsm",e);
			response.setHSMResponseCode("06");
		}
		return response;
	}
	
	private CMHSMPINValidationResponse processMessageInternal(CMHSMPINValidationRequest pinRequest)
	{
		CMHSMPINValidationResponse response = new CMHSMPINValidationResponse();
		try
		{
			boolean validationResult = commandImplementor.validatePINinHSM(pinRequest.getSourceMDN(),pinRequest.getHPin(),pinRequest.getoffset());
			//response.setoffset(offset);
			if(validationResult)
				response.setHSMResponseCode(GeneralConstants.LOGIN_RESPONSE_SUCCESS);
			else
				response.setHSMResponseCode(GeneralConstants.LOGIN_RESPONSE_FAILED);
		}
		catch(Exception e)
		{
			log.error("Error talking to hsm",e);
			response.setHSMResponseCode("06");
		}
		return response;
	}

	private CMHSMOffsetForATMResponse processMessageInternal(CMHSMOffsetForATMRequest offRequest)
	{ 
		CMHSMOffsetForATMResponse response = new CMHSMOffsetForATMResponse();
		try
		{
			if(null!=offRequest){
				log.info("ThalesProcessor :: processMessageInternal offRequest="+offRequest.DumpFields());
			}
			String offset = commandImplementor.createOffsetForATMRegistration(offRequest.getSourceMDN(),offRequest.getAccountNumber(), offRequest.getHPin());
			log.info("ThalesProcessor :: processMessageInternal offset="+offset);
			response.setoffset(offset);
			response.setHSMResponseCode("00");
		}
		catch(Exception e)
		{
			log.error("Error talking to hsm",e);
			response.setHSMResponseCode("06");
		}
		return response;
	}
	
	private CMHSMPinBlockResponse processMessageInternal(CMHSMPinBlockRequest pinBlockRequest)
	{ 
		CMHSMPinBlockResponse response = new CMHSMPinBlockResponse();
		try
		{
			log.info("Pinblock request = " + pinBlockRequest.DumpFields());
			String pinBlock = commandImplementor.generatePinBlock(pinBlockRequest.getAccountNumber(), pinBlockRequest.getHPin());
			log.info("Generated pin block for given data is :" + pinBlock);
			response.setPinBlock(pinBlock);
			response.setHSMResponseCode("00");
		}
		catch(Exception e)
		{
			log.error("Error talking to hsm",e);
			response.setHSMResponseCode("06");
		}
		return response;
	}
	

	private CMHSMEcryptComponentsResponse processMessageInternal(CMHSMEcryptComponentsRequest encryptComponentRequest)
	{ 
		CMHSMEcryptComponentsResponse response = new CMHSMEcryptComponentsResponse();
		try
		{
			log.info("Generate Encrypted Key request = " + encryptComponentRequest.DumpFields());
			String keyUnderLmk = commandImplementor.generateKeyUnderLMK();
			log.info("Generated keyUnderLmk for given request is :" + keyUnderLmk);
			response.setKey1(keyUnderLmk);
			response.setHSMResponseCode("00");
		}
		catch(Exception e)
		{
			log.error("Error talking to hsm",e);
			response.setHSMResponseCode("06");
		}
		return response;
	}
}

