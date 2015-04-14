package com.mfino.hsm.thales7.command;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.space.Space;
import org.jpos.space.SpaceFactory;
import org.jpos.space.TSpace;

import com.mfino.constants.GeneralConstants;
import com.mfino.hsm.thales7.core.ThalesCore;
import com.mfino.hsm.thales7.core.ThalesMsg;
import com.mfino.hsm.thales7.util.HThalesKeyScheme;
import com.mfino.hsm.thales7.util.ThalesConstants;
/**
 * Thales command implementor
 * @author POCHADRI
 *
 *TODO: add the validation logic for each message that for a given request did the expected response come
 * for example for DE DF response should come else it is a failure
 * 
 * IF EOF is set then it means the expected content was not received hence error
 */
public class ThalesCommandImplementor
{
	public Log log = LogFactory.getLog(this.getClass());
	String basePath;
	String pinBlockFormat;
	//PIN Verification key
	String pvk;
	//zone private key
	String zpk;
	// double length zpk
	String zmk;
	String keySchemeTag;
	// encrypted individual components
	String component1;
	String component2;
	String component3;

	public String getKeySchemeTag() {
		return keySchemeTag;
	}

	public void setKeySchemeTag(String keySchemeTag) {
		this.keySchemeTag = keySchemeTag;
	}

	boolean isSetupSuccess;
	public String getZmk() {
		return zmk;
	}

	public void setZmk(String zmk) {
		this.zmk = zmk;
	}
	
	public String getComponent1() {
		return component1;
	}

	public void setComponent1(String component1) {
		this.component1 = component1;
	}

	public String getComponent2() {
		return component2;
	}

	public void setComponent2(String component2) {
		this.component2 = component2;
	}

	public String getComponent3() {
		return component3;
	}

	public void setComponent3(String component3) {
		this.component3 = component3;
	}

	int timeout;
	boolean isPinEncryptionEnabled;
	public boolean isPinEncryptionEnabled() {
		return isPinEncryptionEnabled;
	}

	public void setPinEncryptionEnabled(boolean isPinEncryptionEnabled) {
		this.isPinEncryptionEnabled = isPinEncryptionEnabled;
	}

	/**
	 * these values need to overwritten by the appropriate configuration by 
	 * creating the bean of ThalesCommandImplementor
	 */
	String minPinLength = "04";
	String maxpinlength = "12";
	String pinValidationData = "4458329372N3";
	String decimalizationTable = "1234567890123456";
	String keyType="000";
	String keySchemeLMK="U";
	
	Space sp = SpaceFactory.getSpace();

	/**
	 * In queue and out queue should be same as the values that are used in
	 * config file used to connect to thales
	 */
	String inQueueName;
	String outQueueName;
	
	public String getPinValidationData() {
		return pinValidationData;
	}

	public void setPinValidationData(String pinValidationData) {
		this.pinValidationData = pinValidationData;
	}

	public String getDecimalizationTable() {
		return decimalizationTable;
	}

	public void setDecimalizationTable(String decimalizatioTable) {
		this.decimalizationTable = decimalizatioTable;
	}
	
	public String getInQueueName() {
		return inQueueName;
	}

	public void setInQueueName(String inQueueName) {
		this.inQueueName = inQueueName;
	}

	public String getOutQueueName() {
		return outQueueName;
	}

	public void setOutQueueName(String outQueueName) {
		this.outQueueName = outQueueName;
	}
	
	public String getMinPinLength() {
		return minPinLength;
	}

	public void setMinPinLength(String minPinLength) {
		this.minPinLength = minPinLength;
	}
	
	public String getPvk() {
		return pvk;
	}

	public void setPvk(String pvk) {
		this.pvk = pvk;
	}

	public String getZpk() {
		return zpk;
	}

	public void setZpk(String zpk) {
		this.zpk = zpk;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	public String getPinBlockFormat() {
		return pinBlockFormat;
	}

	public void setPinBlockFormat(String pinBlockFormat) {
		this.pinBlockFormat = pinBlockFormat;
	}
	
	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	
	public String getKeyType() {
		return keyType;
	}

	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}

