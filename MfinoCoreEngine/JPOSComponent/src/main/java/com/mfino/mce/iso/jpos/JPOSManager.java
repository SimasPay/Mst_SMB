package com.mfino.mce.iso.jpos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.mce.iso.jpos.util.ISOUtil;

/**
 * 
 * @author POCHADRI JPOS Manager class, create an instance of this class and run
 *         start to start the JPOS Q2 client for sending and receiving messages
 * 
 */

public class JPOSManager {
	private ISOUtil	        isoUtil;
	private static Logger	log	           = LoggerFactory.getLogger(JPOSManager.class);
	// JPOS Q2 is kind of container, its expects a deploy folder in which the
	// configuration
	// files reside.
	// this folder need to have the
	// 1. connection parameters etc for connecting to the server
	// 2. QBean's that define the services running
	// 3. space configuration which would be used in the routing logic.
	private ChannelObserver	observer;
	private String	        deployFolder	= "jpos/deploy";
	long	                timeout	       = 30000;
	long	                sleepOnNoReply	= 10000;

	private String	        muxName;
	private String	        channelName;

//	private EchoRunner	    echoRunner;

	private RepeatedMessageSender echoSender;
	
	private long	        echoInterval;

	public long getEchoInterval() {
		return echoInterval;
	}

	public void setEchoInterval(long echoInterval) {
		this.echoInterval = echoInterval;
	}

	public long getSleepOnNoReply() {
		return sleepOnNoReply;
	}

	public void setSleepOnNoReply(long sleepOnNoReply) {
		this.sleepOnNoReply = sleepOnNoReply;
	}

	public String getDeployFolder() {
		return deployFolder;
	}

	public void setDeployFolder(String deployFolder) {
		this.deployFolder = deployFolder;
	}

	public JPOSManager() {

	}

	public void start() {
		observer = new ChannelObserver(timeout, muxName, channelName, sleepOnNoReply, isoUtil);
		this.echoSender = new Echo();
		this.echoSender.setEchoInterval(echoInterval);
		this.echoSender.setIsoUtil(isoUtil);
		this.echoSender.setMuxName(muxName);
		this.echoSender.setSleepOnNoReply(sleepOnNoReply);
		this.echoSender.setTimeout(timeout);
		
		try {
			Thread.sleep(10000);
			observer.start();
			log.info("Started sign on Observer " + muxName);
			this.echoSender.start();
			log.info("started echosender on mux=" + muxName);
		}
		catch (Exception e) {
			log.error(muxName + " Error Staring jposmanager, there might be no sign on requests to iso server", e);
		}
	}

	public void stop() {
		log.info(muxName + " JPOSManager : stop() : channel=" + channelName);

		try {
			observer.stop();
			this.echoSender.stop();
		}
		catch (Exception ex) {
			log.error(muxName + " Error stopping jposmanager", ex);
		}

	}

	public String getMuxName() {
		return muxName;
	}

	public void setMuxName(String muxName) {
		this.muxName = muxName;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public ISOUtil getIsoUtil() {
		return isoUtil;
	}

	public void setIsoUtil(ISOUtil isoUtil) {
		this.isoUtil = isoUtil;
	}

}
