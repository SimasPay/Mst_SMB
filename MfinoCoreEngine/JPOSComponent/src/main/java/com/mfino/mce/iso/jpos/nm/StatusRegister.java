package com.mfino.mce.iso.jpos.nm;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class StatusRegister {

	private MessageType	msgType;

	public MessageType getType() {
		return msgType;
	}

	public StatusRegister(MessageType type) {
		msgType = type;
	}

	public synchronized NMStatus getStatus(String muxName) {
		NMStatus status = null;
		synchronized (statusHolderRegister) {
			status = statusHolderRegister.get(muxName);
		}
		if (status == null)
			return NMStatus.Illegal;
		return status;
	}

	public synchronized void setStatus(String muxName, NMStatus status) {
		synchronized (statusHolderRegister) {
			statusHolderRegister.put(muxName, status);
		}
	}

	public synchronized Map<String, NMStatus> getRegister() {
		return statusHolderRegister;
	}

	private ConcurrentHashMap<String, NMStatus>	statusHolderRegister	= new ConcurrentHashMap<String, NMStatus>();

}