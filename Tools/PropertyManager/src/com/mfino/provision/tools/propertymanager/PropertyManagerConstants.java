package com.mfino.provision.tools.propertymanager;

public class PropertyManagerConstants
{

	public static final String DES = "DES";
	public static final String DESEDE_ECB_NOPADDING = "DESede/ECB/NoPadding";
	public static final String DESEDE = "DESede";
	public static final String DES_CBC_NOPADDING = "DES/CBC/NoPadding";
	public static String folderLocationInXml = "folderLocation";
	public static String SystemNameInXml = "SystemName";
	public static String IPInXml = "IP";
	public static String searchKeyForHomeFolders = "*";
	public static String searchKeyForSalt = "@saltused";
	public static String searchKeyForNoSalt = "@nosaltused";
	public static String stringAddValueFront = "ENC(";
	public static String stringAddValueBack = ")";
	public static String searchStringRemove = "=" + stringAddValueFront;
	public static String nameOfFileWithFolderDetails = "checkList.txt";
	public static String configFileLocation = "configFile.xml";
	public final static String logLocation = "PropertyManager.log";
	public final static String HELP_DETAILS = "java -jar encryptionToolName [-help] [-debug] [-log <log file name>] \n " +

	"About ENCRYPTION TOOL\n" + "This tool helps in changing the encrypted data present in all the files mentioned in checklist.txt.\n"
			+ "User has to provide the file locations and ENCRYPTED_KEY (both old and new) externally if needed.\n" + "About CHECKLIST FILE\n"
			+ "This tool needs a checkList file as an input to locate all the property files. By default\n"
			+ "Checklist.txt is packaged in a jar file given to the user. Checklist file contains path to the\n"
			+ "property files from the Home folder and name of the Home is given with a * in the text file\n"
			+ "(example: *testFolder_home). User has to provide the Path to home folder when asked for.\n" +

			"------------------------------------------------------------------------------------------------------\n"
			+ "-help	   Displays Instructins for using the tool\n" + "-debug   Puts finest level of data into the log File\n"
			+ "-log <log file name>	This flag enables the user to change the location of LOG file.\n"
			+ "			   File location should be given along with the flag separated by space.\n"
			+ "			   All 'INFO' level data is stored in log file and  as mentioned above, it\n"
			+ "			   can be  changed to Level 'ALL' using -debug flag.\n"
			+ "		  	   By default logs are stored at working directory as PropertyManager.log\n" + "-deploytoproduction  "
			+ "-configFile <config file location>\n"
			+ "-deployverification  Using this mode user can test whether the deployment porperties are properly \n"
			+ "             defined in the proeprty files. This mode needs a checlist file."
			+ "-verifydetails <checkList location> If location of checklist file deployment veryfication \n"
			+ "            is not same as working directory this flag can be used followed by file location of the file.\n"
			+ "-configFile <file location> By default configFile is searched in the working directory and \n"
			+ "            if user wants to give a different location he can do that by using this \n"
			+ "            flag followed by file location.\n"
			+ "-getEncryptedKey  When this flag is called the tool runs in completely different mode.\n"
			+ "            Using this mode the user can decrypt the encrypted KWP using 3des algorithm\n"
			+ "            and ZMK components and get it encrypted with PBEWITHMD5ANDTRIPLEDES and \n"
			+ "            user defined Encryption key. \n";

}
