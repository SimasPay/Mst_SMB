package com.mfino.mce.iso.jpos.unused.task;

import com.mfino.hibernate.Timestamp;

public abstract class AbstractTask implements NetworkTask {

	protected Timestamp	        createdAt;
	protected Timestamp	        startedAt;
	protected Timestamp	        endedAt;
	protected long	            taskID;
	protected NetworkTaskStatus	taskExecutionStatus;

	public AbstractTask() {
		this.taskExecutionStatus = NetworkTaskStatus.Ready;
	}

	@Override
	public NetworkTaskStatus getTaskStatus() {
		return this.taskExecutionStatus;
	}

	@Override
	public String getDescription() {
		return super.toString()+" Task {id=" + this.taskID + ";CreatedAt=" + this.createdAt+"}";
	}

	@Override
	public String toString() {
		return getDescription();
	}

	@Override
	public long getID() {
		return this.taskID;
	}

	@Override
	public Timestamp getCreatedTime() {
		return this.createdAt;
	}

	@Override
	public Timestamp getExecutionStartedAt() {
		return this.startedAt;
	}

	@Override
	public Timestamp getExecutionEndedAt() {
		return this.endedAt;
	}

	@Override
	public void setID(long id) {
		this.taskID = id;
	}

	@Override
	public void setCreatedTime(Timestamp timestamp) {
		this.createdAt = timestamp;
	}

	@Override
	public boolean isSimilarTo(NetworkTask task) {
		if (this.getClass().equals(task.getClass()))
			return true;
		return false;
	}

}