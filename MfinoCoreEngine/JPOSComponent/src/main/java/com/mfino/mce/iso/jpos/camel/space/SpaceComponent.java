package com.mfino.mce.iso.jpos.camel.space;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.jpos.space.Space;
import org.jpos.space.SpaceFactory;

import java.util.Map;
/**
 * Camel component for encapsulating jpos space
 * @author POCHADRI
 *
 */
public class SpaceComponent extends DefaultComponent
{
    @SuppressWarnings({"UnusedDeclaration"})
    public SpaceComponent()
    {
    }

    public SpaceComponent(CamelContext context)
    {
        super(context);
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception
    {
        Space space= SpaceFactory.getSpace(remaining);
        SpaceEndpoint endpoint=new SpaceEndpoint(space,uri,this);
        setProperties(endpoint,parameters);
        return endpoint;
    }
}