	public String getKeySchemeLMK() {
		return keySchemeLMK;
	}

	public void setKeySchemeLMK(String keySchemeLMK) {
		this.keySchemeLMK = keySchemeLMK;
	}
	
	/**
	 * This method would use the space and communicate with HSM
	 * @param req
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private ThalesMsg sendRequest(ThalesMsg req)
	{
		TSpace ts = new TSpace();
		
		ts.out ("Request", req);
		sp.out(inQueueName, ts);
		ThalesMsg resp = (ThalesMsg) ts.in ("Response", timeout);
		
		ts = null;
		
		return resp;
	}
	
	/**
	 * Create the <code>HThalesMsg</code> for the given command
	 * @param command Thales command to be used, it is generally a two latter string
	 * @return
	 * @throws IOException
	 */
	public ThalesMsg createRequest(String command) throws IOException 
	{

       ThalesMsg req = new ThalesMsg("file:"+basePath);
      //Warning, this contain a rough hack
      //Somehow JPOS HThalesMsg will generate a stackoverflow if your schema is not present
      //Can't let that happening to a production system.

        if (command != null)
        {     
       	 req.set("command", command);
            File f = null;
            f = new File(new URL(req.getBasePath()+command+".xml").getFile());
            

            if(!f.exists())
            {
           	 	log.error("Schema File not found " +f.getAbsolutePath());
                throw new IOException("Schema File not found "+f.getAbsolutePath());
            }

        }      
        return req;
    }
	
	/**
	 * This method would convert ZPK under ZMK to ZPK under LMK
	 * @return
	 */
	public void setup()
	{
		try 
		{
			ThalesMsg keyExhangeResponse = sendRequest(keyExchange(zmk,zpk));
				
			if(keyExhangeResponse==null || !"FB".equalsIgnoreCase(keyExhangeResponse.get("response")) || "true".equalsIgnoreCase(keyExhangeResponse.get("EOF")))
			{
				// we did not get the right response
				isSetupSuccess = false;
				log.error("did not get the proper response for key exchange response, check integration log, no further transactions are possible");
			}
			else
			{
				// got proper response
				String error = keyExhangeResponse.get("error");
				if("00".equalsIgnoreCase(error))
				{
					// wow got everything perfect
					zpk = keyExhangeResponse.get("zpkunderlmk");
					isSetupSuccess = true;
					log.info("hsm key exchange is a success");
				}
				else
				{
					isSetupSuccess = false;
					log.error("hsm exchange failed, we got a negative response, no further transactions are possible");
				}
			}
		} 
		catch (IOException e) 
		{
			/**
			 * nothing is done when there is an exception later the status set here is used to reject the transactions
			 */
			isSetupSuccess = false;
			log.error("Exception during HSM Key exchange",e);
		}
	}
	
	private ThalesMsg keyExchange(String zmk, String zpk) throws IOException 
	{
		ThalesMsg req = createRequest("FA");
    	req.set("zmkunderlmk","X"+zmk);
    	req.set("zpkunderzmk",zpk);
    	return req;
	}
	
