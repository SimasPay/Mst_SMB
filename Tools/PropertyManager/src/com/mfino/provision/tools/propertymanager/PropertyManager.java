package com.mfino.provision.tools.propertymanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Shashank
 * 
 */
public class PropertyManager
{
	final static String WELCOME_NOTE = "Starting mFino Key Manager tool ..........\n ";
	final static URL pathOfCheckList = ClassLoader.getSystemResource("checkList.txt");
	@SuppressWarnings("unused")
	private static BufferedWriter outfile;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	public static String getDateNow()
	{
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		// "ddMMMyyyy@(HH_mm_ss)");
		String dateNow = formatter.format(currentDate.getTime());
		return dateNow;
	}

	public static void main(String[] args) throws Exception
	{

		PMObject consoleArgs = new PMObject();
		consoleArgs.setLogFileLocation(PropertyManagerConstants.logLocation);
		consoleArgs.setFstreamChecklist(PropertyManager.class.getClassLoader().getResourceAsStream(
				PropertyManagerConstants.nameOfFileWithFolderDetails));
		consoleArgs.setConfigFileLocation(PropertyManagerConstants.configFileLocation);
		String condition;
		LogHandlerPM FileSearchLogHandler = new LogHandlerPM(consoleArgs.getLogFileLocation(), consoleArgs.isDebug());
		Logger logger = FileSearchLogHandler.logger;

		logger.log(Level.SEVERE, WELCOME_NOTE);

		if (args.length == 0) {
			logger.log(Level.ALL, "Starting in normal mode. Make sure that configfile.xml is present at working directory. ");
		}

		if (args.length >= 1) {
			consoleArgs = TakeInputArgs.getInputs(args, logger, consoleArgs);
		}

		String CoDate = getDateNow();

		if (consoleArgs.isMakeChangesAtProduction() == false && consoleArgs.isDeployVerification() == false) {

			logger.log(Level.SEVERE, "Do you want to change encryption key(yes/no):");
			consoleArgs.setPassword(EncryptedKeys.GetPasswords(logger, in));
			logger.log(Level.SEVERE, "do u want retain old properties[yes/no]");

			try {
				while (true) {
					condition = in.readLine();
					if (condition.equalsIgnoreCase("yes") || condition.equalsIgnoreCase("no")) {
						break;
					} else {
						System.out.println("Invalid Input !!");
					}
				}
				if (condition.equalsIgnoreCase("yes")) {
					consoleArgs.setAllFilePropManualChange(false);
					logger.log(Level.FINE, "all properties are retained to orginal value");
				} else {
					consoleArgs.setAllFilePropManualChange(true);
				}
				// HACK:try-catch are used to avoid null pointer exception
				// generated// when Ctrl-c is pressed by user.
			} catch (NullPointerException ne) {
				System.exit(1);
			}
		} else if (consoleArgs.isDeployVerification() == false) {

			logger.log(Level.SEVERE, "Enter Configuration ID :");
			CoDate = in.readLine();

		}

		logger.log(Level.FINE, "checklist file taken from" + pathOfCheckList);
		logger.log(Level.FINE, "reading file " + pathOfCheckList + " to edit properties file.....");

		TextFileUtil.checklistanalyse(consoleArgs, logger, CoDate);

		logger.log(Level.FINE, "changes saved !!");
		logger.log(Level.INFO, "---------------------------------------------------------------------------------------");
		logger.log(Level.WARNING, "Log location: " + consoleArgs.getLogFileLocation());
		logger.log(Level.SEVERE, "Changes saved Successfully");
		logger.log(Level.SEVERE, "Production ID is: " + CoDate);
		System.exit(1);

	}

}
