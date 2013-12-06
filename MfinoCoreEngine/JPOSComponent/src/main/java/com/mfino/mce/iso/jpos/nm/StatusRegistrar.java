package com.mfino.mce.iso.jpos.nm;

import java.util.concurrent.ConcurrentHashMap;

import com.mfino.hibernate.Timestamp;

public class StatusRegistrar {

	private static ConcurrentHashMap<MessageType, StatusRegister>	registerMap	= new ConcurrentHashMap<MessageType, StatusRegister>();

	static {
		for (MessageType type : MessageType.values()) {
			registerMap.put(type, new StatusRegister(type));
		}
	}

	public static synchronized NMStatus getStatus(String muxName, MessageType msgType) {
		return registerMap.get(msgType).getStatus(muxName);
	}

	public static synchronized void setStatus(String muxName, MessageType msgType, NMStatus status) {
		registerMap.get(msgType).setStatus(muxName, status);
		TimestampRegistrar.setnewTime(muxName, msgType, new Timestamp());
	}

	public static synchronized NMStatus getEchoStatus(String muxName) {
		return getStatus(muxName, MessageType.Echo);
	}

	public static synchronized void setEchoStatus(String muxName, NMStatus status) {
		setStatus(muxName, MessageType.Echo, status);
	}

	public static synchronized NMStatus getSignonStatus(String muxName) {
		return getStatus(muxName, MessageType.Signon);
	}

	public static synchronized NMStatus getKeyExchangeStatus(String muxName) {
		return getStatus(muxName, MessageType.KeyExchange);
	}

	public static synchronized void setSignonStatus(String muxName, NMStatus status) {
		setStatus(muxName, MessageType.Signon, status);
	}

	public static synchronized void setKeyExchangeStatus(String muxName, NMStatus status) {
		setStatus(muxName, MessageType.KeyExchange, status);
	}

}