	private ThalesMsg encryptPIN(String pin, String accountNumber) throws IOException
    {
		log.info("ThalesCommandImplementor :: encryptPIN BEGIN");
    	ThalesMsg req = ThalesCore.createRequest(ThalesConstants.ENCRYPT_COMMAND, basePath);
    	req.set("pin",pin+"FF");
    	req.set("account-number", accountNumber);
    	
    	if(isPinEncryptionEnabled())
    	{
    		log.info("pin encryption is enabled so sending to hsm");
    		ThalesMsg res = sendRequest(req);
    	    return res;
    	}
    	else
    	{
    		/**
    		 * BA command to HSM is disabled, hence constructing a dummy response
      		 * encrypted pin is just a 0 prepended to pin
    		 * we can have any other complex algorithm as well
    		 */
    		log.info("pin encryption is disabled so not sending to hsm");
    		ThalesMsg res = new ThalesMsg(basePath);
    		res.set("encrypted-pin", "0"+pin);
    		res.set("response", "BB");
    		res.set("error", "00");
    		return res;
    	}
    	
    	
    }
	
	
	public boolean validatePINinHSM(String accountNumber, String hPin, String offset) throws Exception 
	{
		// encrypt hpin
		log.info("ThalesCommandImplementor :: validatePINinHSM BEGIN");
		log.info("ThalesCommandImplementor :: sending BA command with pin : "+hPin+" accountNumber : "+accountNumber);
		ThalesMsg encryptPinResponse = encryptPIN(hPin, accountNumber);
		if(encryptPinResponse== null )
		{
			throw new Exception("No response for pin encryption request for AccountNumber :" + accountNumber);
		}
		String pinunderlmk = encryptPinResponse.get("encrypted-pin");
		if(StringUtils.isBlank(pinunderlmk)){
			throw new Exception("pinunderlmk null for AccountNumber :" + accountNumber);
		}
		// create pin block
		log.info("ThalesCommandImplementor :: sending JG command with pinunderlmk : "+pinunderlmk+" accountNumber : "+accountNumber);
		ThalesMsg pinBlockResponse = sendRequest(createPINBlock(zpk,pinunderlmk,accountNumber));
		if(pinBlockResponse == null )
			throw new Exception("Could not generate PIN Block for AccountNumber :"+accountNumber);
		
		// validate pin
		String pinblock = pinBlockResponse.get("pinblock");
		log.info("ThalesCommandImplementor :: got pinblock : "+pinblock +" in JH response");
		log.info("ThalesCommandImplementor :: sending EA command with offset : "+offset+" accountNumber : "+accountNumber);
		if(StringUtils.isBlank(pinblock)){
			throw new Exception("pinblock null for AccountNumber :" + accountNumber);
		}
		ThalesMsg validatePINResponse = sendRequest(validatePIN(pvk,zpk,offset,pinblock,accountNumber));
		if(validatePINResponse == null )
			throw new Exception("Could not validate PIN");
		String errorcode = validatePINResponse.get("error");
		
		// validation true or failure are the nonly valid messages remaining all are data issues
		if(GeneralConstants.LOGIN_RESPONSE_SUCCESS.equals(errorcode))
			return true;
		if(GeneralConstants.LOGIN_RESPONSE_FAILED.equals(errorcode))
			return false;
		
		throw new Exception("Error in validating PIN errorcode is: "+errorcode);
	}
		
	
	
	 private ThalesMsg validatePIN(String pvk, String zpk, String offset, String pinblock, String accountNumber) throws IOException 
	 {		
		log.info("ThalesCommandImplementor :: validatePIN BEGIN");
		ThalesMsg req = createRequest(ThalesConstants.PIN_VALIDATION_COMMAND);
    	req.set("zpkunderlmk",zpk);
    	req.set("pvkunderlmk",pvk);
    	req.set("maxpinlength",maxpinlength);
    	req.set("pinblock",pinblock);
    	req.set("pinblockformat",pinBlockFormat );
    	req.set("minpinlength",minPinLength);
    	req.set("accountnumber",accountNumber);
    	req.set("dt",decimalizationTable);
    	req.set("pvd",pinValidationData);
    	req.set("offset", offset);
    	log.info("ThalesCommandImplementor :: validatePIN END");
    	return req;
	}
	 
