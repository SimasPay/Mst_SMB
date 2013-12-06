/**
 * 
 */
package com.mfino.gt.blackboxws.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.gt.blackbox.cache.BlackBoxLocalCache;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.mce.core.ws.WSCommunicator;

/**
 * @author Pradeep
 * 
 */
public class GTBankBlackBoxWSImpl extends WSCommunicator {

	public static final String WEBSERVICE_OPERATION_NAME = "ConvertNUBAN";
	public static final String SENDER_ID = "GTBank";
	private Log log = LogFactory.getLog(GTBankBlackBoxWSImpl.class);
	private String webServiceEndpointBean;
	private String messageName;

	BlackBoxLocalCache localCache;
 
	@Override
	public String getWebServiceEndpointBean() {
		return webServiceEndpointBean;
	}

	@Override
	public void setWebServiceEndpointBean(String webServiceEndpointBean) {
		this.webServiceEndpointBean = webServiceEndpointBean;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		MCEMessage mceMessage = exchange.getIn().getBody(MCEMessage.class);
		messageName = getMessageName(mceMessage);
		log.info(messageName);

		if (mceMessage.getResponse() instanceof CMMoneyTransferToBank) {
			CMMoneyTransferToBank bankRequest;
			bankRequest = (CMMoneyTransferToBank) mceMessage.getResponse();

			log.info("BlackBox WebService Source Account Number: "
					+ bankRequest.getSourceCardPAN());
			// call to get the converted account number to be sent to bank
			String convertedSourceAcNum = convertAcNum(
					bankRequest.getSourceCardPAN(), exchange);
			
			bankRequest.setSourceCardPAN(convertedSourceAcNum);
			log.info("Got response from BlackBox WebService, Converted Source Account Number:"
					+ convertedSourceAcNum);

			log.info("BlackBox WebService Dest Account Number: "
					+ bankRequest.getSourceCardPAN());
			String convertedDestAcNum = convertAcNum(
					bankRequest.getDestCardPAN(), exchange);
			bankRequest.setDestCardPAN(convertedDestAcNum);
			log.info("Got response from BlackBox WebService, Converted Dest Account Number:"
					+ convertedDestAcNum);
		} else if (mceMessage.getResponse() instanceof CMBalanceInquiryToBank) {
			CMBalanceInquiryToBank bankRequest;
			bankRequest = (CMBalanceInquiryToBank) mceMessage.getResponse();

			log.info("BlackBox WebService Source Account Number: "
					+ bankRequest.getSourceCardPAN());
			String convertedSourceAcNum = convertAcNum(
					bankRequest.getSourceCardPAN(), exchange);
			bankRequest.setSourceCardPAN(convertedSourceAcNum);
			log.info("Got response from BlackBox WebService, Converted Source Account Number:"
					+ convertedSourceAcNum);

		} else if (mceMessage.getResponse() instanceof CMGetLastTransactionsToBank) {
			CMGetLastTransactionsToBank bankRequest;
			bankRequest = (CMGetLastTransactionsToBank) mceMessage
					.getResponse();

			log.info("BlackBox WebService Source Account Number: "
					+ bankRequest.getSourceCardPAN());
			String convertedSourceAcNum = convertAcNum(
					bankRequest.getSourceCardPAN(), exchange);
			bankRequest.setSourceCardPAN(convertedSourceAcNum);
			log.info("Got response from BlackBox WebService, Converted Source Account Number:"
					+ convertedSourceAcNum);

		} else if (mceMessage.getResponse() instanceof CMTransferInquiryToBank) {
			CMTransferInquiryToBank bankRequest;
			bankRequest = (CMTransferInquiryToBank) mceMessage.getResponse();
			log.info("BlackBox WebService Source Account Number: "
					+ bankRequest.getSourceCardPAN());
			String convertedSourceAcNum = convertAcNum(
					bankRequest.getSourceCardPAN(), exchange);
			bankRequest.setSourceCardPAN(convertedSourceAcNum);
			log.info("Got response from BlackBox WebService, Converted Source Account Number:"
					+ convertedSourceAcNum);

		}

	}
	
	/**
	 * Takes in a 10 digit account number and finds the account number that can be sent 
	 * to bank
	 * 1. If account number sent is not 10 digit no processing is done, same number is returned
	 * 2. if account is not 10 digits configured account number mapping file is checked for corresponging
	 *    number otherwise web service request is done for getting the converted number.
	 * 
	 * @param acNum
	 * @param exchange
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String convertAcNum(String acNum, Exchange exchange) {
		if (acNum.length() == 10) 
		{
		    String convertedAccNum = localCache.getConvertedAccNum(acNum);
		    //got a value from local cache so return it
		    if(convertedAccNum != null)
		    {
		    	log.info("got number from the local cache for "+acNum+":"+convertedAccNum);
		    	return convertedAccNum;
		    }
		    log.info("could not find the number in local cache will do a web service call");
			// local cache doesnt have a value to
			List<Object> params = new ArrayList<Object>();
			params.add(acNum);

			List<Object> responseFromWS = null;
			try {
				/**
				 * Need to create the ProducerTemplate and start then and then after use stop it.
				 * The reason for this camel holds on to the internal thread that is created 
				 * when creating ProducerTemplate if not used this way
				 */
				ProducerTemplate template = exchange.getContext().createProducerTemplate();
				template.start();
				Map<String,Object> headersMap = new HashMap<String,Object>();
				headersMap.put("operationName",WEBSERVICE_OPERATION_NAME);
				MCEUtil.setMandatoryHeaders(exchange.getIn().getHeaders(), headersMap);
				responseFromWS = (List<Object>)template.requestBodyAndHeaders("cxf:bean:"+webServiceEndpointBean,params,headersMap);
				template.stop();
			} catch (Exception e) {
				// any exception during call to web service need to be catched
				// and
				// it needs to be treated as failure
				log.warn("Exception during call to Black Box web service", e);
				responseFromWS = new ArrayList<Object>();
				responseFromWS.add(MCEUtil.SERVICE_UNAVAILABLE);
			}
			return responseFromWS.get(0).toString();
		}
		return acNum;
	}

	public BlackBoxLocalCache getLocalCache() {
		return localCache;
	}

	public void setLocalCache(BlackBoxLocalCache localCache) {
		this.localCache = localCache;
	}

	@Override
	public List<Object> getParameterList(MCEMessage mceMessage) {

		List<Object> params = new ArrayList<Object>();
		CMMoneyTransferToBank bankRequest;
		bankRequest = (CMMoneyTransferToBank) mceMessage.getResponse();

		params.add(bankRequest.getSourceCardPAN());

		log.info("BlackBox WebService Source Account Number: "
				+ bankRequest.getSourceCardPAN());
		return params;
	}

	@Override
	public MCEMessage constructReplyMessage(List<Object> response,
			MCEMessage requestMceMessage) {

		Object wsResponseElement = response.get(0);
		log.info("Got response from BlackBox WebService, Converted Source Account Number:"
				+ wsResponseElement.toString());

		CMMoneyTransferToBank bankRequest;
		bankRequest = (CMMoneyTransferToBank) requestMceMessage.getResponse();
		bankRequest.setSourceCardPAN(wsResponseElement.toString());
		return requestMceMessage;
	}

	@Override
	public String getMessageName(MCEMessage mceMessage) {
		return "GT Bank BlackBox Web Service " + MCEMessage.class;
	}

}
