package com.mfino.provision.tools.propertymanager;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TextFileUtil
{
	// private static BufferedWriter outfile;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	public static PropertiesObject propertyFilesReading(PMObject consoleArgs, Logger logger, String orginalFileAtLocal,
			PropertiesObject propertyobject) throws IOException
	{

		propertyobject.AllPropertiesMap = (CommonFunctions.commonPropertiesMap(orginalFileAtLocal, propertyobject.getAllPropertiesMap(), logger));
		String DATESTAMP = PropertyManager.getDateNow();
		String tempFileAtLocal = CommonFunctions.GetFolderName(orginalFileAtLocal, "temp", DATESTAMP);
		// System.out.println(tempNewFIle);
		File file = new File(orginalFileAtLocal);
		if (!file.exists()) {
			logger.log(Level.WARNING, orginalFileAtLocal + " does not exist!!");
			System.exit(0);
		}
		String str = null;

		// FileInputStream fstream = new FileInputStream(file);
		DataInputStream inForPropertiesFile = new DataInputStream(new FileInputStream(file));
		BufferedReader brForPropertiesFile = new BufferedReader(new InputStreamReader(inForPropertiesFile));
		boolean isEncrypted = false;
		int i;
		String propertyName = null;
		while ((str = brForPropertiesFile.readLine()) != null) {

			if (str.contains(PropertyManagerConstants.searchStringRemove)) {
				i = str.indexOf(PropertyManagerConstants.searchStringRemove);
				propertyName = str.substring(0, i);
				String EncryptedPropertyValue = (propertyobject.getAllPropertiesMap().get(propertyName));
				isEncrypted = true;
				PropertiesUtil.propertytake(propertyName, EncryptedPropertyValue, consoleArgs, logger, propertyobject, isEncrypted);
				str = propertyName + "=" + propertyobject.getAllPropertiesMap().get(propertyName);
			} else {
				// System.out.println(str);
				String[] Keyvalue = str.split("=");
				if (Keyvalue.length == 2 && str.startsWith("#") == false) {
					isEncrypted = false;
					PropertiesUtil.propertytake(Keyvalue[0], Keyvalue[1], consoleArgs, logger, propertyobject, isEncrypted);
					propertyName = Keyvalue[0];
					str = propertyName + "=" + propertyobject.getAllPropertiesMap().get(propertyName);
				}
			}
			CommonFunctions.writefile(tempFileAtLocal, str, logger);
		}
		inForPropertiesFile.close();
		propertyobject.setTempFileAtLocal(tempFileAtLocal);
		return propertyobject;
	}

	public static void checklistanalyse(PMObject consoleArgs, Logger logger, String CoDate) throws IOException, InvocationTargetException
	{

		String requiredFile;
		DataInputStream inChecklist = new DataInputStream(consoleArgs.getFstreamChecklist());
		BufferedReader brChecklist = new BufferedReader(new InputStreamReader(inChecklist));
		PropertiesObject propertiesObject = new PropertiesObject();
		String moduleName = null;

		HashMap<String, ArrayList<String>> failedFilesMap = new HashMap<String, ArrayList<String>>();
		while ((requiredFile = brChecklist.readLine()) != null) {
			// search for folders
			if (requiredFile.startsWith(PropertyManagerConstants.searchKeyForHomeFolders)) {
				moduleName = requiredFile
						.substring(requiredFile.indexOf(PropertyManagerConstants.searchKeyForHomeFolders) + 1, requiredFile.length());
				if (consoleArgs.isDeployVerification() == true) {
					logger.log(Level.SEVERE, "Enter Location of " + moduleName);
					moduleName = in.readLine() + "\\";
				}
				// try {
				// File file = new File(moduleName);x
				// } catch (NullPointerException npe) {
				// logger.log(Level.SEVERE, "error:" + npe);
				// }
			} else if (requiredFile.equalsIgnoreCase(PropertyManagerConstants.searchKeyForSalt)) {
				consoleArgs.setUseSalt(true);
			}

			else if (requiredFile.equalsIgnoreCase(PropertyManagerConstants.searchKeyForNoSalt)) {
				consoleArgs.setUseSalt(false);
			} else if (requiredFile.startsWith("#")) {
				// ignore the lines which start with #
			} else {
				if (consoleArgs.isDeployVerification() == true) {

					XmlParse.searchXmlFiles(moduleName, requiredFile, failedFilesMap, logger, consoleArgs);
				} else {
					propertiesObject = XmlParse.checkConfigFile(consoleArgs, requiredFile, logger, moduleName, CoDate, propertiesObject);
				}
			}
		}

		if (consoleArgs.isDeployVerification() == true) {
			logger.log(Level.SEVERE, "End of DVCheckList file !! Check PropertyManager.log for more details.");

			Set<String> keys = failedFilesMap.keySet();
			for (Iterator<String> i = keys.iterator(); i.hasNext();) {
				String failedPropName = i.next();
				ArrayList<String> folderLocationWithNoProperties = failedFilesMap.get(failedPropName);
				logger.log(Level.INFO, "Unable to find " + failedPropName + "in any of the files mentioned in xml");
				for (int g = 0; g < folderLocationWithNoProperties.size(); g++) {
					logger.log(Level.INFO, folderLocationWithNoProperties.get(g));
				}
			}
		}

	}

}