	private  ThalesMsg createPINBlock(String zpk, String encryptedPIN,String accountNumber) throws IOException 
    {
		log.info("ThalesCommandImplementor :: createPINBlock BEGIN");
    	ThalesMsg req = createRequest(ThalesConstants.CREATE_PIN_BLOCK_COMMAND);
    	req.set("zpkunderlmk",zpk);
    	req.set("pinblockformat",pinBlockFormat);
    	req.set("accountnumber",accountNumber);
    	req.set("epin",encryptedPIN);
    	log.info("ThalesCommandImplementor :: createPINBlock END");
    	
    	return req;
	}
	private  ThalesMsg createPINBlockForReactivation(String zpk, String encryptedPIN,String accountNumber) throws IOException 
    {
		log.info("ThalesCommandImplementor :: createPINBlockForReactivation BEGIN");
    	ThalesMsg req = createRequest(ThalesConstants.CREATE_PIN_BLOCK_COMMAND_REACTIVATION);
    	req.set("zpkunderlmkreact",zpk);
    	req.set("pinblockformat",pinBlockFormat);
    	req.set("accountnumber",accountNumber);
    	req.set("epin",encryptedPIN);
    	log.info("ThalesCommandImplementor :: createPINBlockForReactivation END");
    	return req;
	}
	    
    private ThalesMsg generateOffset(String pvk,String pinunderlmk,String accountNumber) throws IOException
    {
    	log.info("ThalesCommandImplementor :: generateOffset BEGIN");
    	ThalesMsg req = createRequest(ThalesConstants.GENERATE_OFFSET_COMMAND);
    	req.set("pvk", pvk);
    	//req.set("pin", "0123");
    	req.set("pin", pinunderlmk);
    	req.set("check-length",minPinLength);
    	req.set("account-number", accountNumber);
    	req.set("dt",decimalizationTable);
    	req.set("pvd",pinValidationData);
    	log.info("ThalesCommandImplementor :: generateOffset END");
    	return req;
    }

    /**
     * Create a offset talking to HSM
     * @param accountNumber Account Number
     * @param hPin hashed pin
     * @return
     * @throws IOException
     */
	public String createOffset(String accountNumber, String hPin) throws IOException 
	{		
		log.info("ThalesCommandImplementor :: createOffset BEGIN");
		ThalesMsg encryptPinResponse = encryptPIN(hPin, accountNumber);
		if(encryptPinResponse==null ||"true".equalsIgnoreCase(encryptPinResponse.get("EOF")))
		{
			throw new IOException("Invalid response from HSM for encrypt pin request for AccountNumber : "+accountNumber);
		}
		String pinunderlmk = encryptPinResponse.get("encrypted-pin");
		if(StringUtils.isBlank(pinunderlmk)){
			throw new IOException("pinunderlmk null for AccountNumber :" + accountNumber);
		}
		ThalesMsg offsetResponse = sendRequest(generateOffset(pvk,pinunderlmk,accountNumber));
		if(offsetResponse==null || "true".equalsIgnoreCase(encryptPinResponse.get("EOF")))
			throw new IOException("Invalid response from HSM for generate offset request");
		String offset = offsetResponse.get("offset");
		log.info("ThalesCommandImplementor :: createOffset END");
		return offset;
	
	}
	
