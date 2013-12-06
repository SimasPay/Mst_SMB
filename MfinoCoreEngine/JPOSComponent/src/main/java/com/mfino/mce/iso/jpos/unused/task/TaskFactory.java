package com.mfino.mce.iso.jpos.unused.task;

import com.mfino.hibernate.Timestamp;
import com.mfino.mce.iso.jpos.nm.MessageType;
import com.mfino.mce.iso.jpos.unused.definedtasks.DisconnectedTask;
import com.mfino.mce.iso.jpos.unused.definedtasks.EchoTask;
import com.mfino.mce.iso.jpos.unused.definedtasks.SignonTask;
import com.mfino.mce.iso.jpos.util.ISOUtil;

public class TaskFactory {

	private static TaskFactory	_taskFactory;

	public static synchronized TaskFactory getInstance() {

		if (_taskFactory == null)
			_taskFactory = new TaskFactory();

		return _taskFactory;

	}

	private long	taskId;

	private synchronized long getNextTaskId() {
		return ++taskId;
	}

	private synchronized Timestamp getTimestamp() {
		return new Timestamp();
	}

	public NetworkTask getTask(MessageType type, String muxName, long timeout, String channelName, ISOUtil isoutil) {

		NetworkTask task = null;

		if (type.equals(MessageType.Signon))
			task = new SignonTask(muxName, timeout, channelName, isoutil);
		else if (type.equals(MessageType.Signoff))
			task = new DisconnectedTask(muxName);
		else if (type.equals(MessageType.Echo))
			task = new EchoTask(muxName, timeout, channelName, isoutil);

		synchronized (this) {
			task.setID(getNextTaskId());
			task.setCreatedTime(getTimestamp());
		}

		return task;
	}

}
