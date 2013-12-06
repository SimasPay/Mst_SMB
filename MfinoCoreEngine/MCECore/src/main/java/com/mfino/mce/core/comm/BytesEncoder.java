package com.mfino.mce.core.comm;

import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public class BytesEncoder extends OneToOneEncoder 
{	
	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
							Object msg) throws Exception 
	{
        if (!(msg instanceof byte[])) {
            return msg;
        }
        return wrappedBuffer((byte[]) msg);
	}
}