	//FA command
	public String commandFA(String zpkUnderZMK) throws IOException {
		log.info("ThalesCommandImplementor :: commandFA BEGIN");
		ThalesMsg req = ThalesCore.createRequest(ThalesConstants.ZMK_TO_LMK_ENCRYPTION_COMMAND, basePath);
		BufferedReader br = null;
		String zmkunderlmk="";
		try {
			br = new BufferedReader(new FileReader("mfino_conf"+File.separator+"zmkunderlmk"+component1+component2+component3+".txt"));
			String currentLine;
			while ((currentLine = br.readLine()) != null) {
				zmkunderlmk=currentLine;
			}
			if(StringUtils.isNotBlank(zmkunderlmk)){
				if(zmkunderlmk.startsWith("U") || zmkunderlmk.startsWith("X") || zmkunderlmk.startsWith("T") || zmkunderlmk.startsWith("Y"))
				{
					zmkunderlmk = zmkunderlmk.substring(0,33);
				}
				else{
					zmkunderlmk = zmkunderlmk.substring(0,32);
				}
				
			}
		} catch (FileNotFoundException e) {
			log.error("Error while reading zpkunderlmk.txt file." + e.getMessage(),e );
		} finally {
			br.close();
		}
		req.set("zmkunderlmk",zmkunderlmk);
		if(StringUtils.isNotBlank(keySchemeTag)){
			zpkUnderZMK=keySchemeTag+zpkUnderZMK;
		}
    	req.set("zpkunderzmk",zpkUnderZMK);
    	log.info("ThalesCommandImplementor :: zmkToLMKEncryption sending request with zpkunderzmk= "+zpkUnderZMK+" zmkunderlmk= "+zmkunderlmk);
    	ThalesMsg res = sendRequest(req);
    	if(res==null || "true".equalsIgnoreCase(res.get("EOF"))){
			throw new IOException("Invalid response from HSM for zmkToLMKEncryption");
		}
		String zpkunderlmk = res.get("zpkunderlmk");
		String kcv = res.get("keycheckvalue");
		
		log.info("ThalesCommandImplementor :: zmkToLMKEncryption response zpkunderlmk= "+zpkunderlmk);
		log.info("ThalesCommandImplementor :: commandFA END");
		return zpkunderlmk+kcv;
    	
	}
	//NG command
	public String decryptPin(String accountNumber,String pinunderlmk) throws IOException {
		log.info("ThalesCommandImplementor :: decryptPin BEGIN");
		ThalesMsg req = ThalesCore.createRequest(ThalesConstants.DECRYPT_COMMAND, basePath);
		log.info("sending decryptPin request with zpkunderlmk "+pinunderlmk);
    	req.set("accountnumber", accountNumber);
    	req.set("pin", pinunderlmk);
    	log.info("ThalesCommandImplementor :: decryptPin sending request ");
    	ThalesMsg res = sendRequest(req);
    	if(res==null || "true".equalsIgnoreCase(res.get("EOF"))){
			throw new IOException("Invalid response from HSM for NG Command during decryptPin()");
		}
		String clearpin = res.get("clear-pin");
		log.info("ThalesCommandImplementor :: decryptPin END");
		return clearpin;
    	
	}
	
	//JE command
	public String zpkToLMKTranslate(String accountNumber,String pinBlock) throws IOException {
		log.info("ThalesCommandImplementor :: zpkToLMKTranslate BEGIN");
		String zpkunderlmk = "";
		BufferedReader br = new BufferedReader(new FileReader("mfino_conf"+File.separator+"zpkunderlmk.txt"));
		String currentLine;
		while ((currentLine = br.readLine()) != null) {
				zpkunderlmk=currentLine;
		}
		ThalesMsg req = ThalesCore.createRequest(ThalesConstants.ZPK_TO_LMK_TRANSLATION_COMMAND, basePath);
		log.info("sending zpkToLMKTranslate request with zpkunderlmk "+zpkunderlmk);
		req.set("zpkunderlmk",zpkunderlmk);
    	req.set("accountnumber", accountNumber);
    	req.set("pinblock", pinBlock);
    	req.set("pinblockformat", pinBlockFormat);
    	log.info("ThalesCommandImplementor :: zpkToLMKTranslate sending request ");
    	ThalesMsg res = sendRequest(req);
    	if(res==null || "true".equalsIgnoreCase(res.get("EOF"))){
			throw new IOException("Invalid response from HSM for zpkToLMKTranslate for accountNumber :"+accountNumber);
		}
		String pinunderlmk = res.get("encrypted-pin");
		if(StringUtils.isBlank(pinunderlmk)){
			throw new IOException("pinunderlmk null for zpkToLMKTranslate accountNumber:"+accountNumber);
		}
		log.info("ThalesCommandImplementor :: zpkToLMKTranslate reponse pinunderlmk"+pinunderlmk);
		return pinunderlmk;
    	
	}
	
