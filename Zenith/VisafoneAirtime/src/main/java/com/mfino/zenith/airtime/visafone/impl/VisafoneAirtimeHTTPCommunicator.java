package com.mfino.zenith.airtime.visafone.impl;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.service.impl.SubscriberServiceImpl;
import com.mfino.zenith.airtime.visafone.VisafoneAirtimeConfiguration;

/**
 * @author Sasi
 *
 */
public class VisafoneAirtimeHTTPCommunicator implements Processor{
	
	private VisafoneAirtimeConfiguration visafoneAirtimeConfiguration;
	
	Log log = LogFactory.getLog(this.getClass());
	
	@Override
	public void process(Exchange httpExchange) throws Exception {
		log.debug("VisafoneAirtimeWSCommunicator :: process() BEGIN");
		MCEMessage mceMessage = httpExchange.getIn().getBody(MCEMessage.class);
		VisafoneAirtimeBackendResponse airtimePurchaseRequest = (VisafoneAirtimeBackendResponse)mceMessage.getResponse();
		
		String queryString = getQueryString(airtimePurchaseRequest);
		String url = visafoneAirtimeConfiguration.getUrl() + "?httpClient.soTimeout=30000";
		
		CamelContext camelContext = httpExchange.getContext();
		
		log.debug("VisafoneAirtimeHTTPCommunicator :: process() url="+url+", Exchange.HTTP_QUERY="+queryString);
		
		httpExchange.getIn().setHeader(Exchange.HTTP_QUERY, queryString);
		
		Object obj = null;
		
		try{
			ProducerTemplate template = camelContext.createProducerTemplate();
			template.start();
			obj = template.sendBodyAndHeader(url, ExchangePattern.InOut,"",Exchange.HTTP_QUERY,getQueryString(airtimePurchaseRequest) );
			template.stop();
		}
		catch (Exception e) {
			log.debug("VisafoneAirtimeHTTPCommunicator catch block, Error communicating with VisafoneAirtime WebService", e);
			obj = MCEUtil.SERVICE_UNAVAILABLE;
		}
		
		log.debug("VisafoneAirtimeHTTPCommunicator DEBUG obj="+obj+", obj.getclass="+obj.getClass());
		
		String response = (String)obj;
		
		log.debug("Response from web service = "+response);
		
		if((null != response) && !("".equals(response))){
			MCEMessage replyMessage = constructReplyMessage(response.trim(), mceMessage);
			httpExchange.getIn().setBody(replyMessage);
			Map<String,Object> headers = httpExchange.getIn().getHeaders();
			//exchange.setOut(exchange.getIn());
			httpExchange.getOut().setHeaders(headers);
			httpExchange.getOut().setBody(replyMessage);
		}
		
		log.debug("VisafoneAirtimeWSCommunicator :: process() END");
	}

	public VisafoneAirtimeConfiguration getConfiguration() {
		return visafoneAirtimeConfiguration;
	}

	public void setConfiguration(VisafoneAirtimeConfiguration configuration) {
		this.visafoneAirtimeConfiguration = configuration;
	}
	
	public String getQueryString(VisafoneAirtimeBackendResponse airtimePurchaseRequest){
		String queryString = "";
		
		log.debug("VisafoneAirtimeHTTPCommunicator :: airtimePurchaseRequest.getRechargeMdn()="
				+ airtimePurchaseRequest.getRechargeMdn()
				+ ", airtimePurchaseRequest.getAmount()="
				+ airtimePurchaseRequest.getAmount()
				+ airtimePurchaseRequest.getINTxnId()
				+ airtimePurchaseRequest.getRechargeMdn()
				);
		
		String rechargeMdn = airtimePurchaseRequest.getRechargeMdn();
		SubscriberServiceImpl subscriberServiceImpl = new SubscriberServiceImpl();
		rechargeMdn = subscriberServiceImpl.deNormalizeMDN(rechargeMdn);
		
		visafoneAirtimeConfiguration.setParameter(VisafoneAirtimeConfiguration.KEY_MSISDN, rechargeMdn);
		visafoneAirtimeConfiguration.setParameter(VisafoneAirtimeConfiguration.KEY_AMOUNT, airtimePurchaseRequest.getAmount().toBigInteger().toString());
		visafoneAirtimeConfiguration.setParameter(VisafoneAirtimeConfiguration.KEY_TRANSACTION_ID, airtimePurchaseRequest.getINTxnId());
		
		queryString = visafoneAirtimeConfiguration.getQueryString();
		
		return queryString;
	}
	
	public MCEMessage constructReplyMessage(String response, MCEMessage requestMceMessage) 
	{
		VisafoneAirtimeResponseCodes responseCode = VisafoneAirtimeResponseCodes.getResponseCode(Integer.valueOf(response));

		MCEMessage  mceResponse =  new MCEMessage();
		mceResponse.setRequest(requestMceMessage.getRequest());
		VisafoneAirtimeBackendResponse inResponse = (VisafoneAirtimeBackendResponse)requestMceMessage.getResponse();
		inResponse.setProcessed(true);
		inResponse.setWebServiceResponse(response);
		inResponse.setResponseCode(responseCode);
		mceResponse.setResponse(inResponse);
		return mceResponse;
	}
}
