package com.dimo.fuse.reports;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dimo.fuse.reports.scheduler.ReportSchedulerProperties;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class FtpService {
	private Log log = LogFactory.getLog(this.getClass());
	private String url;
	private String username;
	private String password;
	private int port = 22;
	
	public FtpService() { }
	
	public FtpService(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}
	
	public void init(){
		this.url = ReportSchedulerProperties.getFtpHost();
		this.username = ReportSchedulerProperties.getFtpUsername();
		this.password = ReportSchedulerProperties.getFtpPassword();
		this.port = Integer.valueOf(ReportSchedulerProperties.getFtpPort()) ;
	}
	
	public boolean sendThroughSftp(String localFullpathFile, String remoteFullPathFile) {
		log.info("Prepare to send file through SFTP");
		Session session = null;
	    Channel channel = null;
	    try {
	        JSch ssh = new JSch();
	        session = ssh.getSession(username, url, port);
	        session.setPassword(password);
	        session.setConfig("StrictHostKeyChecking", "no");
	        session.connect();
	        channel = session.openChannel("sftp");
	        channel.connect();
	        ChannelSftp sftp = (ChannelSftp) channel;
	        sftp.put(localFullpathFile, remoteFullPathFile);
	        log.info("Successfully Transfer to SFTP Server");
	        return true;
	    } catch (JSchException e) {
	        log.error("Error", e);
	    } catch (SftpException e) {
	    	log.error("Error", e);
	    } finally {
	        if (channel != null) {
	            channel.disconnect();
	        }
	        if (session != null) {
	            session.disconnect();
	        }
	    }
		
		return false;
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

	public static void main(String[] args) {
		FtpService service = new FtpService("repo.dimo.co.id", "simobi", "bsim@2016");
		service.sendThroughSftp("/Users/dimo/Simaspay/Reports/glreport_20170324.txt", "/home/simobi/glreport/glreport_20170324.txt");
	}
}
