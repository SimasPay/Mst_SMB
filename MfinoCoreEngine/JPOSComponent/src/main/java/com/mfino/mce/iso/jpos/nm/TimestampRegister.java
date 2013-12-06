package com.mfino.mce.iso.jpos.nm;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.mfino.hibernate.Timestamp;

class TimestampRegister {

	private MessageType	msgType;

	public MessageType getType() {
		return msgType;
	}

	public TimestampRegister(MessageType type) {
		msgType = type;
	}

	public synchronized Timestamp getStatus(String muxName) {
		Timestamp time = null;
		synchronized (timeHolderRegister) {
			time = timeHolderRegister.get(muxName);
		}
		return time;
	}

	public synchronized void setStatus(String muxName, Timestamp time) {
		synchronized (timeHolderRegister) {
			timeHolderRegister.put(muxName, time);
		}
	}

	public synchronized Map<String, Timestamp> getRegister() {
		return timeHolderRegister;
	}

	private ConcurrentHashMap<String, Timestamp>	timeHolderRegister	= new ConcurrentHashMap<String, Timestamp>();

}