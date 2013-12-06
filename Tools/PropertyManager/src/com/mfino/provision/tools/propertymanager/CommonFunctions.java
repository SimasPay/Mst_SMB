package com.mfino.provision.tools.propertymanager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonFunctions
{
	public static ArrayList<String> extract(String propname, String a, String b)
	{
		ArrayList<String> fileArray = new ArrayList<String>();

		Pattern pattern = Pattern.compile("\\" + a + "(.+?)\\" + b);
		Matcher matcher = pattern.matcher(propname);
		boolean found = false;
		while (matcher.find()) {
			fileArray.add(matcher.group(1));
			// System.out.println( matcher.group(1));
			found = true;
		}
		if (!found) {
			System.out.println("No match found");
		}
		return fileArray;
	}

	public static BufferedWriter writefile(String filen, String str, Logger logger)
	{

		try {
			BufferedWriter output = null;
			File file = new File(filen);
			output = new BufferedWriter(new FileWriter(file, true));
			output.write(str);
			output.newLine();
			output.close();
			return output;
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not create file");
			// System.out.println();
		}
		return null;
	}

	public static String[] folderNameSeparate(String givenFolder)
	{
		String search = ".";
		int lastIndexOfFileWithoutType = givenFolder.lastIndexOf(search);
		String fileNameWithoutType = givenFolder.substring(0, lastIndexOfFileWithoutType);
		int lastIndexofFileType = givenFolder.length();
		String fileTypeName = givenFolder.substring(lastIndexOfFileWithoutType, lastIndexofFileType);
		String[] separatedFileNames = { fileNameWithoutType, fileTypeName };
		return separatedFileNames;
	}

	public static String GetFolderName(String FolderName, String tag, String Stamp)
	{
		String[] fileNameSplit = CommonFunctions.folderNameSeparate(FolderName);
		return (fileNameSplit[0] + "_" + tag + "_" + Stamp + fileNameSplit[1]);

	}

	public static HashMap<String, String> commonPropertiesMap(String propertiesFileName, HashMap<String, String> finalMapWithNoCommon, Logger logger)
			throws IOException
	{
		String fileNAME = propertiesFileName;
		File f = new File(fileNAME);
		if (f.exists()) {
			Properties TempPropertyMap = new Properties();
			FileInputStream inLoadFile = new FileInputStream(f);
			TempPropertyMap.load(inLoadFile);
			inLoadFile.close();
			@SuppressWarnings({ "rawtypes", "unchecked" })
			HashMap<String, String> PropertyHashMap = new HashMap<String, String>((Map) TempPropertyMap);

			Map<String, String> tmp = new HashMap<String, String>(PropertyHashMap);
			tmp.keySet().removeAll(finalMapWithNoCommon.keySet());
			finalMapWithNoCommon.putAll(tmp);
		} else {
			logger.log(Level.SEVERE, f + "file doesn't exist");
		}
		return finalMapWithNoCommon;
	}
}
