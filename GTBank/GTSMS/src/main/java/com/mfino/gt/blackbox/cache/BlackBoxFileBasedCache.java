package com.mfino.gt.blackbox.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public class BlackBoxFileBasedCache implements BlackBoxLocalCache 
{	
	private Log log = LogFactory.getLog(BlackBoxFileBasedCache.class);
	private String localCacheFileLocation;
	//boolean variable to check the existence of gtAccountNumberfile
	private boolean isLocalCacheAvailable;
	//HashMap to store the data from the properties file
	HashMap<String, String> propertiesMap; 
	public BlackBoxFileBasedCache(String localCacheFileLocation)
	{
		this.localCacheFileLocation = localCacheFileLocation;
		isLocalCacheAvailable = constructLocalAccNumCache();
	}
	//Read data from the localCacheFile into a Properties object and transfer to
		//a HashMap

		public boolean constructLocalAccNumCache() {
			File localCacheFile = new File(localCacheFileLocation);
			if (localCacheFile.exists()) {
				   Properties localCachePropertiesFile = new Properties();
				   FileInputStream propertiesLoadFile;
				try {
					propertiesLoadFile = new FileInputStream(localCacheFile);
						try {
							localCachePropertiesFile.load(propertiesLoadFile);
							
						} catch (IOException e1) {
							log.warn("Error occured reading properties Load File.  stack trace is :" + e1.getStackTrace());
							return false;
						}
						finally
						{
							try
							{
								 propertiesLoadFile.close();
							}
							catch(IOException e)
							{
							  // ignore
							}
						}

				} catch (FileNotFoundException e) {
					log.warn("could not find the local Cache File.The stack trace is :" + e.getStackTrace());
					return false;
				}
				   
				   log.info("got the localCacheFile");

				   propertiesMap = validateAndGenerateMap(localCachePropertiesFile);
				   if(propertiesMap.size()==0){
					   log.warn("local Cache File is empty");
					   return false;
				   }
				   return true;
			}
			log.warn("could not find the localCacheFile");
			return false;
		}
		
		/*
		 * gets the properties from the file ,stores it in a hashMap and validates it to not contain spaces or null as value and returns the validated hashMap 
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		private static HashMap<String, String> validateAndGenerateMap(Properties localCachePropertiesFile)
		{	 
			   //we know that these values are strings hence added the supress warning for this conversion
			HashMap<String, String> toValidatePropertyMap = new HashMap<String, String>((Map)localCachePropertiesFile);
			HashMap<String, String> validatedPropertyMap = new HashMap<String, String>();

			 for(String key:toValidatePropertyMap.keySet())
			   {
				 String value = toValidatePropertyMap.get(key);
				   if(!(value==null|| value.trim().equals("")))
				   {
					   validatedPropertyMap.put(key, value);
				   }
			   }
			return validatedPropertyMap;
		}

		
		/**
		 * gets the acount number from the local cache
		 * Note: local cache is a file which is a configurable property on this bean
		 * @param acNum
		 * @return
		 */
	@Override
	public String getConvertedAccNum(String accountNumber) {
		//if the gtAccountNumberFile exists then get the value for the key
		   if(isLocalCacheAvailable)
		   {  
			   //return the 16 digit number for the 10 digit number entered
				return propertiesMap.get(accountNumber);
		   }
		   return null; 
	}
	
    /**
     * Since reading from the file there is no need of having an 
     * implementation for it
     */
	 @Override
	 public String addAccNum(String accountNumber,
 			String ConvertedAccountNumber) 
   	 {
 	 	 // TODO Auto-generated method stub
		 return null;
	 }
}
