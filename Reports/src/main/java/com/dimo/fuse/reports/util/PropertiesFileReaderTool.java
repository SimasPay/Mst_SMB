package com.dimo.fuse.reports.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author Amar
 *
 */
public class PropertiesFileReaderTool {

	private static Logger log = LoggerFactory.getLogger(PropertiesFileReaderTool.class);	
	private static String _propertyDir = ".mfino";
	
	public static Properties readProperties(String propertyFileName){
		
		File configFile;
		Properties configurationProperties = new Properties();
		
		try {
			boolean foundFile = false;
			int codeSourceFilePrefixlength = 6;

			if (!foundFile) {
				Context ctx = new InitialContext();
				String warType =  new String();
				log.info("searching wartype:");
				try{
					warType = (String)ctx.lookup("java:comp/env/" + propertyFileName);
					log.info("wartype="+warType);
				}catch(Exception e){
					log.warn("jndi env not found.getting path for " + propertyFileName +" from the mfino_conf folder for tomcat" );
				}
				
				if(warType!=null && !(warType.equals(""))){
					log.info("Getting path for " + propertyFileName + " from jndi env.");
					//get path from the jndi environment variable set in tomcat context.xml
					String[] path = ((String) ctx.lookup( "java:comp/env/mfino."+warType+"." + propertyFileName )).split("file:");
					log.info("path" + path[1]);
					configFile = new File(path[1]);
				}else{
					String path=PropertiesFileReaderTool.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(codeSourceFilePrefixlength);
					log.info("path" + path);
					//same level as the full jar location 
					File dirFile = new File(path);
					// same level as deploy folder of servicemix
					log.info("directory parent="+dirFile.getParent());
					//entering the mfino_conf folder
					String confDir = dirFile.getParentFile().getParent()+"/mfino_conf";
					configFile = new File(confDir, propertyFileName);
					log.info("Try getting property file " + configFile);

				}
				log.info("Config file exist="+configFile.exists()+"Config null check="+(configFile==null));
				if (configFile != null && configFile.exists()) {
					log.info("config file found and not null");
					FileInputStream fis = new FileInputStream(configFile);
					configurationProperties.load(fis);
					fis.close();
					foundFile = true;
				}
			}

			if (!foundFile) {
				String userHome = System.getProperty("user.home");
				configFile = new File(new File(userHome, _propertyDir), propertyFileName);

				log.info("Try getting property file " + configFile);
				if (configFile != null && configFile.exists()) {
					FileInputStream fis = new FileInputStream(configFile);
					configurationProperties.load(fis);
					fis.close();
					foundFile = true;
				}
			}

			if (!foundFile) {
				log.error("Did not find configuration file " + propertyFileName);
			} else {
				log.info(configurationProperties.toString());
			}

		} catch (Exception ex) {
			log.error("Failed to load configuration file " + propertyFileName + ".\n", ex);
		}
		
		return configurationProperties;
	}
}
