package com.mfino.mce.iso.jpos;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.util.NameRegistrar.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.mce.iso.jpos.nm.NMStatus;
import com.mfino.mce.iso.jpos.nm.StatusRegistrar;
import com.mfino.mce.iso.jpos.util.ISOUtil;

public class Signon implements Runnable {

	Logger	        log	= LoggerFactory.getLogger(Signon.class);

	private boolean	stopExecution;

	public void stop() {
		this.stopExecution = true;
	}

	@Override
	public void run() {
		log.info("Connected to ISO server,will do a sign on message muxName=" + muxName);
		doSignon();

	}

	private void doSignon() {

		while (!this.stopExecution && !Thread.interrupted()) {

			NMStatus status = StatusRegistrar.getSignonStatus(muxName);
			if (NMStatus.Successful.equals(status)) {
				log.info("Signon was done already.Not doing again");
				break;
			}

			try {
				ISOMsg signOn = isoUtil.getSignOnMessage();
				ISOMsg replyMsg = isoUtil.sendAndReceive(signOn, timeout, muxName);
				if (replyMsg != null && replyMsg.getString(39).equals(isoUtil.getSignOnSuccessResponseCode())) {
					log.info("Got reply for sign on and it's successful mux=" + muxName);
					status = NMStatus.Successful;
					StatusRegistrar.setSignonStatus(muxName, status);
					break;
				}
				else {
					status = NMStatus.Failed;
					log.info("signon failed for muxname=" + muxName);
				}
			}
			catch (NotFoundException e) {
				log.error(muxName + " Could not find the Name for sending sign on", e);
				status = NMStatus.Failed;
			}
			catch (ISOException e) {
				log.error(muxName + ", Exception creating the iso message for sign on", e);
				status = NMStatus.Failed;
			}
			StatusRegistrar.setSignonStatus(muxName, status);
			isoUtil.sleep(sleepOnNoReply);

		}

	}

	private String	muxName;
	private long	timeout	= 30000;
	private String	channelName;

	private long	sleepOnNoReply;

	public long getSleepOnNoReply() {
		return sleepOnNoReply;
	}

	public void setSleepOnNoReply(long sleepOnNoReply) {
		this.sleepOnNoReply = sleepOnNoReply;
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

	private ISOUtil	isoUtil;

	public ISOUtil getIsoUtil() {
		return isoUtil;
	}

	public void setIsoUtil(ISOUtil isoUtil) {
		this.isoUtil = isoUtil;
	}

}
