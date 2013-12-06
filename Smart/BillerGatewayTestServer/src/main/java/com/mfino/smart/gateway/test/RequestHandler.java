package com.mfino.smart.gateway.test;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class RequestHandler implements Processor {

	private Handler	inquiryHandler;

	private Handler	confirmHandler;

	private Handler reversalHandler;
	
	public void setReversalHandler(Handler reversalHandler) {
		this.reversalHandler = reversalHandler;
	}

	public void setInquiryHandler(Handler inquiryHandler) {
		this.inquiryHandler = inquiryHandler;
	}

	public void setConfirmHandler(Handler confirmHandler) {
		this.confirmHandler = confirmHandler;
	}

	@Override
	public void process(Exchange exchange) throws Exception {

		String body = exchange.getIn().getBody(String.class);

		Handler proc = null;
		if (body.contains("<Inquiry>"))
			proc = inquiryHandler;
		if (body.contains("<Payment>"))
			proc = confirmHandler;
		if(body.contains("<Reversal>"))
			proc = reversalHandler;
		exchange.getOut().setBody(proc.handle(body));

	}

}
