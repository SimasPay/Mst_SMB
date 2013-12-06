package com.mfino.mce.iso.jpos.unused.definedtasks;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.util.NameRegistrar.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.hibernate.Timestamp;
import com.mfino.mce.iso.jpos.nm.NMStatus;
import com.mfino.mce.iso.jpos.nm.StatusRegistrar;
import com.mfino.mce.iso.jpos.unused.task.AbstractTask;
import com.mfino.mce.iso.jpos.unused.task.NetworkTaskResult;
import com.mfino.mce.iso.jpos.unused.task.NetworkTaskStatus;
import com.mfino.mce.iso.jpos.util.ISOUtil;

public class EchoTask extends AbstractTask {

	private static Logger	log	= LoggerFactory.getLogger(EchoTask.class);

	public EchoTask(String muxName, long timeout, String channelName, ISOUtil isoutil) {

		this.muxName = muxName;
		this.timeout = timeout;
		this.isoUtil = isoutil;

	}

	private String	muxName;
	private long	timeout	= 30000;
	private ISOUtil	isoUtil;

	@Override
	public NetworkTaskResult run() {

		this.taskExecutionStatus = NetworkTaskStatus.Running;

		NMStatus signonStatus = StatusRegistrar.getSignonStatus(muxName);

		if (!signonStatus.equals(NMStatus.Successful)) {
			log.info("signon was not done for mux=" + muxName + ".So not sending Echo=" + this);
			this.taskExecutionStatus = NetworkTaskStatus.Completed;
			return NetworkTaskResult.Failed;
		}

		NMStatus echoStatus = NMStatus.Illegal;
		try {

			ISOMsg signOn = isoUtil.getEchoMessage();
			this.startedAt = new Timestamp();
			ISOMsg replyMsg = isoUtil.sendAndReceive(signOn, timeout, muxName);
			this.endedAt = new Timestamp();
			if (replyMsg != null && replyMsg.getString(39).equals("00")) {
				log.info("Got reply for echo=" + this + " and it's successful mux=" + muxName);
				echoStatus = NMStatus.Successful;
			}
			else {
				echoStatus = NMStatus.Failed;
				log.info("No reply for echo=" + this + " for muxname=" + muxName);
			}
		}
		catch (NotFoundException e) {
			log.error(muxName + " Could not find the Name for sending echo=" + this, e);
			echoStatus = NMStatus.Failed;
		}
		catch (ISOException e) {
			log.error(muxName + ", Exception creating the iso message for echo=" + this, e);
			echoStatus = NMStatus.Failed;
		}

		StatusRegistrar.setEchoStatus(muxName, echoStatus);

		this.taskExecutionStatus = NetworkTaskStatus.Completed;

		if (echoStatus.equals(NMStatus.Successful))
			return NetworkTaskResult.Successful;
		else
			return NetworkTaskResult.Failed;

	}

}