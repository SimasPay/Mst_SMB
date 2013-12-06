package com.mfino.mce.component.iso;

import com.mfino.mce.component.iso.common.Decoder;
import com.mfino.mce.component.iso.common.Encoder;
import com.mfino.mce.component.iso.common.Envelope;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;


public abstract class ISOClient implements ClientHandlerListener
{
	Log	log	= LogFactory.getLog(ClientHandlerListener.class);
    // configuration 

    private final String host;
    private final int port;
    private final int timeout;
    private int timeBeforeReconnect;

    // internal vars 
    
    private ChannelFactory clientFactory;
    private ChannelGroup channelGroup;
    private ClientHandler handler;
    private long startTime;
	
    // constructors 

    public ISOClient(String host, int port, int timeout, int timeBeforeReconnect) 
    {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        this.timeBeforeReconnect = timeBeforeReconnect;
    }

    // public methods -------------------------------------------------------------------------------------------------

    public boolean start() {

        // For production scenarios, use limited sized thread pools
        this.clientFactory = new NioClientSocketChannelFactory(Executors.newSingleThreadExecutor(),
                Executors.newFixedThreadPool(10));
        this.channelGroup = new DefaultChannelGroup(this + "-channelGroup");
        this.handler = new ClientHandler(this, this.channelGroup);
        final ClientBootstrap bootstrap = new ClientBootstrap(this.clientFactory);
        bootstrap.setOption("reuseAddress", true);
        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("keepAlive", true);
        bootstrap.setOption("remoteAddress", host);
        ChannelPipelineFactory pipelineFactory = new ChannelPipelineFactory() {
        	final Timer timer = new HashedWheelTimer();
			
			public ChannelPipeline getPipeline() throws Exception {
	                ChannelPipeline pipeline = Channels.pipeline();
	                //pipeline.addLast("byteCounter", new ByteCounter("clientByteCounter"));
	                //pipeline.addLast("timer",new ReadTimeoutHandler(timer, timeout));
	                //pipeline.addLast("uptime", new  UptimeClientHandler(bootstrap, timer, timeBeforeReconnect));
	                pipeline.addLast("encoder", Encoder.getInstance());
	                pipeline.addLast("decoder", new Decoder());
	                pipeline.addLast("handler", handler);
	                return pipeline;
	            }
			};
        
        bootstrap.setPipelineFactory(pipelineFactory);

        
        boolean connected = bootstrap.connect(new InetSocketAddress(host, port)).awaitUninterruptibly().isSuccess();
        if (!connected) {
            this.stop();
        }	
        return connected;
    }

    public void stop() {
        if (this.channelGroup != null) {
            this.channelGroup.close();
        }
        if (this.clientFactory != null) {
            this.clientFactory.releaseExternalResources();
        }
    }    
    
    public void sendMessage(Envelope message) {
        if ((this.channelGroup == null) || (this.clientFactory == null)) {
            return;
        }

        this.startTime = System.currentTimeMillis();
        this.handler.sendMessage(message);
    }

    // main -----------------------------------------------------------------------------------------------------------

   /* public static void main(String[] args) throws InterruptedException {
        final Client client = new Client("localhost", 9999, 100000, 1);

        if (!client.start()) {

            System.exit(-1);
            return; // not really needed...
        }

        System.out.println("Client started...");

        client.flood();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                client.stop();
            }
        });
    }*/

	

	/**
     * Got the message send it to the some queue for further processing
     */
	public abstract void messageReceived(Envelope message);
	/*{
		//send the message to the designated queue
	    log.info("Received message in ISO Client" + new String(message.getPayload()));
	    try {
			
			Exchange e = producer.createExchange();
			e.getOut().setBody(message.getPayload());
			producer.process(e);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Not able to send the ISO message to designated queue", e);
		}
	    //Exchange exchange = producerTemplate.createExchange();
	    //exchange.getOut().setBody(message.getPayload());
	    //producerTemplate.sendBody(message.getPayload());
	}*/
}
	
