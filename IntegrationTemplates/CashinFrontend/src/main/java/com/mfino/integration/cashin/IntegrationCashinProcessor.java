package com.mfino.integration.cashin;

import java.util.Map;
import java.util.UUID;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.integration.xml.RequestResponseTransformation;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.service.TransactionIdentifierService;
import com.mfino.service.impl.TransactionIdentifierServiceImpl;

public class IntegrationCashinProcessor implements Processor {

	Logger	       log	= LoggerFactory.getLogger(IntegrationCashinProcessor.class);

	private String	FrontendID;
	private Integer timeout;

	private RequestResponseTransformation	xsltTransformation;
	private TransactionIdentifierService transactionIdentifierService ;
	public TransactionIdentifierService getTransactionIdentifierService() {
		return transactionIdentifierService;
	}


	public void setTransactionIdentifierService(
			TransactionIdentifierService transactionIdentifierService) {
		this.transactionIdentifierService = transactionIdentifierService;
	}

	private String	cashinInQueue;
	private String	fronetendOutQ;

	public void setFrontendID(String FrontendID) {
		this.FrontendID = FrontendID;
	}


	public void setXSLTTransformation(RequestResponseTransformation transformation) {
		this.xsltTransformation = transformation;
	}

	public RequestResponseTransformation getTransformation() {
		return xsltTransformation;
	}


	public void setCashinInQueue(String cashinInQueue) {
		this.cashinInQueue = cashinInQueue;
	}


	public void setFronetendOutQ(String fronetendOutQ) {
		this.fronetendOutQ = fronetendOutQ;
	}

	private static final String	EXCHANGE_HEADER_SYNCHRONOUS_REQUEST_ID	= "synchronous_request_id";

	@Override
	public void process(Exchange httpExchange) throws Exception {
		String request = httpExchange.getIn().getBody(String.class);
		log.info("xml request received-->");

		boolean isGetDetailsRequest=false;
		
		final String requestID = UUID.randomUUID().toString();

		Map<String, Object> headers = httpExchange.getIn().getHeaders();
		log.info("synchronous request id " + requestID);
		headers.put(EXCHANGE_HEADER_SYNCHRONOUS_REQUEST_ID, requestID);
		headers.put("FrontendID", FrontendID);
		headers.put("frontendOutQ", this.fronetendOutQ);

		String er, eresponse=null ;

		CamelContext context = httpExchange.getContext();
		String transformedRequest = null;
		try {
			//getting the sourceMDN from the request to create the transactionIdentifier at start
			String mdnTag = getMdnTag(request);
			String uniqueIdMDN = getCustomerMDN(request,mdnTag);

			String trxnIdentifier = transactionIdentifierService.generateTransactionIdentifier(uniqueIdMDN);
			MCEUtil.setBreadCrumbId(headers, trxnIdentifier);
			MDC.put(MCEUtil.BREADCRUMB_ID,trxnIdentifier);
			log.info("Transaction Identifier created in IntegrationCashinProcessor with ID -->"+trxnIdentifier);
			transformedRequest = xsltTransformation.requestTransform(request);
			isGetDetailsRequest=transformedRequest.matches("(?i).*<CustomerInformationRequest>.*") || transformedRequest.matches("(?i).*<GetDetailsRequest>.*");
			if(isGetDetailsRequest == true){
				transformedRequest = xsltTransformation.getDetailsRequestTransform(transformedRequest);
				String institutionId = getInstitutionId(transformedRequest);
				String mdn = getMdn(transformedRequest);
				er = "<GetDetailsResponse><InstitutionId>" + institutionId +"</InstitutionId><Mdn>"+ mdn +"</Mdn><ResponseCode>106"
		        + "</ResponseCode></GetDetailsResponse>";
				eresponse = xsltTransformation.responseTransform(er);
				eresponse = xsltTransformation.getDetailsResponseTransform(eresponse);
			}
			else{
				String logID = getPaymentLogId(request);
				er = "<CashInResponse><PaymentLogId>" + logID + "</PaymentLogId><ResponseCode>106"
				        + "</ResponseCode></CashInResponse>";
				eresponse = xsltTransformation.responseTransform(er);
			}
			
			log.info("xml request after request transformation --> " + transformedRequest);
		}
		catch (Exception ex) {
			sendHTTP200Code(httpExchange,eresponse);
			return;
		}

		ProducerTemplate producerTemplate = context.createProducerTemplate();
		producerTemplate.start();
		producerTemplate.sendBodyAndHeaders(cashinInQueue, transformedRequest, headers);
		producerTemplate.stop();

		ConsumerTemplate consumerTemplate = context.createConsumerTemplate();
		consumerTemplate.start();
		Exchange resultFromQueuingSystem = consumerTemplate.receive("seda:" + requestID, timeout);
		consumerTemplate.stop();
		log.info("result from queing system " + resultFromQueuingSystem);

		if (resultFromQueuingSystem == null) {
			sendHTTP200Code(httpExchange, eresponse);
			return;
		}

		String response = resultFromQueuingSystem.getIn().getBody(String.class);

		log.info("got response from backend-->" + response);
		String transformedResponse =null;
		if (response != null) {
			transformedResponse = xsltTransformation.responseTransform(response);
			if(isGetDetailsRequest == true){
				transformedResponse = xsltTransformation.getDetailsResponseTransform(transformedResponse);
			}
			log.info("response after reponse transformation --> " + transformedResponse);

			httpExchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			httpExchange.getOut().setBody(transformedResponse);
		}
		else {
			log.info("Received null response from backend");
			sendHTTP200Code(httpExchange,eresponse);
		}
		MDC.remove(MCEUtil.BREADCRUMB_ID);
	}

	private String getMdn(String xml) {
		try {
			int start = xml.indexOf("<Mdn>");
			int end = xml.indexOf("</Mdn>");
			return xml.substring(start + 5, end);
		}
		catch (Exception ex) {
			return "-1";
		}
	}
	
	/**
	 * extracts the source mdn from the request and returns it.Incase of exception returns -1
	 * @param request
	 * @param mdnString
	 * @return
	 */
	private String getCustomerMDN(String request,String mdnString) {
		try {
			int start = request.indexOf("<"+mdnString);
			int end = request.indexOf("</"+mdnString);
			return request.substring(start + mdnString.length()+2, end);
		}
		catch (Exception ex) {
			log.error("could not extract mdn from request for transactionIdentifier");
			return "-1";
		}
	}
	
	/**
	 * returns the integration related tag that contains the sourceMDN
	 * @param request
	 * @return
	 */
	private String getMdnTag(String request) {
		String mdnTag = null;
		if(request.contains("<CustReference>")){
			mdnTag = "CustReference";
		}
		else if(request.contains("<TargetMdn>")){
			mdnTag = "TargetMdn";
		}
		else if(request.contains("<Mdn>")){
			mdnTag = "Mdn";
		}
		return mdnTag;
	}

	private void sendHTTP200Code(Exchange httpExchange,String response) {

		httpExchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
		httpExchange.getOut().setBody(response);
	}

	public String getPaymentLogId(String xml) {
		try {
			int start = xml.indexOf("<PaymentLogId>");
			int end = xml.indexOf("</PaymentLogId>");
			return xml.substring(start + 14, end);
		}
		catch (Exception ex) {
			return "-1";
		}
	}
	
	public String getInstitutionId(String xml) {
		try {
			int start = xml.indexOf("<InstitutionId>");
			int end = xml.indexOf("</InstitutionId>");
			return xml.substring(start + 15, end);
		}
		catch (Exception ex) {
			return "-1";
		}
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

}
