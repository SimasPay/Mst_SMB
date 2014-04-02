package com.dimo.fuse.reports.db;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Amar
 * 
 */
public class DBProperties {

	private static Logger log = LoggerFactory.getLogger(DBProperties.class);
	private static String _dbPropertiesFileName = "/database_config.properties";
	private static Properties dbProperties;
	private static String encryptionKey;
	private static StandardPBEStringEncryptor stringEncryptor;

	static {
		getEncryptionKey();
		readDBProperties();
	}

	private static void getEncryptionKey() {
		encryptionKey = System.getProperty("ENCRYPTION_KEY");
		if (encryptionKey == null) {
			encryptionKey = System.getenv().get("ENCRYPTION_KEY");
		}
	}

	public static void readDBProperties() {
		dbProperties = new Properties();
		try {
			InputStream ins = DBProperties.class.getClassLoader()
					.getResourceAsStream(_dbPropertiesFileName);
			dbProperties.load(ins);
		} catch (FileNotFoundException e) {
			log.error("file " + _dbPropertiesFileName + "not found");
			e.printStackTrace();
		} catch (IOException e) {
			log.error("Failed to load configuration file.\n", e);
			e.printStackTrace();
		}
	}

	public static String getJDBCDriver() {
		return decryptProperty(dbProperties.getProperty(
				"mfino.dbcp.driverClassName", "com.mysql.jdbc.Driver"));
	}

	public static String getDBUrl() {
		return decryptProperty(dbProperties.getProperty("mfino.dbcp.url"));
	}

	public static String getDBUserName() {
		return decryptProperty(dbProperties.getProperty("mfino.dbcp.username"));
	}

	public static String getDBPassword() {
		return decryptProperty(dbProperties.getProperty("mfino.dbcp.password"));
	}

	public static String getDBEncryptionPassword() {
		return decryptProperty(dbProperties
				.getProperty("mfino.dbEncryption.password"));
	}

	public static String getDBEncryptionSalt() {
		return decryptProperty(dbProperties
				.getProperty("mfino.dbEncryption.salt"));
	}

	public static String decryptProperty(String property) {
		if (property.startsWith("ENC(")) {
			property = property.substring(4, property.length() - 1);
			try {
				return getStringEncryptor().decrypt(property);
			} catch (EncryptionOperationNotPossibleException e) {
				log.warn("Invalid property value. Either the string is not encrypted properly or is encrypted with different algorithm and/or key");
			}
		}
		return property;
	}

	private static StandardPBEStringEncryptor getStringEncryptor() {
		if (stringEncryptor == null) {
			stringEncryptor = StringEncryptorDecryptor
					.getDBStringEncryptor(encryptionKey);
		}
		return stringEncryptor;
	}

}
