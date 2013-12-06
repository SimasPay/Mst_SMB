package com.mfino.smart.gateway.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InquiryHandler implements Handler {

	private static Logger	log	= LoggerFactory.getLogger(InquiryHandler.class);

	private String	      responseCode;

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	private Long	sleepDurationInMillis	= 3000l;

	public void setSleepDurationInMillis(Long sleepDuration) {
		this.sleepDurationInMillis = sleepDuration;
	}

	@Override
	public String handle(String body) {

		log.info("received a inquiry request -->" + body);

		String content = "</BillerCategory><ResponseCode>" + responseCode + "</ResponseCode><Remark>Success</Remark>";
		String response = body.replace("</BillerCategory>", content);

		content = "</TrxAmount><CustomerName>Karthik</CustomerName><BillInfo>blahblahblah</BillInfo>";
		response = response.replace("</TrxAmount>", content);

		try {
			Thread.sleep(sleepDurationInMillis);
		}
		catch (InterruptedException ex) {
		}

		log.info("sending response -->" + response);
		return response;

	}

}
