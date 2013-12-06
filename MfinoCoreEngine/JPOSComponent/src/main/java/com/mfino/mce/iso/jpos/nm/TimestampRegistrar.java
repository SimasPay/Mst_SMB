package com.mfino.mce.iso.jpos.nm;

import java.util.concurrent.ConcurrentHashMap;
import com.mfino.hibernate.Timestamp;

public class TimestampRegistrar {

	//set methods have no access modifier by design
	
	private static ConcurrentHashMap<MessageType, TimestampRegister>	registerMap	= new ConcurrentHashMap<MessageType, TimestampRegister>();

	static {
		for (MessageType type : MessageType.values()) {
			registerMap.put(type, new TimestampRegister(type));
		}
	}

	public static synchronized Timestamp getLastTriedTime(String muxName, MessageType msgType) {
		return registerMap.get(msgType).getStatus(muxName);
	}

	static synchronized void setnewTime(String muxName, MessageType msgType, Timestamp time) {
		registerMap.get(msgType).setStatus(muxName, time);
	}

	public static synchronized Timestamp getLastEchoTime(String muxName) {
		return getLastTriedTime(muxName, MessageType.Echo);
	}

	static synchronized void setNewEchoTime(String muxName, Timestamp time) {
		setnewTime(muxName, MessageType.Echo, time);
	}

	public static synchronized Timestamp getLastSignonTime(String muxName) {
		return getLastTriedTime(muxName, MessageType.Signon);
	}

	static synchronized void setNewSignonTime(String muxName, Timestamp time) {
		setnewTime(muxName, MessageType.Signon, time);
	}

	public static synchronized Timestamp getLastKeyExchangeTime(String muxName) {
		return getLastTriedTime(muxName, MessageType.KeyExchange);
	}

	static synchronized void setNewKeyExchangeTime(String muxName, Timestamp time) {
		setnewTime(muxName, MessageType.KeyExchange, time);
	}

}