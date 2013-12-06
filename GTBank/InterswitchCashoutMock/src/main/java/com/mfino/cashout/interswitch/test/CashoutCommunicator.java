package com.mfino.cashout.interswitch.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;

import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.MCEUtil;

public class CashoutCommunicator implements Processor{
	
	String wsEndpoint;
	
//	public String getWebServiceEndpointBean() {
//		return webServiceEndpointBean;
//	}
//
//	public void setWebServiceEndpointBean(String webServiceEndpointBean) {
//		this.webServiceEndpointBean = webServiceEndpointBean;
//	}

	public String getWsEndpoint() {
    	return wsEndpoint;
    }

	public void setWsEndpoint(String wsEndpoint) {
    	this.wsEndpoint = wsEndpoint;
    }

	@SuppressWarnings("unchecked")
	public void process(Exchange exchange) throws Exception 
	{
//		MCEMessage mceMessage = exchange.getIn().getBody(MCEMessage.class);
		
		
		String payload = exchange.getIn().getBody(String.class);
		
//		XMLInputFactory fact = XMLInputFactory.newFactory();
//		XMLStreamReader  reader = fact.createXMLStreamReader(new StringReader(payload));
		
//		XMLStreamReader reader = XMLStreamReader.this;
		
//		String messageName = getMessageName(mceMessage);
		
//		List<Source> elements = new ArrayList<Source>();
//		elements.add(new DOMSource(org.apache.cxf.helpers.DOMUtils.readXml(new StringReader(payload)).getDocumentElement()));
		
		List<Object> responseFromWS = null;
		
		List<Object> requestList = new ArrayList<Object>();
		requestList.add(payload);
		
		try
		{	
			ProducerTemplate template = exchange.getContext().createProducerTemplate();
			template.start();
			responseFromWS = (List<Object>)
							template.requestBodyAndHeader("cxf:bean:"+wsEndpoint,requestList,
									"operationName","Iso8583PostXml");
			template.stop();
		}
		catch(Exception e)
		{
			
			e.printStackTrace();
			//any exception during call to web service need to be catched and it needs to be treated as failure
//			log.warn("Exception during call to web service",e);
			responseFromWS = new ArrayList<Object>();
			responseFromWS.add(MCEUtil.SERVICE_UNAVAILABLE);
		}

		int i=0;
		i=i+99;
		
//		MCEMessage replyMessage = constructReplyMessage(responseFromWS, mceMessage);
//		exchange.getIn().setBody(replyMessage);
//		Map<String,Object> headers = exchange.getIn().getHeaders();
		//exchange.setOut(exchange.getIn());
//		exchange.getOut().setHeaders(headers);
//		exchange.getOut().setBody(replyMessage);
		//String xml = processMessage(mceMessage);
	}


//	String payload;
//    public void setPayload(String str) {
//	    this.payload = str;
//    }

    public MCEMessage constructReplyMessage(List<Object> response, MCEMessage requestMceMessage) {
	    // TODO Auto-generated method stub
	    return null;
    }

    public String getMessageName(MCEMessage mceMessage) {
	    // TODO Auto-generated method stub
	    return null;
    }


}
