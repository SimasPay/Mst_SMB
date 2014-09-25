package com.mfino.bsim.iso8583.nm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.bsim.iso8583.nm.exceptions.InvalidWorkingKeyException;
import com.mfino.bsim.iso8583.nm.exceptions.KcvValidationFailedException;
import com.mfino.handlers.hsm.HSMHandler;
import com.mfino.util.ConfigurationUtil;

public class KeyExchangeHandler {

	private Logger	log	= LoggerFactory.getLogger(KeyExchangeHandler.class);

	private static KeyExchangeHandler keyExchangeHandler;
//	private HibernateTransactionManager htm;
//	private SessionFactory sessionFactory;
//	public HibernateTransactionManager getHtm() {
//		return htm;
//	}
//
//	public void setHtm(HibernateTransactionManager htm) {
//		this.htm = htm;
//	}

//	public static KeyExchangeHandler createInstance(){
//		if(keyExchangeHandler==null){
//			keyExchangeHandler = new KeyExchangeHandler();
//		}
//		
//		return keyExchangeHandler;
//	}

	public static KeyExchangeHandler getInstance(){
	if(keyExchangeHandler==null){
		keyExchangeHandler = new KeyExchangeHandler();
//		throw new RuntimeException("Instance is not already created");
	}
	return keyExchangeHandler;
	}

	public void handle(ISOMsg msg) throws KcvValidationFailedException, InvalidWorkingKeyException, Exception {

		try {
//			sessionFactory = htm.getSessionFactory();
//			Session session = SessionFactoryUtils.getSession(sessionFactory, true);
//			TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
			String key=msg.getValue(48).toString();
			if (key.length() < 48) {
				log.error("working key length is <48");
				throw new InvalidWorkingKeyException("Invalid key length.Key field length is not 48.");
			}
			String encryptedKey = key.substring(0,32);
			log.info("received encrypted working key in de-48 :" + encryptedKey);
			String sixteenDigitReceivedKCV = key.substring(32, 48);
			log.info("received KCV in de-48 :" + sixteenDigitReceivedKCV);
			String zpkUnderLmk = "";
			String calculateKCV="";
			//this should go to hsm if usehsm is true
			if(ConfigurationUtil.getuseHSM()) {
				HSMHandler handler  = new HSMHandler();
				String encryptComponentsResponse = handler.generateEncryptedComponents();
				String response = handler.handleKeyExchangeRequest(encryptedKey); 
				//As HSM Simulator not supporting double length keys added a parameter to differentiate between real hsm and hsm simulator.
				
				if(ConfigurationUtil.getUseRealHSM()){
					if(StringUtils.isNotBlank(response)){
						if(response.startsWith("U") || response.startsWith("X") || response.startsWith("T") || response.startsWith("Y")){
							zpkUnderLmk=response.substring(0, 33);
							calculateKCV = response.substring(33, 49);
						}else{
							zpkUnderLmk = response.substring(0,32);
							calculateKCV = response.substring(32, 48);
						}
					}
				}else{
					zpkUnderLmk = response.substring(0,16);
					calculateKCV = response.substring(16, 32);
				}
				
				//kcv validation in hsm
				if(sixteenDigitReceivedKCV.equalsIgnoreCase(calculateKCV)){
					log.info("kcv validation from hsm success");
				}else{
					log.warn("kcv validation from hsm failed");
					throw new KcvValidationFailedException("kcv validation from hsm failed");
				}
				
				File file = new File("mfino_conf"+File.separator+"zpkunderlmk.txt");
				if (!file.exists()) {
					file.createNewFile();
				}
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(zpkUnderLmk);
				log.info("written zpk under lmk key: "+ zpkUnderLmk + " to file : " + file.getAbsolutePath());
				bw.close();
				log.info("KeyExchangeHandler :: handle setting de-39 as success as kcv validation from hsm is successfull");
				msg.set(39,"00");
			}
		
		}
		catch (KcvValidationFailedException ex) {
			log.error("kcv validation failed", ex);
			throw ex;
		}
		catch (Exception ex) {
			log.error("Exception while validating the kcv", ex);
			throw ex;
		}finally{
//			SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
//			SessionFactoryUtils.closeSession(sessionHolder.getSession());
		}
	}
}
