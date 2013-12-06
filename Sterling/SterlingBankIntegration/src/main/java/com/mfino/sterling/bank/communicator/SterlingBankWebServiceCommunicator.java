package com.mfino.sterling.bank.communicator;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.fix.CFIXMsg;
import com.mfino.mce.backend.impl.BaseServiceImpl;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.sterling.bank.util.SterlingBankWebServiceRequest;
import com.mfino.sterling.crypto.CryptoService;
import com.mfino.sterling.crypto.DecryptionException;

/**
 * @author Amar
 *
 */
public abstract class SterlingBankWebServiceCommunicator extends BaseServiceImpl{
	
	public static final String WEBSERVICE_OPERATION_NAME = "IBSBridge";
	
	private String webServiceEndpointBean;
	
	private CryptoService cryptoService;
	
	private boolean encryptionEnabled;
	
	public String getWebServiceEndpointBean() {
		return webServiceEndpointBean;
	}

	public void setWebServiceEndpointBean(String webServiceEndpointBean) {
		this.webServiceEndpointBean = webServiceEndpointBean;
	}
	
	private SterlingBankWSClient xmlWSCommunicator;

	public abstract SterlingBankWebServiceRequest createSterlingBankWebServiceRequest(MCEMessage mceMessage);
	
	public abstract CFIXMsg constructReplyMessage(List<Object> response, CFIXMsg requestFixMessage);
	
	public abstract String createRequestXml(SterlingBankWebServiceRequest sterlingBankWebServiceRequest);
	
	public Log log = LogFactory.getLog(this.getClass());
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public MCEMessage process(Exchange exchange)
	{
		MCEMessage mceMessage = exchange.getIn().getBody(MCEMessage.class);
		log.info("SterlingBankWebServiceCommunicator :: process() BEGIN mceMessage="+mceMessage);
		MCEMessage replyMessage = new MCEMessage();
		List<Object> responseFromWS = new ArrayList<Object>();
		String requestXml = "";
		
		try
		{
			SterlingBankWebServiceRequest sterlingBankWebServiceRequest = createSterlingBankWebServiceRequest(mceMessage);
			log.info("SterlingBankWebServiceCommunicator :: sterlingBankWebServiceRequest="+sterlingBankWebServiceRequest);
			
			requestXml = createRequestXml(sterlingBankWebServiceRequest);
			
			if(isEncryptionEnabled()){
				log.info("Encrypting request");
				requestXml = cryptoService.encrypt(requestXml);
				log.info("Encrypted request:"+requestXml);
			}
			
			if(requestXml != null)
			{
				ProducerTemplate template = exchange.getContext().createProducerTemplate();
				template.start();
				Map<String,Object> headersMap = new HashMap<String,Object>();
				headersMap.put("operationName",WEBSERVICE_OPERATION_NAME);
				MCEUtil.setMandatoryHeaders(exchange.getIn().getHeaders(), headersMap);
				List<Object> encryptedResponseFromWS = (List<Object>)template.requestBodyAndHeaders("cxf:bean:"+webServiceEndpointBean,requestXml,headersMap);								
				template.stop();
				if(isEncryptionEnabled()){
					log.info("Decrypting response:"+(String)encryptedResponseFromWS.get(0));
					String responseXML = cryptoService.decrypt((String)encryptedResponseFromWS.get(0));
					responseFromWS.add(responseXML);
				}else{
					responseFromWS = encryptedResponseFromWS;
				}
			}			
		}
		catch(Exception e){
			log.error("SterlingBankWebServiceCommunicator :: Exception e=",e);
			responseFromWS = handleHttpCommunicationException(e);
		}
		CFIXMsg requestFixMessage = mceMessage.getResponse();
		CFIXMsg responseFixMsg = null;
		
		if(!MCEUtil.SERVICE_TIME_OUT.equals(responseFromWS))
			responseFixMsg=constructReplyMessage(responseFromWS, requestFixMessage);	
		else
			log.info("response is SERVICE_TIME_OUT so return null response");
		
		replyMessage.setRequest(requestFixMessage);
		replyMessage.setResponse(responseFixMsg);
		replyMessage.setDestinationQueues(mceMessage.getDestinationQueues());
		
		log.info("SterlingBankWebServiceCommunicator :: process() END");
		return replyMessage;
	}
	
	public List<Object> handleHttpCommunicationException(Exception e){
		List<Object> responseFromWS = new ArrayList<Object>();
		if(e instanceof DecryptionException
				|| e instanceof SocketTimeoutException)
			responseFromWS.add(MCEUtil.SERVICE_TIME_OUT);
		else
			responseFromWS.add(MCEUtil.SERVICE_UNAVAILABLE);
		return responseFromWS;
	}

	
	public SterlingBankWSClient getXmlWSCommunicator() {
		return xmlWSCommunicator;
	}

	public void setXmlWSCommunicator(SterlingBankWSClient xmlWSCommunicator) {
		this.xmlWSCommunicator = xmlWSCommunicator;
	}
	
	/**
	 * returns a fixed length string containing 7 digits. If the length of the string is less that 7 digits, '0's are prefixed.
	 * If its length exceeds 7 digits, last 7 digits are returned.
	 * 
	 * @param sctlID
	 * @return
	 */
	public String normalize(Long sctlID)
	{
		Long id = sctlID;
		id = id % 10000000 ;
		String referenceNo = id.toString();
		StringBuilder prefix =  new StringBuilder();
		for(int i = 0; i < 7 - referenceNo.length() ; i++)
		{
			prefix = prefix.append("0");
		}
		referenceNo = prefix.toString() + referenceNo;
		return referenceNo;
	}

	
	public boolean isEncryptionEnabled() {
		return encryptionEnabled;
	}

	public void setEncryptionEnabled(boolean encryptionEnabled) {
		this.encryptionEnabled = encryptionEnabled;
	}
	
	public CryptoService getCryptoService() {
		return cryptoService;
	}

	public void setCryptoService(CryptoService cryptoService) {
		this.cryptoService = cryptoService;
	}
}
