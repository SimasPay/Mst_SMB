/**
 * 
 */
package com.mfino.scheduler.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.mail.EmailException;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Service;

import com.mfino.scheduler.service.KYCUpgradeSchedulerService;
import com.mfino.service.MailService;
import com.mfino.transactionapi.service.impl.KYCUpgradeServiceImpl;
import com.mfino.util.ConfigurationUtil;

/**
 * @author Bala Sunku
 *
 */
@Service("KYCUpgradeSchedulerServiceImpl")
public class KYCUpgradeSchedulerServiceImpl implements KYCUpgradeSchedulerService {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("KYCUpgradeServiceImpl")
	private KYCUpgradeServiceImpl kycUpgradeServiceImpl;
	
	@Autowired
	@Qualifier("MailServiceImpl")
	private MailService mailService;
	
	private HibernateTransactionManager txManager;
	private String dateString = null;
	private String newRemoteFilePath;
	
	public HibernateTransactionManager getTxManager() {
		return txManager;
	}

	public void setTxManager(HibernateTransactionManager txManager) {
		this.txManager = txManager;
	}

	/**
	 * Process the KYC Upgrade file from FTP server
	 */
	@Override
	public void processKYCUpgrade() {
		log.info("Begin::Processing the KYC Upgrade file from FTP server");
//		Step 1: Download the file from FTP server.
		String outputFilePath = null;
		boolean processStatus = false;
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
		dateString = sdf.format(new Date());
		String currentDir = System.getProperty("user.dir");
		String downloadFilePath = currentDir + "/kycupgrade_"+ dateString + ".csv";
		boolean downloadStatus = downloadFileFromFtpServer(downloadFilePath);

//		Step 2: Process the downloaded file
		if (downloadStatus) {
			processStatus = kycUpgradeServiceImpl.processKYCUpgradeFile(downloadFilePath);
			outputFilePath = kycUpgradeServiceImpl.generateFilePath(downloadFilePath, "_output");

//		Step 3: Upload the output file to FTP server.
			uploadFileToFTPServer(outputFilePath);
			if (processStatus) {
				String to = ConfigurationUtil.getKYCUpgradeNotifyEmail();
				String subject = ConfigurationUtil.getKYCUpgradeEmailSubject();
				String message = ConfigurationUtil.getKYCUpgradeEmailMessage();
				try {
					mailService.sendMail(to, to, subject, message);
				} catch (EmailException e) {
					log.error("Error while sending the kyc upgrade completion e-mail to: " + to);
				}
			}
			else {
				log.error("Error while processing the file: " + downloadFilePath);
			}
		}
		log.info("End::Processing the KYC Upgrade file from FTP server");
	}
	
	/**
	 * Creates FTP connection
	 * @return
	 * @throws IOException
	 * @throws SocketException
	 */
	private FTPClient getFTPClient() throws IOException, SocketException {
		FTPClient ftpClient = new FTPClient();
		ftpClient.connect(ConfigurationUtil.getKYCUpgradeFTPServer(), ConfigurationUtil.getKYCUpgradeFTPPort());
		ftpClient.login(ConfigurationUtil.getKYCUpgradeFTPUser(), ConfigurationUtil.getKYCUpgradeFTPPassword());
		ftpClient.enterLocalPassiveMode();
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		return ftpClient;
	}
	
	/**
	 * Close the FTP connection
	 * @param ftpClient
	 */
	private void closeFTPClient(FTPClient ftpClient) {
		try {
            if (ftpClient!=null && ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (IOException e) {
        	log.error("Error while closing the FTP conncetion", e);
        }
	}
		
	/**
	 * Downloading the file from FTP server
	 * @param downloadFilePath
	 * @return
	 */
	private boolean downloadFileFromFtpServer(String downloadFilePath) {
		
		boolean downloadStatus = false;
		FTPClient ftpClient = null;
		String remoteFilePath = ConfigurationUtil.getKYCUpgradeFTPDownloadFilePath();
		try {
			ftpClient = getFTPClient();
	        InputStream is = ftpClient.retrieveFileStream(remoteFilePath);
	        int returnCode = ftpClient.getReplyCode();
	        if (is == null || returnCode == 550) {
	        	log.info("No file in the FTP server to process at: " + remoteFilePath);
	        	downloadStatus = false;
	        }
	        else {
				log.info("Downloading the file from FTP server into local path: " + downloadFilePath);
				OutputStream os = new BufferedOutputStream(new FileOutputStream(downloadFilePath));
				byte[] bytes = new byte[4096];
				int readBytes = -1;
				while ((readBytes = is.read(bytes)) != -1) {
					os.write(bytes, 0, readBytes);
	            }
	 
				downloadStatus = ftpClient.completePendingCommand();
				if (downloadStatus) {
					log.info("File at FTP location: " + remoteFilePath + " has been donwloaded successfully");
					// Rename the file on FTP server after processing
					newRemoteFilePath = kycUpgradeServiceImpl.generateFilePath(remoteFilePath, "_"+dateString);
					ftpClient.rename(remoteFilePath, newRemoteFilePath);
				}
				else {
					log.error("Error while downloading the file at FTP location: " + remoteFilePath);
				}
				os.close();
				is.close();	
	        }
		} catch (SocketException e) {
			log.error("Error: Socket exception while downloading the file at FTP location: " + remoteFilePath, e);
		} catch (FileNotFoundException e) {
			log.error("Error: File not found exception while downloading the file at FTP location: " + remoteFilePath, e);
		} catch (IOException e) {
			log.error("Error: IO Exception while downloading the file at FTP location: " + remoteFilePath, e);
		} finally {
			closeFTPClient(ftpClient);
        }
		
		return downloadStatus;
	}
	
	/**
	 * Upload the output file after processing the KYC upgrade file to FTP
	 * @param outputFilePath
	 * @return
	 */
	private boolean uploadFileToFTPServer(String outputFilePath) {
		log.info("Uploading the file at local path: " + outputFilePath + " to FTP server");
		FTPClient ftpClient = null;
		
		String remoteUploadFilePath = kycUpgradeServiceImpl.generateFilePath(newRemoteFilePath, "_output");
		boolean uploadStatus = false;
		try {
			ftpClient = getFTPClient();
			File outputFile = new File(outputFilePath);
			InputStream is = new FileInputStream(outputFile);
			uploadStatus = ftpClient.storeFile(remoteUploadFilePath, is);
			is.close();
			if (uploadStatus) {
				log.info("Output file is uploaded to FTP atlocation: " + remoteUploadFilePath);
			}
			else {
				log.error("Error while uploading the output file to FTP location: " + remoteUploadFilePath);
			}
		} catch (SocketException e) {
			log.error("Error: Socket exception while uploading the output file to FTP location: " + remoteUploadFilePath, e);
		} catch (FileNotFoundException e) {
			log.error("Error: File not found exception while uploading the output file to FTP location: " + remoteUploadFilePath, e);
		} catch (IOException e) {
			log.error("Error: IOexception while uploading the output file to FTP location: " + remoteUploadFilePath, e);
		} finally {
			closeFTPClient(ftpClient);
		}
		
		return uploadStatus;
	}
}
