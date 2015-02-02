/**
 * 
 */
package com.mfino.service;


/**
 * @author Shashank
 *
 */
public interface MfinoUtilService {
	/**
	 * Validate the given pin/hashed pin 
	 * It can validate either using hash or HSM based on the configuration
	 * property name that need to be set in mfino.properties is mfino.use.hsm, values are true/false
	 * @param pin pin or hashed pin
	 * @param mdn mobile number
	 * @param storedPin digested PIN/ offset (digested pin in case of hash and offset in case of HSM)
	 * @return
	 */
	public String validatePin(String mdn, String pin ,String storedPin, int pinlength);
	public  String modifyPINForStoring(String mdn, String pin) throws Exception;
	/**
	 * As part of request we get PIN in the form of Hash, we cannot use that pin for validation 
	 * in case of HSM, so need to convert it to the pin length that is supported
	 * @param pin
	 * @return
	 */
	public  String convertPinForValidation(String pin, int len);

	String validatePINUsingHash(String mdn, String pin, String digestedPin);
	
	String validatePINUsingHSM(String mdn,String hPin, String offset);



}
