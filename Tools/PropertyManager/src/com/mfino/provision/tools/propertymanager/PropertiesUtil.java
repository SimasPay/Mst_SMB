package com.mfino.provision.tools.propertymanager;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jasypt.exceptions.EncryptionOperationNotPossibleException;

class PropertiesObject
{
	ArrayList<String> allProperties = new ArrayList<String>();
	HashMap<String, String> AllPropertiesMap = new HashMap<String, String>();
	HashMap<String, LoginDetails> SystemCred = new HashMap<String, LoginDetails>();
	String tempFileAtLocal;

	public HashMap<String, LoginDetails> getSystemCred()
	{
		return this.SystemCred;
	}

	public void setSystemCred(HashMap<String, LoginDetails> systemCred)
	{
		this.SystemCred = systemCred;
	}

	public ArrayList<String> getAllProperties()
	{
		return this.allProperties;
	}

	public void setAllProperties(ArrayList<String> allProperties)
	{
		this.allProperties = allProperties;
	}

	public HashMap<String, String> getAllPropertiesMap()
	{
		return this.AllPropertiesMap;
	}

	public void setAllPropertiesMap(HashMap<String, String> allPropertiesMap)
	{
		this.AllPropertiesMap = allPropertiesMap;
	}

	public String getTempFileAtLocal()
	{
		return this.tempFileAtLocal;
	}

	public void setTempFileAtLocal(String tempFileAtLocal)
	{
		this.tempFileAtLocal = tempFileAtLocal;
	}
}

public class PropertiesUtil
{
	static String CLASSPATH = "classpath:";
	static String classpathFolder = "\\WEB-INF\\classes\\";
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	public static HashMap<String, ArrayList<String>> checkforproperties(Logger logger, ObjectFromXml xmlObjectFrmMain,
			HashMap<String, ArrayList<String>> failedFilesMap, PMObject consoleArgs, PropertiesObject propertyobject) throws IOException

	{
		String SearchInFolder = xmlObjectFrmMain.getSearchInFolder();
		String Home = xmlObjectFrmMain.getHome();
		ArrayList<String> fileLocationActual = xmlObjectFrmMain.getFileLocationActual();
		boolean FailCheck = false;
		String str;
		logger.log(Level.INFO, "---------------------------------------------------------------------------");
		String finalFolder = null;
		for (int i = 0; i < xmlObjectFrmMain.getPropertyarray().size(); i++) {
			String propname = xmlObjectFrmMain.getPropertyarray().get(i);
			boolean propertyExists = false;
			logger.log(Level.INFO, "checking for " + propname);
			for (int k = 0; k < fileLocationActual.size(); k++) {
				File file;
				String fileName = fileLocationActual.get(k);
				String split = fileName.split(":", 0)[1];
				if (fileName.startsWith((CLASSPATH))) {
					finalFolder = SearchInFolder + classpathFolder + split;
					file = new File(finalFolder);
				} else {
					finalFolder = Home + split;
					file = new File(finalFolder);
				}

				FileInputStream fstream = new FileInputStream(file);
				DataInputStream inForPropertiesFile = new DataInputStream(fstream);
				BufferedReader brForProeprtiesFile = new BufferedReader(new InputStreamReader(inForPropertiesFile));

				while ((str = brForProeprtiesFile.readLine()) != null) {
					if (str.startsWith(propname) == true) {
						propertyExists = true;
						logger.log(Level.INFO, " PASSED !!");
					}
				}
				inForPropertiesFile.close();
			}

			if (propertyExists == false) {

				ArrayList<String> tempArray = new ArrayList<String>();
				FailCheck = true;
				logger.log(Level.SEVERE, "The property " + propname + " is not defined in any files !!");
				logger.log(Level.SEVERE, "do you want to update it on property file[yes/no]");

				String condition = in.readLine();
				if (condition.equalsIgnoreCase("yes")) {

					if (propertyobject.allProperties.size() == 0) {
						DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
						// System.out.println(dateFormat.format(new Date()));
						String strwe = "";
						String strnew1 = "###############################################################################";
						String strnew2 = "#This line is added by PropertyManager Tool on " + dateFormat.format(new Date());
						String strnew3 = "###############################################################################";

						CommonFunctions.writefile(finalFolder, strwe, logger);
						CommonFunctions.writefile(finalFolder, strnew1, logger);
						CommonFunctions.writefile(finalFolder, strnew2, logger);
						CommonFunctions.writefile(finalFolder, strnew3, logger);
					}
					String strnew = PropertiesUtil.propertytake(propname, "Not Defined", consoleArgs, logger, propertyobject, false);
					CommonFunctions.writefile(finalFolder, strnew, logger);
					FailCheck = false;
					propertyExists = true;

					if (FailCheck == true) {
						logger.log(Level.SEVERE, "FAILED");
						if (failedFilesMap.containsKey(propname) == true) {
							tempArray = failedFilesMap.get(propname);
							tempArray.addAll(xmlObjectFrmMain.getFileLocationActual());
						} else {
							tempArray.addAll(xmlObjectFrmMain.getFileLocationActual());
						}
						failedFilesMap.put(propname, tempArray);
					}
				}
			}
		}
		logger.log(Level.SEVERE, "---------------------------------------------------------------------------");
		// System.out.println("---------------------------------------------------------------------------");
		return failedFilesMap;

	}

