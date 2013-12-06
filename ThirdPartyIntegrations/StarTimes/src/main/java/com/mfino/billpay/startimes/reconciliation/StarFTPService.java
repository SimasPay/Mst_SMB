package com.mfino.billpay.startimes.reconciliation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class StarFTPService {
	private Log log = LogFactory.getLog(this.getClass());
	private String url;
	private String username;
	private String password;
	private String remoteDirPath;

	public boolean send(File file) {
		FTPClient ftp = new FTPClient();
		FileInputStream inputStream = null;
		try {
			ftp.connect(url);
			log.info("Connected to ftp server at:" + url);
			int reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				log.error("FTP server refused connection.");
				return false;
			}
			if (!ftp.login(username, password)) {
				log.error("FTP Login failed.");
				return false;
			}
			if (StringUtils.isNotBlank(remoteDirPath)) {
				reply = ftp.cwd(remoteDirPath);
				if (!FTPReply.isPositiveCompletion(reply)) {
					log.error("Failed to change directory.");
					return false;
				}
			}
			inputStream = new FileInputStream(file);
			if (!ftp.storeFile(file.getName(), inputStream)) {
				log.error(" Transfer file:" + file.getPath() + " failed. ");
				return false;
			}
			log.info("Successfully Transfered file:" + file.getPath());
		} catch (IOException e) {
			log.error("Error in StarFTPUtil::send()", e);
			return false;
		} finally {
			if (ftp.isConnected()) {
				try {
					log.info("Closing ftp connection");
					ftp.disconnect();
				} catch (IOException ioe) {
					// log.error("Error in StarFTPUtil::send() disconnect",e);
				}
			}
			if(inputStream!=null){
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error("Failed to Close file stream");
				}
			}
		}
		return true;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setRemoteDirPath(String remoteDirPath) {
		this.remoteDirPath = remoteDirPath;
	}

	public static void main(String a[]) {
		StarFTPService ftp = new StarFTPService();
		ftp.setUrl("ftp.mfino.com");
		ftp.setUsername("qa");
		ftp.setPassword("qa@350");
		ftp.setRemoteDirPath("startimes");
		File file = new File("D:/servicemixcmd.txt");
		ftp.send(file);
	}

}
