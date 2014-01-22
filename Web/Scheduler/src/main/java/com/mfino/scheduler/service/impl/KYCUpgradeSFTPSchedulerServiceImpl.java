/**
 * 
 */
package com.mfino.scheduler.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.mail.EmailException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.stereotype.Service;

import com.mfino.scheduler.service.KYCUpgradeSFTPSchedulerService;
import com.mfino.service.MailService;
import com.mfino.transactionapi.service.KYCUpgradeService;
import com.mfino.util.ConfigurationUtil;

/**
 * @author Bala Sunku
 *
 */
@Service("KYCUpgradeSFTPSchedulerServiceImpl")
public class KYCUpgradeSFTPSchedulerServiceImpl implements KYCUpgradeSFTPSchedulerService {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("KYCUpgradeServiceImpl")
	private KYCUpgradeService kycUpgradeServiceImpl;
	
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
	 * Establishing the SFTP connection for the given remote file path.
	 * @param remoteFilePath
	 * @return
	 */
    private String createConnectionString(String remoteFilePath) {
    	String hostName = ConfigurationUtil.getKYCUpgradeFTPServer();
    	String userName = ConfigurationUtil.getKYCUpgradeFTPUser();
    	String password = ConfigurationUtil.getKYCUpgradeFTPPassword();
    	return "sftp://" + userName + ":" + password + "@" + hostName + "/" + remoteFilePath;
    }
    
    /**
     * Creating the default file system options for SFTP
     * @return
     * @throws FileSystemException
     */
    private FileSystemOptions createDefaultOptions() throws FileSystemException {
        // Create SFTP options
        FileSystemOptions opts = new FileSystemOptions();

        // SSH Key checking
        SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");

        // Root directory set to user home
        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, false);

        // Timeout is count by Milliseconds
        SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, 60000);

        return opts;
    }
    
    /**
     * Downloads the file from FTP server
     * @param downloadFilePath
     * @return
     */
    private boolean downloadFileFromFtpServer(String downloadFilePath) {
    	boolean downloadStatus = false;
        StandardFileSystemManager manager = new StandardFileSystemManager();
        String remoteFilePath = ConfigurationUtil.getKYCUpgradeFTPDownloadFilePath();
        FileObject localFile = null;
        FileObject remoteFile = null;
        FileObject newRemoteFile = null;
        try {
			manager.init();
			// Create local file object
			localFile = manager.resolveFile(downloadFilePath);

			// Create remote file object
			remoteFile = manager.resolveFile(createConnectionString(remoteFilePath), createDefaultOptions());

			// Copy to local file from sftp server
			if (remoteFile.exists()) {
				localFile.copyFrom(remoteFile, Selectors.SELECT_SELF);
				downloadStatus = true;
				log.info("File at FTP location: " + remoteFilePath + " has been donwloaded successfully to " + downloadFilePath);
				
				// Rename the file on FTP server after downloading
				newRemoteFilePath = kycUpgradeServiceImpl.generateFilePath(remoteFilePath, "_"+dateString);
				newRemoteFile = manager.resolveFile(createConnectionString(newRemoteFilePath), createDefaultOptions());
				remoteFile.moveTo(newRemoteFile);
				log.info("Remote file on FTP has been renamed to " + newRemoteFilePath);
			}
			else {
				log.info("No file exists at FTP location: " + remoteFilePath);
			}

        } catch (FileSystemException e) {
			log.error("Error: FileSystemException while downloading the file at FTP location: " + remoteFilePath, e);
			downloadStatus = false;
		} catch (Exception e) {
			log.error("Error: Exception while downloading the file at FTP location: " + remoteFilePath, e);
			downloadStatus = false;
		} finally {
			try {
				if (newRemoteFile != null)
					newRemoteFile.close();
				if (remoteFile != null)
					remoteFile.close();
				if (localFile != null) 
					localFile.close();
			} catch (Exception e) {
				log.error("Error: Exception while closing the file object references", e);
			}
            manager.close();
		}
        return downloadStatus;
    }

    /**
     * Upload the output file after processing the KYC upgrade file to FTP
     * @param outputFilePath
     */
    private void uploadFileToFTPServer(String outputFilePath) {
    	log.info("Uploading the file at local path: " + outputFilePath + " to FTP server");
        
    	FileObject localFile = null;
    	FileObject remoteFile = null;
        StandardFileSystemManager manager = new StandardFileSystemManager();
        String remoteUploadFilePath = kycUpgradeServiceImpl.generateFilePath(newRemoteFilePath, "_output");
        try {
			manager.init();
			// Create local file object
			localFile = manager.resolveFile(outputFilePath);

			// Create remote file object
			remoteFile = manager.resolveFile(createConnectionString(remoteUploadFilePath), createDefaultOptions());

			// Copy local file to sftp server
			remoteFile.copyFrom(localFile, Selectors.SELECT_SELF);
			log.info("Output file is uploaded to FTP at location: " + remoteUploadFilePath);
			
		} catch (FileSystemException e) {
			log.error("Error: FileSystemException while uploading the output file to FTP location: " + remoteUploadFilePath, e);
		} catch (Exception e) {
			log.error("Error: Exception while uploading the output file to FTP location: " + remoteUploadFilePath, e);
		} finally {
			try {
				if (localFile != null)
					localFile.close();
				if (remoteFile != null) 
					remoteFile.close();
			} catch (Exception e) {
				log.error("Error: Exception while closing the file object references", e);
			}
            manager.close();
		}
    }
}
