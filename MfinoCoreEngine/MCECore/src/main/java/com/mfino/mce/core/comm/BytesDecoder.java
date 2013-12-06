package com.mfino.mce.core.comm;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

public class BytesDecoder extends OneToOneDecoder 
{
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception 
	{
		if (!(msg instanceof ChannelBuffer)) 
	    {
	       return msg;
	    }
	    return ((ChannelBuffer) msg).array();
	}
}
