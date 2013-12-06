package com.mfino.tools.adjustments;

import org.apache.camel.Body;
import org.apache.camel.Handler;
import org.apache.camel.Header;

import com.mfino.mce.core.MCEMessage;

public class PropertySetterBean {

	@Handler
	public MCEMessage setDestQueue(@Header("destinationQueue") String queue,@Body MCEMessage msg){
		msg.setDestinationQueue(queue);
		return msg;
		
	}
	
}
