package com.mfino.mce.iso.jpos.unused.definedtasks;

import com.mfino.hibernate.Timestamp;
import com.mfino.mce.iso.jpos.nm.NMStatus;
import com.mfino.mce.iso.jpos.nm.StatusRegistrar;
import com.mfino.mce.iso.jpos.unused.task.AbstractTask;
import com.mfino.mce.iso.jpos.unused.task.NetworkTaskResult;
import com.mfino.mce.iso.jpos.unused.task.NetworkTaskStatus;

public class DisconnectedTask extends AbstractTask {

	public DisconnectedTask(String muxName) {
		this.muxName = muxName;

		this.taskExecutionStatus = NetworkTaskStatus.Ready;
	}

	private String	muxName;

	@Override
	public NetworkTaskResult run() {

		this.taskExecutionStatus = NetworkTaskStatus.Running;

		this.startedAt = new Timestamp();

		StatusRegistrar.setSignonStatus(this.muxName, NMStatus.Illegal);

		this.endedAt = new Timestamp();

		this.taskExecutionStatus = NetworkTaskStatus.Completed;

		return NetworkTaskResult.Successful;

	}

}
