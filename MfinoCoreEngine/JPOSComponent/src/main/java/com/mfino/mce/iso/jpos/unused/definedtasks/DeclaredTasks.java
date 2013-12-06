package com.mfino.mce.iso.jpos.unused.definedtasks;

import com.mfino.hibernate.Timestamp;
import com.mfino.mce.iso.jpos.unused.task.NetworkTask;
import com.mfino.mce.iso.jpos.unused.task.NetworkTaskResult;
import com.mfino.mce.iso.jpos.unused.task.NetworkTaskStatus;

public enum DeclaredTasks implements NetworkTask {
	EmptyTask;

	@Override
	public NetworkTaskResult run() {
		throw new UnsupportedOperationException("this task is not supported on EmptyTask");
	}

	@Override
	public NetworkTaskStatus getTaskStatus() {
		return NetworkTaskStatus.Illegal;
	}

	@Override
	public String getDescription() {
		return "EmptyTask";
	}

	@Override
	public long getID() {
		return Integer.MIN_VALUE;
	}

	@Override
	public Timestamp getCreatedTime() {
		throw new UnsupportedOperationException("this task is not supported on EmptyTask");
	}

	@Override
	public Timestamp getExecutionStartedAt() {
		throw new UnsupportedOperationException("this task is not supported on EmptyTask");
	}

	@Override
	public Timestamp getExecutionEndedAt() {
		throw new UnsupportedOperationException("this task is not supported on EmptyTask");
	}

	@Override
    public void setID(long id) {
		throw new UnsupportedOperationException("this task is not supported on EmptyTask");
    }

	@Override
    public void setCreatedTime(Timestamp timestamp) {
		throw new UnsupportedOperationException("this task is not supported on EmptyTask");
    }

	@Override
    public boolean isSimilarTo(NetworkTask task) {
		throw new UnsupportedOperationException("this task is not supported on EmptyTask");
    }

}
