package com.mfino.zenith.vtu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.cxf.CxfPayload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.binding.soap.SoapHeader;

import com.ucitech.neptune.Item;
import com.ucitech.neptune.Order;
import com.ucitech.neptune.ProxyResponse;

public class ClientWSCommunicator implements Processor {

	public String	requestMsg;

	public String getRequestMsg() {
		return requestMsg;
	}

	public void setRequestMsg(String requestMsg) {
		this.requestMsg = requestMsg;
	}

	protected Log	log	= LogFactory.getLog(this.getClass());
	String	      webServiceEndpointBean;

	public String getWebServiceEndpointBean() {
		return webServiceEndpointBean;
	}

	public void setWebServiceEndpointBean(String webServiceEndpointBean) {
		this.webServiceEndpointBean = webServiceEndpointBean;
	}

	public void process(Exchange exchange) throws Exception {
		this.requestMsg = exchange.getIn().getBody(String.class);

		List<Object> responseFromWS = queryMerchants(exchange);

		ProxyResponse pr = (ProxyResponse)responseFromWS.get(0);
		
		int i = 0;
		i = i + 1;

		exchange.getIn().setBody(responseFromWS.toString());
		// Map<String, Object> headers = exchange.getIn().getHeaders();
		// exchange.getOut().setHeaders(headers);
		// exchange.getOut().setBody(responseFromWS.get(0));

	}

	@SuppressWarnings("unchecked")
	private List<Object> queryMerchants(Exchange exchange) {
		Order order = new Order();
		order.setAdditionalInfo("345676");
		order.setCustomerId("434");
		order.setId(2323l);
		Item item = new Item();
		item.setProductId(1232);
		item.setQuantity(32);

		List<Object> params = new ArrayList<Object>();
		params.add("ucitech");
		params.add("visafone");
		params.add(order);

		List<Object> responseFromWS = null;
		try {
			ProducerTemplate template = exchange.getContext().createProducerTemplate();
			template.start();
			responseFromWS = (List<Object>)template  
			        .requestBodyAndHeader("cxf:bean:" + webServiceEndpointBean, params, "operationName", "addCustomerProductByMerchant");
			template.stop();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return responseFromWS;
	}

}