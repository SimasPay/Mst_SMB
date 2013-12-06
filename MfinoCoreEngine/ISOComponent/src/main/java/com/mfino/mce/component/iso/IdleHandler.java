package com.mfino.mce.component.iso;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;

public class IdleHandler extends IdleStateAwareChannelHandler  
{
	Log	log	= LogFactory.getLog(IdleHandler.class);
	public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) {
		//write an echo message if there was no communication on the channel
        //ctx.getChannel().write(new PingMessage());
		log.info("no message for timeout seconds");
    }
}