	public static String propertytake(String key, String value, PMObject consoleArgs, Logger logger, PropertiesObject propertyobject,
			boolean IsEncrypted) throws IOException
	{
		if (propertyobject.getAllProperties().contains(key) == false) {

			propertyobject.getAllProperties().add(key);
			String pwdNotEncrypted = value;

			if (IsEncrypted == true) {
				String pwdWithEncryption = CommonFunctions.extract(value, "(", ")").get(0);
				try {
					pwdNotEncrypted = CipherUtil.decrypt(consoleArgs.getPassword().getOldPassword(), pwdWithEncryption, consoleArgs.isUseSalt());
				} catch (EncryptionOperationNotPossibleException pr) {
					System.out.println(pr);
					logger.log(Level.SEVERE,
							"Decryption failed !! Either Old Encrytion key is wrong or the Strong Policy files are noot being installed. Please check !!");
					System.exit(0);
				}
			}
			boolean propChangeCondition = false;
			if (consoleArgs.isDeployVerification() == true) {
				consoleArgs.setAllFilePropManualChange(false);
				propChangeCondition = true;
			}

			// if the user want to change each // property manually
			try {
				// HACK:try-catch are used to// avoid// null pointer// exception
				// generated when // Ctrl-c// is pressed by// user.

				if (consoleArgs.isAllFilePropManualChange() == true)

				{
					logger.log(Level.SEVERE, key + "=" + pwdNotEncrypted);
					logger.log(Level.SEVERE, "[do you want to change (yes/no) ]:");
					String condition = in.readLine();
					propChangeCondition = condition.equalsIgnoreCase(("yes"));
				} else {
				}

				if (propChangeCondition == true) {
					logger.log(Level.SEVERE, "Enter Property value:");
					value = in.readLine();
					logger.log(Level.FINE, "Password changed!!");
					if (IsEncrypted == false) {
						logger.log(Level.SEVERE, "Do you want the property to be encrypted  [yes/no]:");
						String condition = in.readLine();
						IsEncrypted = condition.contains("yes");
					}
				} else {

					value = pwdNotEncrypted;
				}

				if (IsEncrypted == true) {

					if (consoleArgs.getPassword() == null) {
						Passwords passwords = new Passwords();
						String oldPassword = CipherUtil.passwordDoubleCheck("Encryption key:");
						passwords.setOldPassword(oldPassword);
						passwords.setNewPassword(oldPassword);
						passwords.setPasswordisset(true);
						consoleArgs.setPassword(passwords);
					}
					String pwdWithEncryption = CipherUtil.encrypt(consoleArgs.getPassword().getNewPassword(), value, consoleArgs.isUseSalt());
					value = PropertyManagerConstants.stringAddValueFront + pwdWithEncryption + PropertyManagerConstants.stringAddValueBack;
				}
				propertyobject.getAllPropertiesMap().put(key, value);
			} catch (NullPointerException ne) {
				System.exit(1);
			}
			logger.log(Level.FINE, key + "=" + pwdNotEncrypted);
		}
		// if property is already changed in// other/ file!! don't// ask again
		// bring it from there and// save it !
		else {
			logger.log(Level.FINE, "Property value of" + key + " is confirmed in other file");
			value = (propertyobject.getAllPropertiesMap().get(key));
			if (IsEncrypted == true) {
				String pwdWithEncryption = CommonFunctions.extract(value, "(", ")").get(0);
				value = CipherUtil.decrypt(consoleArgs.getPassword().getNewPassword(), pwdWithEncryption, consoleArgs.isUseSalt());
			}
			logger.log(Level.INFO, "property of " + key + " is already set '" + value + "' same value is copied");
			logger.log(Level.INFO, key + "=" + value);
		}

		return (key + "=" + value);
	}

}
