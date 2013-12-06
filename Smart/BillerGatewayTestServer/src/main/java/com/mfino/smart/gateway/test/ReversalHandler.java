package com.mfino.smart.gateway.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReversalHandler implements Handler {

	private static Logger	       log	                  = LoggerFactory.getLogger(ReversalHandler.class);

	private String	responseCode;

	private Long	sleepDurationInMillis	= 3000l;

	public void setSleepDurationInMillis(Long sleepDuration) {
		this.sleepDurationInMillis = sleepDuration;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String handle(String body) {

		log.info("received a reversal request -->" + body);

		String content = "</BillerCategory><ResponseCode>" + responseCode + "</ResponseCode><Remark>Success</Remark>";
		String response = body.replace("</BillerCategory>", content);

		content = "</OrigTrxTime><ReferenceID>345124</ReferenceID>";
		response = response.replace("</OrigTrxTime>", content);

		try {
			Thread.sleep(sleepDurationInMillis);
		}
		catch (InterruptedException ex) {
		}

		log.info("sending response -->" + response);
		return response;
	}

}