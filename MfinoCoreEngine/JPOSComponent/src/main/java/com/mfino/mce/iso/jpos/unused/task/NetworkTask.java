package com.mfino.mce.iso.jpos.unused.task;

import com.mfino.hibernate.Timestamp;

public interface NetworkTask {

	public NetworkTaskResult run();

	public NetworkTaskStatus getTaskStatus();

	public String getDescription();

	public long getID();
	public void setID(long id);
	
	public Timestamp getCreatedTime();

	public Timestamp getExecutionStartedAt();
	
	public Timestamp getExecutionEndedAt();

	public void setCreatedTime(Timestamp timestamp);
	
	public boolean isSimilarTo(NetworkTask task);
	
}
