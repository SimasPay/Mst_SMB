package com.mfino.mce.iso.jpos;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.net.ntp.TimeInfo;
import org.jpos.iso.BaseChannel;
import org.jpos.iso.ISOChannel;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.util.NameRegistrar.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.mce.iso.jpos.nm.NMStatus;
import com.mfino.mce.iso.jpos.nm.StatusRegistrar;
import com.mfino.mce.iso.jpos.util.ISOUtil;

public class ChannelObserver implements Observer {
	private ISOUtil	        isoUtil;
	private static Logger	log	    = LoggerFactory.getLogger(ChannelObserver.class);

	private String	        muxName;
	private long	        timeout	= 30000;
	private String	        channelName;
	// private TaskExecutor nmExecutor;

	private ExecutorService	executorService;

	private long	        sleepOnNoReply;

	public long getSleepOnNoReply() {
		return sleepOnNoReply;
	}

	public void setSleepOnNoReply(long sleepOnNoReply) {
		this.sleepOnNoReply = sleepOnNoReply;
	}
	
	private Signon signon;

	public ChannelObserver(long timeout, String muxName, String channelName, long sleepOnNoReply, ISOUtil isoUtil) {
		this.timeout = timeout;
		this.muxName = muxName;
		this.channelName = channelName;
		this.sleepOnNoReply = sleepOnNoReply;
		this.isoUtil = isoUtil;
		// this.nmExecutor = new TaskExecutor();
		// this.nmExecutor.setDelayBetweenTasks(sleepOnNoReply);

		this.executorService = Executors.newSingleThreadExecutor();

		log.info("ChannelObserver constructor called mux=" + muxName + ", isoUtil=" + isoUtil.getClass());
	}

	public void start() throws Exception {

		this.signon = new Signon();
		signon.setIsoUtil(isoUtil);
		signon.setMuxName(muxName);
		signon.setSleepOnNoReply(sleepOnNoReply);
		signon.setTimeout(timeout);
		signon.setChannelName(channelName);
		
		ISOChannel ch = BaseChannel.getChannel(channelName);
		if (ch != null && ch instanceof BaseChannel) {
			((BaseChannel) ch).addObserver(this);
		}

		updateChannel(ch);

	}

	private void updateChannel(ISOChannel ch) {

		log.info("channel with mux=" + muxName + " connected.");
		if (ch.isConnected()) {
			// NetworkTask signon =
			// TaskFactory.getInstance().getTask(MessageType.Signon, muxName,
			// timeout, channelName, this.isoUtil);
			// this.nmExecutor.submitTask(signon);
			
			log.info("Adding sign on task to thread pool muxName=" + muxName);
			executorService.submit(this.signon);

		}
		else {
			log.info("channel with mux=" + muxName + " disconnected.");
			// NetworkTask discoTask =
			// TaskFactory.getInstance().getTask(MessageType.Signoff, muxName,
			// timeout, channelName, this.isoUtil);
			// this.nmExecutor.submitTask(discoTask);
			StatusRegistrar.setSignonStatus(muxName, NMStatus.Disconnected);
		}
	}

	public void stop() throws Exception {

		ISOChannel ch = BaseChannel.getChannel(channelName);
		if (ch != null && ch instanceof BaseChannel) {
			((BaseChannel) ch).deleteObserver(this);
		}
		log.info("deleted channelobserver from the observer list");

		if(this.signon!=null)
			this.signon.stop();
		log.info("stopped the signon task");
		if (executorService != null)
			executorService.shutdownNow();
		log.info("stopped the signon executorservice");

		StatusRegistrar.setSignonStatus(muxName, NMStatus.Illegal);
//		StatusRegistrar.setKeyExchangeStatus(muxName, NMStatus.Illegal);
		log.info("did set signon status to Illegal of mux=" + muxName + " from Registrar");

	}

	@Override
	public void update(Observable o, Object arg) {

		if (o instanceof BaseChannel && arg == null) {
			updateChannel((BaseChannel) o);
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
	
	public static void main(String[] args){
		
		ExecutorService serv = Executors.newFixedThreadPool(3);
		MyRun run = new MyRun();
		
		for(int i=0;i<10;i++){
			serv.submit(run);
		}
		
		serv.shutdown();
		
	}
	
	static class MyRun implements Runnable{

		static long id;
		
		synchronized static long getID(){
			return ++id;
		}
		
		@Override
        public void run() {
	        System.out.println("asdfsfdfsd : " + getID());	        
	        
	        try {
	            TimeUnit.SECONDS.sleep(3);
            }
            catch (InterruptedException ex) {
	            ex.printStackTrace();
            }
	        
        }
		
	}

}
