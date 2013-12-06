package com.mfino.mce.iso.jpos;

import static com.mfino.mce.iso.jpos.camel.util.ISOUtil.sleep;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.util.NameRegistrar.NotFoundException;

import com.mfino.mce.iso.jpos.nm.NMStatus;
import com.mfino.mce.iso.jpos.nm.StatusRegistrar;
import com.mfino.mce.iso.jpos.util.ISOUtil;

public abstract class RepeatedMessageSender {
	protected ISOUtil	isoUtil;
	Log	              log	= LogFactory.getLog(RepeatedMessageSender.class);
	ExecutorService	  es;

	public long getEchoInterval() {
		return echoInterval;
	}

	public void setEchoInterval(long echoInterval) {
		this.echoInterval = echoInterval;
	}

	public String getMuxName() {
		return muxName;
	}

	public void setMuxName(String muxName) {
		this.muxName = muxName;
	}

	private boolean	stopExecuton;

	long	        echoInterval;
	String	        muxName;
	long	        timeout	       = 30000;
	long	        sleepOnNoReply	= 10000;

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public long getSleepOnNoReply() {
		return sleepOnNoReply;
	}

	public void setSleepOnNoReply(long sleepOnNoReply) {
		this.sleepOnNoReply = sleepOnNoReply;
	}

	public RepeatedMessageSender() {
		es = Executors.newSingleThreadExecutor();
	}

	protected abstract ISOMsg getISOMsgToSend() throws ISOException;

	protected abstract String getNoReplyMessage();

	protected ISOMsg sendAndReceive(ISOMsg msg, long timeout, String muxName) throws ISOException, NotFoundException {
		return isoUtil.sendAndReceive(msg, timeout, muxName);
	}

	void start() {
		log.info("Starting the echo thread, echo would be every " + echoInterval + " milli seconds muxName=" + muxName);
		es.execute(new Runnable() {

			@Override
			public void run() {
				// send echo messages in a loop
				while (!stopExecuton && !Thread.interrupted()) {
					NMStatus signOnstatus = StatusRegistrar.getSignonStatus(muxName);
					NMStatus newStatus = NMStatus.Illegal;
					if (!signOnstatus.equals(NMStatus.Successful)) {
						// sign on not completed sleep for sometime
						sleep(sleepOnNoReply);
						continue;
					}
					try {
						ISOMsg echo = getISOMsgToSend();
						ISOMsg echoReply = sendAndReceive(echo, timeout, muxName);

						if (echoReply == null || !isoUtil.getEchoSuccessResponseCode().equals(echoReply.getString(39))) {
							newStatus = NMStatus.Failed;
							log.warn("no reply to echo message, will resend echo after " + echoInterval + " milli seconds muxName=" + muxName);
						}
						else {
							newStatus = NMStatus.Successful;
							log.info("Reply reveived for echo sent at " + echo.getString(12) + " for de-11 "+echo.getString(11)+" will sleep for " + echoInterval
							        + " milli seconds muxName=" + muxName);
						}
					}
					catch (ISOException e) {
						newStatus = NMStatus.Failed;
						log.warn(muxName + "error sending a echo message, wil retry ", e);
					}
					catch (NotFoundException e) {
						newStatus = NMStatus.Failed;
						log.error(muxName + "cannot find the mux used for sending echo, check configuration\n", e);
					}
					catch (Throwable t) {
						newStatus = NMStatus.Failed;
						log.error(muxName + "unexpected exception, check the reason, wil continue hoping it will be resolved", t);
					}
					finally {
					}

					StatusRegistrar.setEchoStatus(muxName, newStatus);
					sleep(echoInterval);

				}

			}
		});
	}

	void stop() {
		this.stopExecuton = true;
		es.shutdown();
	}

	public ISOUtil getIsoUtil() {
		return isoUtil;
	}

	public void setIsoUtil(ISOUtil isoUtil) {
		this.isoUtil = isoUtil;
	}

}
