package com.mfino.mce.iso.jpos.unused;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.mce.iso.jpos.nm.MessageType;
import com.mfino.mce.iso.jpos.nm.NMStatus;
import com.mfino.mce.iso.jpos.nm.StatusRegistrar;
import com.mfino.mce.iso.jpos.unused.task.NetworkTask;
import com.mfino.mce.iso.jpos.unused.task.TaskFactory;
import com.mfino.mce.iso.jpos.unused.task.exec.TaskExecutor;
import com.mfino.mce.iso.jpos.util.ISOUtil;

public class EchoRunner {

	private static Logger	log	           = LoggerFactory.getLogger(EchoRunner.class);

	long	                timeout	       = 30000;
	long	                sleepOnNoReply	= 10000;
	private String	        muxName;
	private String	        channelName;

	private ExecutorService	executorService;
	private TaskExecutor	echoexecutor;

	private Timer	        echoTimer;

	public EchoRunner(long timeout, String muxName, String channelName, long sleepOnNoReply, ISOUtil isoUtil, long echoInterval) {
	
		this.timeout = timeout;
		this.muxName = muxName;
		this.channelName = channelName;
		this.sleepOnNoReply = sleepOnNoReply;
		this.isoUtil = isoUtil;
		this.echoInterval = echoInterval;

	}

	public void start() {

		log.info("starting the EchoInitiator");

		this.echoexecutor = new TaskExecutor();
		this.echoexecutor.setDelayBetweenTasks(echoInterval);
		this.executorService = Executors.newSingleThreadExecutor();

		this.executorService.submit(echoexecutor);

		log.info("starting the echo timer with echointerval=" + echoInterval);
		this.echoTimer = new Timer();
		this.echoTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				NetworkTask echo = TaskFactory.getInstance().getTask(MessageType.Echo, muxName, timeout, channelName, isoUtil);
				echoexecutor.submitTask(echo);

			}
		}, timeout, echoInterval);

	}

	public void stop() {
		log.info(muxName + " JPOSManager : stop() : channel=" + channelName);
		log.info("setting echo status of mux to Illegal");
		StatusRegistrar.setEchoStatus(muxName, NMStatus.Illegal);

		log.info("stopping the echotimer");
		this.echoTimer.cancel();
		log.info("stopping the echoexecutor");
		this.echoexecutor.stop();
		this.executorService.shutdownNow();

	}

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

	public ISOUtil getIsoUtil() {
		return isoUtil;
	}

	public void setIsoUtil(ISOUtil isoUtil) {
		this.isoUtil = isoUtil;
	}

	private ISOUtil	isoUtil;

	private long	echoInterval;

	public long getEchoInterval() {
		return echoInterval;
	}

	public void setEchoInterval(long echoInterval) {
		this.echoInterval = echoInterval;
	}

}