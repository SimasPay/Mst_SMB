package com.mfino.transactionapi.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.util.ConfigurationUtil;


/**
 * 
 * @author Amar
 *
 */
public class LanguageTranslator {

	private static Logger log = LoggerFactory.getLogger(LanguageTranslator.class);	
	private static File _configFile;
	private static String _propertyFileName = "languageTranslation.json";
	private static String _propertyDir = ".mfino";
	private static JSONObject languageTranslator = null;
	
	static {
		findAndReadJsonFile();
	}
	
	public static void findAndReadJsonFile(){
		try {
			boolean foundFile = false;
			int codeSourceFilePrefixlength = 6;

			if (!foundFile) {
				Context ctx = new InitialContext();
				String warType =  new String();
				log.info("searching wartype:");
				try{
					warType = (String)ctx.lookup("java:comp/env/languageTranslation.json");
					log.info("wartype="+warType);
				}catch(Exception e){
					log.warn("jndi env not found.getting path for languageTranslation.json from the mfino_conf folder for tomcat" );
				}
				
				if(warType!=null && !(warType.equals(""))){
					log.info("Getting path for languageTranslation.json from jndi env.");
					//get path from the jndi environment variable set in tomcat context.xml
					String[] path = ((String) ctx.lookup( "java:comp/env/mfino."+warType+".languageTranslation.json" )).split("file:");
					log.info("path" + path[1]);
					_configFile = new File(path[1]);
				}else{
					String path=ConfigurationUtil.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(codeSourceFilePrefixlength);
					log.info("path" + path);
					//same level as the full jar location 
					File dirFile = new File(path);
					// same level as deploy folder of servicemix
					log.info("directory parent="+dirFile.getParent());
					//entering the mfino_conf folder
					String confDir = dirFile.getParentFile().getParent()+"/mfino_conf";
					_configFile = new File(confDir, _propertyFileName);
					log.info("Try getting property file " + _configFile);

				}
				log.info("Config file exist="+_configFile.exists()+"Config null check="+(_configFile==null));
				if (_configFile != null && _configFile.exists()) {
					log.info("config file found and not null");
					setLanguageTranslator(readJsonFile(_configFile));
					foundFile = true;
				}
			}

			if (!foundFile) {
				String userHome = System.getProperty("user.home");
				_configFile = new File(new File(userHome, _propertyDir), _propertyFileName);

				log.info("Try getting property file " + _configFile);
				if (_configFile != null && _configFile.exists()) {
					setLanguageTranslator(readJsonFile(_configFile));
					foundFile = true;
				}
			}

			if (!foundFile) {
				log.error("Did not find configuration file");
			} else {
				log.info(_configFile.toString());
			}

		} catch (Exception ex) {
			log.error("Failed to load configuration file.\n", ex);
		}
	}
	
	public static JSONObject readJsonFile(File file) throws IOException, JSONException{
		try{
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new FileReader(file));
			String curLine = null;
			while((curLine=br.readLine()) != null){
				sb.append(curLine);
			}
			br.close();
			return new JSONObject(sb.toString());
		}catch(FileNotFoundException e){
			log.info(file.toString() + "could not be found");
			return null;
		}catch(IOException e){
			log.info(e.getMessage());
			return null;
		}catch(JSONException e){
			log.info(e.getMessage());
			return null;
		}	
	}

	public static JSONObject getLanguageTranslator() {
		return languageTranslator;
	}

	public static void setLanguageTranslator(JSONObject languageTranslator) {
		LanguageTranslator.languageTranslator = languageTranslator;
	}
	
	public static String translate(Integer language, String baseLangText){
		try{

			if(languageTranslator != null){
				JSONObject translator = languageTranslator.getJSONObject(language.toString());
				String translatedText = translator.getString(baseLangText);
				return translatedText;
			}
		}catch(JSONException e){
			log.info(e.getMessage());
			//return baseLangText;
		}
		return baseLangText;
	}
	
//	public static JSONObject getLanguageSpecificTranslator(Integer language){
//		try{
//
//			if(languageTranslator != null){
//				JSONObject translator = languageTranslator.getJSONObject(language.toString());
//				return translator;
//			}
//		}catch(JSONException e){
//			log.info(e.getMessage());
//			//return baseLangText;
//		}
//		return null;
//	}

}
