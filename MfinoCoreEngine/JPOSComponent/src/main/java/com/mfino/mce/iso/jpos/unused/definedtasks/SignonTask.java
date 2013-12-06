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

public class SignonTask extends AbstractTask {

	private static Logger	log	= LoggerFactory.getLogger(SignonTask.class);

	public SignonTask(String muxName, long timeout, String channelName, ISOUtil isoutil) {

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

		NMStatus status = StatusRegistrar.getSignonStatus(muxName);
		try {
			ISOMsg signOn = isoUtil.getSignOnMessage();
			this.startedAt = new Timestamp();
			ISOMsg replyMsg = isoUtil.sendAndReceive(signOn, timeout, muxName);
			this.endedAt = new Timestamp();
			if (replyMsg != null && replyMsg.getString(39).equals("00")) {
				log.info("Got reply for sign on and it's successful mux=" + muxName);
				status = NMStatus.Successful;
			}
			else {
				status = NMStatus.Failed;
				log.info("No reply for signon for muxname=" + muxName);
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

		this.taskExecutionStatus = NetworkTaskStatus.Completed;

		if (status.equals(NMStatus.Successful))
			return NetworkTaskResult.Successful;
		else
			return NetworkTaskResult.Failed;

	}

}