	public String createOffsetForATMRegistration(String sourceMDN,String accountNumber,String pinBlock) throws IOException{
		log.info("ThalesCommandImplementor :: createOffsetForATMRegistration BEGIN");
		log.info("Send ZpktoLMK Translate to hsm accountNumber ="+accountNumber);
		//call JE
		String pinunderlmk = zpkToLMKTranslate(accountNumber, pinBlock);
		//call NG
		String clearPin = decryptPin(accountNumber, pinunderlmk);
		log.info("ThalesCommandImplementor :: createOffsetForATMRegistration END");
		return clearPin;
	}
	
	/**
	 * Generates the Pin block under ZPK for the given clear Pin using the given account number
	 * @param accountNumber
	 * @param hPin
	 * @return
	 * @throws IOException
	 */
	public String generatePinBlock(String accountNumber, String hPin) throws IOException {			
		// convert the clear pin to pin under LMK
		log.info("ThalesCommandImplementor :: generatePinBlock BEGIN");
		log.info("Sending request to HSM for BA command....");
		ThalesMsg encryptPinResponse = encryptPIN(hPin, accountNumber);
		if(encryptPinResponse==null ||"true".equalsIgnoreCase(encryptPinResponse.get("EOF")))
		{
			throw new IOException("Invalid response from HSM for encrypt pin request (BA Command)");
		}
		String pinunderlmk = encryptPinResponse.get("encrypted-pin");
		if(StringUtils.isBlank(pinunderlmk)){
			throw new IOException("pinunderlmk null for AccountNumber :" + accountNumber);
		}
		// convert the pin under LMK to pin under ZPK
		String zpkunderlmk = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("mfino_conf"+File.separator+"zpkunderlmk.txt"));
			String currentLine;
			while ((currentLine = br.readLine()) != null) {
					zpkunderlmk=currentLine;
			}
		} catch (FileNotFoundException e) {
			log.error("Error while reading zpkunderlmk.txt file." + e.getMessage(),e );
		} finally {
			br.close();
		}
		log.info("Sending request to HSM for JG command ...");
		ThalesMsg pinBlockResponse = sendRequest(createPINBlockForReactivation(zpkunderlmk,pinunderlmk,accountNumber));
		if(pinBlockResponse == null  || "true".equalsIgnoreCase(pinBlockResponse.get("EOF")) ) {
			throw new IOException("Could not generate PIN Block");
		}
		
		String pinblock = pinBlockResponse.get("pinblock");
		log.info("ThalesCommandImplementor :: generatePinBlock END");
		return pinblock;
	}
	
	public String generateKeyUnderLMK() throws IOException{
		log.info("ThalesCommandImplementor :: generateKeyUnderLMK BEGIN");
		ThalesMsg req = createRequest(ThalesConstants.GENERATE_KEY_UNDER_LMK);
		req.set("number-of-components","3"); 
		req.set("key-type",keyType);
		req.set("key-scheme-lmk",keySchemeLMK);
    	req.set("component1",component1);
    	req.set("component2",component2);
    	req.set("component3",component3);
    	ThalesMsg res = sendRequest(req);
    	if(res==null || "true".equalsIgnoreCase(res.get("EOF"))){
			throw new IOException("Invalid response from HSM for generateKeyUnderLMK");
		}
		String keyunderlmk = res.get("key-lmk");
		String kcv = res.get("check-value");
		//writing zmkunderlmk file
		File fileZL = new File("mfino_conf"+File.separator+"zmkunderlmk"+component1+component2+component3+".txt");
		if (!fileZL.exists()) {
			fileZL.createNewFile();
		FileWriter fwZL = new FileWriter(fileZL.getAbsoluteFile());
		BufferedWriter bwZL = new BufferedWriter(fwZL);
		bwZL.write(keyunderlmk+kcv);
		log.info("written zmk under lmk key: "+ keyunderlmk + " to file : " + fileZL.getAbsolutePath());
		bwZL.close();
		}
		log.info("ThalesCommandImplementor :: generateKeyUnderLMK response keyunderlmk= "+keyunderlmk+ "kcv "+kcv);
		return keyunderlmk+kcv;
	}

}
