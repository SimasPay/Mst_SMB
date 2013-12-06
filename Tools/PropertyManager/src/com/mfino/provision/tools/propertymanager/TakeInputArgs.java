package com.mfino.provision.tools.propertymanager;

import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TakeInputArgs
{

	public static PMObject getInputs(String[] args, Logger logger, PMObject argsFromMain) throws Exception
	{

		for (int i = 0; i < args.length; i++) {
			if (args[i].equalsIgnoreCase("-help")) {
				logger.log(Level.FINEST, "processing the flag -help");
				logger.log(Level.SEVERE, PropertyManagerConstants.HELP_DETAILS);
				System.exit(0);
			} else if (args[i].equalsIgnoreCase("-debug")) {
				logger.log(Level.FINEST, "processing the flag -debug");
				// fh.setLevel(Level.ALL);
				argsFromMain.setDebug(true);
			} else if (args[i].equalsIgnoreCase("-log")) {
				logger.log(Level.FINEST, "log file saved in " + args[i + 1]);
				argsFromMain.setLogFileLocation(args[i + 1]);
				File fn = new File(args[i + 1]);
				if (!fn.exists()) {
					logger.log(Level.SEVERE, "log file location invalid !!. Stored in default location.");
					argsFromMain.setLogFileLocation(PropertyManagerConstants.logLocation);
				}
				i = i + 1;
			} else if (args[i].equalsIgnoreCase("-checklist")) {
				// String checkListPath = args[i + 1];
				File fileChecklist = new File(args[i + 1]);
				argsFromMain.setFstreamChecklist(new FileInputStream(fileChecklist));
				i = i + 1;
			} else if (args[i].equalsIgnoreCase("-verifydetails")) {
				// String checkListPath = args[i + 1];
				File fileChecklist = new File(args[i + 1]);
				argsFromMain.setFstreamChecklist(new FileInputStream(fileChecklist));
				i = i + 1;
			} else if (args[i].equalsIgnoreCase("-deployVerification")) {
				argsFromMain.setDeployVerification(true);
				File fileChecklist1 = new File(PropertyManagerConstants.nameOfFileWithFolderDetails);
				if (fileChecklist1.exists()) {
					argsFromMain.setFstreamChecklist(new FileInputStream(fileChecklist1));
				} else if (args[i + 1].equalsIgnoreCase("-verifydetails")) {
					continue;
				} else {
					logger.log(Level.SEVERE, "Invalid Flad Enter '-help' for more details");
				}
			} else if (args[i].equalsIgnoreCase("-deployToProduction")) {

				argsFromMain.setMakeChangesAtProduction(true);

			} else if (args[i].equalsIgnoreCase("-configFile")) {
				// configFile = true;
				argsFromMain.setConfigFileLocation(args[i + 1]);
				i = i + 1;
			} else if (args[i].equalsIgnoreCase("-getEncryptedKey")) {
				String decryptedValue = getEncryptionKey.getDecryptedString(logger);
				boolean useSalt = false;
				logger.log(Level.SEVERE, "\n");
				String newPassword1 = CipherUtil.passwordDoubleCheck("Key to Encrypt the sensitive information:");
				String StringWithEncryption = CipherUtil.encrypt(newPassword1, decryptedValue, useSalt);
				logger.log(Level.SEVERE, "New Encrypted Key is:");
				logger.log(Level.SEVERE, StringWithEncryption);
				System.exit(0);

			} else if (args[i].equalsIgnoreCase("-keyCheckValue")) {
				getEncryptionKey.keycheckvalue(logger);
				System.exit(0);
			} else {
				logger.log(Level.SEVERE, "invalid flag " + args[i] + "use \"-help\" for more information");
				System.exit(0);
			}

		}
		return argsFromMain;

	}
}
