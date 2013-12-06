package com.mfino.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.URL;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SAPGroupIDDAO;
import com.mfino.domain.SAPGroupID;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.HibernateUtil;

/**
 * 
 * @author xchen
 */
public class SmartGroupIDImport {
	private static final Logger log = LoggerFactory.getLogger(SmartGroupIDImport.class);
	private Options options = null;

	@SuppressWarnings("static-access")
	public SmartGroupIDImport() {
		// create the Options
		options = new Options();
		options.addOption(OptionBuilder
						.withLongOpt("ftp-url")
						.withDescription("url to the ftp directory that contains the SAP import files. if there are more than one file in the dirctory, only the first file will be imported. if user name and password is required, it should be in the url too.")
						.hasArg().withArgName("URL").create());
		options.addOption(OptionBuilder.withLongOpt("username")
				.withDescription("user name to login to the ftp server")
				.hasArg().withArgName("USERNAME").create());
		options.addOption(OptionBuilder.withLongOpt("password")
				.withDescription("password to login to the ftp server")
				.hasArg().withArgName("PASSWORD").create());
	}

	public void run(){
		runInternal(ConfigurationUtil.getGroupIDImportFtpServer(),
				ConfigurationUtil.getGroupIDImportFtpUsername(),
				ConfigurationUtil.getGroupIDImportFtpPassword());
	}
	private void runInternal(String url, String username, String password) {
		log.info("Start Smart groupid import");
		
		try {
			URL ftpUrl = new URL(url);

			FTPClient f = new FTPClient();
			f.connect(ftpUrl.getHost());
			if (StringUtils.isNotBlank(username)
					&& StringUtils.isNotBlank(password)) {
				f.login(username, password);
			}

			if(f.changeWorkingDirectory("~/" + ftpUrl.getPath()) == false){
				log.error("Failed to switch to directory " + ftpUrl.getPath());
				return;
			}
			
			FTPFile[] files = f.listFiles();
			if (files.length > 0) {
				FTPFile file = files[0];
				File output = new File(ConfigurationUtil.getTempDir(), file.getName());
				OutputStream outputStream = new FileOutputStream(output);

				boolean success = f.retrieveFile(file.getName(), outputStream);
				if (success) {
					outputStream.close();
				} else {
					log.error("Failed to download GroupID file from ftp");
					return; 
				}

				BufferedReader reader = new BufferedReader(new FileReader(output));
				String strLine = null;
				int lineNum = 0;
				// there is really nothing much to verify
				// in the sample file, there is lines with id only, also the
				// id is not nessesarily a number
				// while ((strLine = reader.readLine()) != null) {
				// lineNum ++;
				// String[] segments = strLine.split("\t");
				// //check if there are 2 segments
				// if (segments.length != 2 || Pattern.matches("^\\d*$",
				// segments[0]) == false) {
				// DefaultLogger.error(String.format("Format error at line %s: %s",
				// lineNum, strLine));
				// return;
				// }
				// }
				//
				// reader.close();
				// reader = new BufferedReader(new FileReader(output));

				try {
					Session session = HibernateUtil.getCurrentSession();
					session.beginTransaction();

					String hqlDelete = "delete SAPGroupID";
					int deletedEntities = session.createQuery(hqlDelete)
							.executeUpdate();
					log.info(String.format(
							"Deleted all %s SAPGroupID records.",
							deletedEntities));

					SAPGroupIDDAO dao = DAOFactory.getInstance().getSAPGroupIDDAO();
					while ((strLine = reader.readLine()) != null) {
						String[] segments = strLine.split("\t");
						// save it to the database
						SAPGroupID groupid = new SAPGroupID();
						groupid.setCreatedBy("system");
						groupid.setUpdatedBy("system");
						groupid.setGroupID(segments[0]);
						if (segments.length > 1) {
							groupid.setGroupIDName(segments[1]);
						}
						dao.saveWithoutFlush(groupid);
					}
					HibernateUtil.getCurrentTransaction().commit();
				} catch (Exception ex) {
					log.error(
							"Error in saving records to SAPGroupID table", ex);
					HibernateUtil.getCurrentTransaction().rollback();
				}
			} else {
				log.info("No SAP file found. Exiting...");
				return;
			}
		} catch (SocketException socketEx) {
			log.error("Failed to connect to ftp server.", socketEx);
		} catch (IOException ioEx) {
			log.error("Failed to read files from ftp server", ioEx);
		}
	}

	public static void main(String[] args) {
		SmartGroupIDImport program = new SmartGroupIDImport();
		program.run();
		
		/*
		// args = new
		// String[]{"--ftp-url=ftp://localhost/xchen/Desktop/ftptest",
		// "--username=xchen", "--password=11223344"};
		SmartGroupIDImport program = new SmartGroupIDImport();
		// create the command line parser
		CommandLineParser parser = new PosixParser();

		try {
			// parse the command line arguments
			CommandLine line = parser.parse(program.options, args);

			if (line.hasOption("ftp-url") == false) {
				program.printHelp();
			}

			try {
				new URL(line.getOptionValue("ftp-url"));
			} catch (MalformedURLException urlEx) {
				DefaultLogger.error("Ftp url is in a wrong format. e.g. ftp://somehost:21/dirtofile");
				return;
			}

			program.runInternal(line.getOptionValue("ftp-url"), line
					.getOptionValue("username"), line
					.getOptionValue("password"));
		} catch (ParseException commondParserEx) {
			program.printHelp();
		}
		*/
	}

	public void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("SmartGroupIDImport", options);
	}
}
