package com.mfino.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.handlers.hsm.HSMHandler;
import com.mfino.service.MfinoUtilService;
import com.mfino.service.SystemParametersService;
 import com.mfino.util.ConfigurationUtil;
import com.mfino.util.MfinoUtil;

@Service("MfinoUtilServiceImpl")
public class MfinoUtilServiceImpl implements MfinoUtilService{

	private static Logger log = LoggerFactory.getLogger(MfinoUtilServiceImpl.class);	

	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;

	/**
	 * Validate the given pin/hashed pin 
	 * It can validate either using hash or HSM based on the configuration
	 * property name that need to be set in mfino.properties is mfino.use.hsm, values are true/false
	 * @param pin pin or hashed pin
	 * @param mdn mobile number
	 * @param storedPin digested PIN/ offset (digested pin in case of hash and offset in case of HSM)
	 * @return
	 */
	public boolean validatePin(String mdn, String pin ,String storedPin)
	{
		/**
		 * Hopefully this method is always called in a transaction
		 * convert the hashed pin to pin with valid length for all validations
		 */
		if(ConfigurationUtil.getuseHashedPIN())
		{
			log.info("Converting hashed pin for validation");
			pin = convertPinForValidation(pin, systemParametersService.getPinLength());
		}
		//use digested PIN for validation
		if(!ConfigurationUtil.getuseHSM())
		{
			log.debug("validating pin using hash");
			return validatePINUsingHash(mdn, pin, storedPin);
		}
		//use HSM for validation
		else
		{
			log.debug("validating pin using HSM");
			return validatePINUsingHSM(mdn,pin,storedPin);
		}
	}
	public  String modifyPINForStoring(String mdn, String pin) throws Exception
	{
		if(!ConfigurationUtil.getuseHSM()){
			return MfinoUtil.calculateDigestPin(mdn,pin);}
		else
		{
			if(ConfigurationUtil.getuseHashedPIN())
				pin = convertPinForValidation(pin,systemParametersService.getPinLength());
			HSMHandler handler = new HSMHandler();
			return handler.generateOffset(mdn, pin);
		}
	}
	/**
	 * As part of request we get PIN in the form of Hash, we cannot use that pin for validation 
	 * in case of HSM, so need to convert it to the pin length that is supported
	 * @param pin
	 * @return
	 */
	public   String convertPinForValidation(String pin, int len)
	{
		// no need of conversion if HSM is not used or hashed pin is not used.
		if(!ConfigurationUtil.getuseHashedPIN())
			return pin;
		if(pin==null||pin.trim().equals("")) return pin;
		//lets find the last N digits from the given PIN
		final StringBuilder sb = new StringBuilder(len*2);
		int count = 0;
	    for(int i= pin.length()-1; i >= 0; i--)
	    {
	        final char c = pin.charAt(i);
	        if(c > 47 && c < 58){
	            sb.append(c);
	            count++;
	        }
	        if(count==len)
	        	break;
	    }
	    
	    if(count<len)
	    {
	    	int extraLen = len-count;
	    	for(int i=0;i<extraLen;i++)
	    	{
	    		sb.append("1");
	    	}
	    }
	    return sb.toString();
	}

	public boolean validatePINUsingHash(String mdn, String pin, String digestedPin)
	{
		String calcPIN = MfinoUtil.calculateDigestPin(mdn, pin);
		return calcPIN.equalsIgnoreCase(digestedPin);
	}
	
	public boolean validatePINUsingHSM(String mdn,String hPin, String offset)
	{
		HSMHandler h = new HSMHandler();
		return  h.validatePIN(mdn, hPin,offset);
		//return response.equals("00");
	}

}
