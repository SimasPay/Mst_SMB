package com.mfino.mce.component.iso.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;


/**
 * OneToOneEncoder implementation that converts an Envelope instance into a ChannelBuffer.
 *
 * Since the encoder is stateless, a single instance can be shared among all pipelines, hence the @Sharable annotation
 * and the singleton instantiation.
 */
@ChannelHandler.Sharable
public class Encoder extends OneToOneEncoder {
	Log	log	= LogFactory.getLog(Encoder.class);
    // constructors ---------------------------------------------------------------------------------------------------

    private Encoder() {
    }

    // public static methods ------------------------------------------------------------------------------------------

    public static Encoder getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public static ChannelBuffer encodeMessage(Envelope message) throws IllegalArgumentException {
        // you can move these verifications "upper" (before writing to the channel) in order not to cause a
        // channel shutdown.

        if ((message.getPayload() == null) || (message.getPayload().length == 0)) {
            throw new IllegalArgumentException("Message payload cannot be null or empty");
        }

        // version(1b) + type(1b) + payload length(4b) + payload(nb)
        //send the message that we got here.
        int size = message.getPayload().length;

        ChannelBuffer buffer = ChannelBuffers.buffer(size);
        //TODO: write the ISO length calculation logic here
        //buffer.writeInt(message.getPayload().length);
        buffer.writeBytes(message.getPayload());

        return buffer;
    }

    // OneToOneEncoder ------------------------------------------------------------------------------------------------

    @Override
    protected Object encode(ChannelHandlerContext channelHandlerContext, Channel channel, Object msg) throws Exception 
    {
    	log.info("ENCODER Encoding Message: '"+((Envelope)msg).getPayload()+"'");
        if (msg instanceof Envelope) {
            return encodeMessage((Envelope) msg);
        } else {
            return msg;
        }
    }

    // private classes ------------------------------------------------------------------------------------------------

    private static final class InstanceHolder {
        private static final Encoder INSTANCE = new Encoder();
    }
}
