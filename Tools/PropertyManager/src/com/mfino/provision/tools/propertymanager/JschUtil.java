package com.mfino.provision.tools.propertymanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class JschUtil
{

	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	public static ChannelSftp CreateConnection(LoginDetails IntialLoginDetails, Logger logger)
	{

		JSch jsch = new JSch();
		Session session = null;
		try {

			session = jsch.getSession(IntialLoginDetails.getUSERNAME(), IntialLoginDetails.getIPADDRESS());
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(IntialLoginDetails.getPASSWORD());

			session.connect();
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			logger.log(Level.INFO, "conection to IP" + IntialLoginDetails.getIPADDRESS() + channel.isConnected());

			return sftpChannel;

		} catch (JSchException e) {
			logger.log(Level.SEVERE, "Login failed !! invalid credentials or network failure !!" + e);
		} catch (Exception e) {
			logger.log(Level.INFO, e + "recieved from the code");
			e.printStackTrace();
		}
		return null;

	}

	public static String fileCopyFromSource(LoginDetails IntialDetails, String foldPlusFileNAme, Logger logger) throws SecurityException,
			IOException, JSchException
	{
		logger.log(Level.SEVERE, "Trying to access files in " + IntialDetails.getSYSTEMNAME());
		String[] fileNameSplit = CommonFunctions.folderNameSeparate(foldPlusFileNAme);
		String DATEE = PropertyManager.getDateNow();
		int goal = (int) (1000000 * (Math.random()));
		String tempNewFIle = "temp_" + goal + "_" + DATEE + fileNameSplit[1];
		Channel sftp = CreateConnection(IntialDetails, logger);
		if (sftp == null) {
			logger.log(Level.SEVERE, "Unable to login");
			System.exit(1);
		}
		try {
			((ChannelSftp) sftp).get(foldPlusFileNAme, tempNewFIle);
		} catch (SftpException fe) {
			System.out.println(foldPlusFileNAme + "file not found !!");
			System.exit(0);
		}
		((ChannelSftp) sftp).exit();

		return tempNewFIle;
	}

	public static LoginDetails getLoginDetails(LoginDetails IntialLoginDetials, Logger logger) throws IOException, JSchException
	{

		int LoginAttempts = 3;
		// System.out.println("authentication required !!");
		logger.log(Level.SEVERE, "------------------------------------------");
		logger.log(Level.SEVERE, "authentication required !!");
		for (int i = 0; i < LoginAttempts; i++) {

			// logger.log(Level.SEVERE, "Username:");
			System.out.print("Username:");
			IntialLoginDetials.setUSERNAME(in.readLine());
			IntialLoginDetials.setPASSWORD(CipherUtil.passwordDoubleCheck("Password"));
			Channel sftp = CreateConnection(IntialLoginDetials, logger);
			if (sftp != null) {
				i = 4;
			}

			if (i == LoginAttempts - 1) {
				System.err.println("Sorry  Could not connect !!");
				// System.exit(1);
			}
		}

		logger.log(Level.SEVERE, "------------------------------------------");
		return IntialLoginDetials;
	}

	public static void filePasteAtSource(LoginDetails IntialDetails, String tempNewFIle1, String tempNewFIle2, Logger logger)
			throws SecurityException, IOException
	{
		Channel sftp = CreateConnection(IntialDetails, logger);
		if (sftp == null) {
			logger.log(Level.SEVERE, "Unable to login");
			System.exit(1);
		}
		try {
			((ChannelSftp) sftp).put(tempNewFIle1, tempNewFIle2);

			logger.log(Level.INFO, "settings succesfully saved at " + tempNewFIle2 + " along with orginal file");

		} catch (SftpException fe) {
			System.exit(0);
		}
		((ChannelSftp) sftp).exit();

	}

	public static void fileRename(LoginDetails loginDetails, String tempNewFIle1, String tempNewFIle2, Logger logger) throws SecurityException,
			IOException
	{

		Channel sftp = CreateConnection(loginDetails, logger);
		if (sftp == null) {
			logger.log(Level.SEVERE, "\n Login Failed. tool couldn't complete the action !!");
			logger.log(Level.SEVERE, "Please replace the file" + tempNewFIle1 + "\n" + "to " + tempNewFIle2);
		}
		try {
			((ChannelSftp) sftp).rename(tempNewFIle1, tempNewFIle2);
			logger.log(Level.INFO, tempNewFIle1 + " is renamed to " + tempNewFIle2);
			logger.log(Level.INFO, "settings succesfully saved at " + tempNewFIle2 + " along with orginal file");

		} catch (SftpException fe) {
			logger.log(Level.SEVERE, "\n Login Failed. tool couldn't complete the action !!");
			logger.log(Level.SEVERE, "Please replace the file" + tempNewFIle1 + "\n" + "to " + tempNewFIle2);
			System.exit(0);
		}
		((ChannelSftp) sftp).exit();

	}
	/*
	 * public static void main(String[] args) throws IOException, JSchException
	 * { LoginDetails enterDetails = new LoginDetails();
	 * enterDetails.setIPADDRESS("113.193.226.147");
	 * enterDetails.setSYSTEMNAME("hello"); LogHandlerPM FileSearchLogHandler =
	 * new LogHandlerPM("C:/hello.log", false); Logger logger =
	 * FileSearchLogHandler.logger;
	 * 
	 * JschUtil.getLoginDetails(enterDetails, logger);
	 * System.out.println("hello");
	 * System.out.println(enterDetails.UsernameForSystem);
	 * System.out.println(enterDetails.PasswordForSystem); }
	 *//*
		 * public static void main(String[] args) throws SecurityException,
		 * IOException, JSchException, SftpException { // ArrayList<String>[]
		 * ip_all = new ArrayList<String>({"1919191", // "12345"}); //
		 * FolderReading helomadam = new FolderReading("192.168.1.105", "mfino",
		 * // "mFino260"); //
		 * System.out.println(helomadam.USERNAME);System.out.println
		 * (helomadam.IP_Adress ); // System.out.println(helomadam.PASSWORD); //
		 * String FOld = filepaste(USERNAME, IP_Adress, PASSWORD, //
		 * "/home/mfino/Downloads/shashank/mce.properties", //
		 * "H:/hello/mfino_conf/mce.properties"); // // System.out.print(FOld);
		 * // // Logger logger = PropertyManager_old.logger; LoginDetails
		 * enterDetails = new LoginDetails(); // enterDetails =
		 * JschUtil.getLoginDetails("113.193.226.147", logger);
		 * enterDetails.setIPADDRESS("113.193.226.147");
		 * enterDetails.setSYSTEMNAME("hello");
		 * enterDetails.setPASSWORD("mFino260");
		 * enterDetails.setUSERNAME("mfino");
		 * System.out.println(enterDetails.getUSERNAME());
		 * System.out.println(enterDetails.getPASSWORD()); Channel sftp =
		 * fileall(enterDetails, logger); ((ChannelSftp) sftp).get(
		 * "/home/mfino/Downloads/shashank/testFolder1/mfino_conf/mce.properties"
		 * , "H:/shashank.txt"); ((ChannelSftp) sftp).put("H:/shashank.txt",
		 * "/home/mfino/Downloads/shashank/testFolder1/mfino_conf/mce1.properties"
		 * ); ((ChannelSftp) sftp).rename(
		 * "/home/mfino/Downloads/shashank/testFolder1/mfino_conf/mce1.properties"
		 * ,
		 * "/home/mfino/Downloads/shashank/testFolder1/mfino_conf/shashank2.txt"
		 * ); ((ChannelSftp) sftp).get(
		 * "/home/mfino/Downloads/shashank/testFolder1/mfino_conf/shashank2.txt"
		 * , "H:/shashank2.txt"); // sftpChannel.rename(tempNewFIle1,
		 * tempNewFIle2); ((ChannelSftp) sftp).exit(); }
		 */
}
