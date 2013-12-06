package com.mfino.mce.core.ws;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.MCEUtil;

/**
 * 
 * @author POCHADRI
 *
 */
public abstract class WSCommunicator implements Processor 
{
	protected Log log = LogFactory.getLog(this.getClass());
	String webServiceEndpointBean;
	
	public String getWebServiceEndpointBean() {
		return webServiceEndpointBean;
	}

	public void setWebServiceEndpointBean(String webServiceEndpointBean) {
		this.webServiceEndpointBean = webServiceEndpointBean;
	}

	@SuppressWarnings("unchecked")
 	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void process(Exchange exchange) throws Exception 
	{
		MCEMessage mceMessage = exchange.getIn().getBody(MCEMessage.class);
		String messageName = getMessageName(mceMessage);
		List<Object> params = getParameterList(mceMessage);		
		
		
		List<Object> responseFromWS = null;
		try
		{	
			ProducerTemplate template = exchange.getContext().createProducerTemplate();
			template.start();
			responseFromWS = (List<Object>)template
							.requestBodyAndHeader("cxf:bean:"+webServiceEndpointBean,params,
									"operationName",messageName);
			template.stop();
		}
		catch(Exception e)
		{
			//any exception during call to web service need to be catched and it needs to be treated as failure
			log.warn("Exception during call to web service",e);
			responseFromWS = handleWSCommunicationException(e);
		}
		
		MCEMessage replyMessage = constructReplyMessage(responseFromWS, mceMessage);
		exchange.getIn().setBody(replyMessage);
		Map<String,Object> headers = exchange.getIn().getHeaders();
		//exchange.setOut(exchange.getIn());
		exchange.getOut().setHeaders(headers);
		exchange.getOut().setBody(replyMessage);
		//String xml = processMessage(mceMessage);
	}
	
	public abstract List<Object> getParameterList(MCEMessage mceMessage);
	
	public abstract MCEMessage constructReplyMessage(List<Object> response, MCEMessage requestMceMessage); 
	
	public abstract String getMessageName(MCEMessage mceMessage);

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public List<Object> handleWSCommunicationException(Exception e){
		List<Object> responseFromWS = new ArrayList<Object>();
		
		responseFromWS.add(MCEUtil.SERVICE_UNAVAILABLE);
		
/*		override this method to handle specific exceptions.
 * 		if(e.getCause() instanceof SocketTimeoutException){
			responseFromWS.add(MCEUtil.SERVICE_TIME_OUT);
		}
		else{
			responseFromWS.add(MCEUtil.SERVICE_UNAVAILABLE);
		}*/
		
		return responseFromWS;
	}
}
