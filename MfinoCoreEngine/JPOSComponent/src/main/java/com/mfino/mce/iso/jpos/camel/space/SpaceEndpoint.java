package com.mfino.mce.iso.jpos.camel.space;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.jpos.space.Space;

/**
 * Space endpoint
 * @author POCHADRI
 *
 */
public class SpaceEndpoint extends DefaultEndpoint
{
    private Space space;
    private long timeout=-1;
    private String key;
    public String getSend() {
		return send;
	}

	public void setSend(String send) {
		this.send = send;
	}

	public String getReceive() {
		return receive;
	}

	public void setReceive(String receive) {
		this.receive = receive;
	}

	private String send;
    private String receive;

    public SpaceEndpoint(Space space,String uri, SpaceComponent component)
    {
        super(uri, component);
        this.space=space;
    }

    public Space getSpace()
    {
        return space;
    }

    public long getTimeout()
    {
        return timeout;
    }

    public void setTimeout(long timeout)
    {
        this.timeout = timeout;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    @Override
    public Producer createProducer() throws Exception
    {
        return new SpaceProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception
    {
        return new SpaceConsumer(this,processor);
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }
}
