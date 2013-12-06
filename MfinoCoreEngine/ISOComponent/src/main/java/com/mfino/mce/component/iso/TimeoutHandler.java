package com.mfino.mce.component.iso;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.util.Timer;

public class TimeoutHandler extends ReadTimeoutHandler {
	
	public TimeoutHandler(Timer timer, int timeoutSeconds) 
	{
		super(timer, timeoutSeconds);
	}
	
	@Override
	public void readTimedOut(ChannelHandlerContext ctx)
	{
		// when there are no messages send a echo message and wait for that reply
	}

}
