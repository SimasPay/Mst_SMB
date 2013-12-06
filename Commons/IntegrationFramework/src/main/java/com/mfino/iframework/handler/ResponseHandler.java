package com.mfino.iframework.handler;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.billpayments.beans.BillPayResponse;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.iframework.builders.IntegrationBuilder;
import com.mfino.iframework.builders.ResponseParser;
import com.mfino.iframework.de.Constants;
import com.mfino.iframework.de.ResponseCode;
import com.mfino.iframework.domain.Integration;
import com.mfino.mce.core.MCEMessage;

public class ResponseHandler {

	private static Logger	logger	= LoggerFactory.getLogger(ResponseHandler.class);

	private String	      variantFilepath;

	public void setVariantFilepath(String variantFilepath) {
		this.variantFilepath = variantFilepath;
	}

	Map<String,String> responseTypeToresponseFormatMap;
	
	public void setResponseTypeToresponseFormatMap(Map<String, String> responseTypeToresponseFormatMap) {
		this.responseTypeToresponseFormatMap = responseTypeToresponseFormatMap;
	}

	/**
	 * Response is parsed based on the variant file and the response values are
	 * added to the map that is part of MCEMessage.
	 * 
	 * This values in the map will be used to construct further requests(INquiry
	 * response values are used in confirmation request etc...)
	 * 
	 * 
	 * @param exchange
	 */
	@Handler
	public MCEMessage handleResponse(Exchange exchange, @Header("requestType") String requestType) {

		MCEMessage mceMsg = (MCEMessage) exchange.getIn().getHeader("mceMessage");

		MCEMessage newMsg = new MCEMessage();
		newMsg.setRequest(mceMsg.getRequest());

		logger.info("constructing the integration object");
		Integration integration = IntegrationBuilder.buildIntegration(variantFilepath);

		String response = exchange.getIn().getBody(String.class);

		Map<String, String> responseMap = null;
		try {
			logger.info("parsing the response=" + response + " for the request " + requestType);
			ResponseParser rParser = new ResponseParser();
			rParser.setIntegrationObject(integration);
			responseMap = rParser.parseResponse(response, requestType);
		}
		catch (Exception ex) {
			// handle case for reversal
			logger.error("Parsing the response has failed.Should do reversal");
		}

		newMsg.getIntegrationDataHolder().putAll(mceMsg.getIntegrationDataHolder());
		newMsg.getIntegrationDataHolder().putAll(responseMap);

		CMBase base = (CMBase) mceMsg.getRequest();
		logger.info("constructing the billpayresponse object for sctlid=" + base.getServiceChargeTransactionLogID());
		BillPayResponse billpayResponse = new BillPayResponse();
		billpayResponse.setServiceChargeTransactionLogID(base.getServiceChargeTransactionLogID());

		String responseCodeStr = requestType + "." + Constants.RESPONSE_CODE;
		responseCodeStr = responseCodeStr.toLowerCase();

		logger.info("getting " + responseCodeStr + " from the map");
		if (ResponseCode.Success.getMappedCode().equals(responseMap.get(responseCodeStr))) {
			logger.info("respose is identified as successful");
			billpayResponse.setResponse(CmFinoFIX.ResponseCode_Success);
			billpayResponse.setResult(CmFinoFIX.ResponseCode_Success);
		}
		else {
			logger.info("respose is identified as failure");
			billpayResponse.setResponse(CmFinoFIX.ResponseCode_Failure);
			billpayResponse.setResult(CmFinoFIX.ResponseCode_Failure);
		}

		newMsg.setResponse(billpayResponse);

		return newMsg;
	}

}