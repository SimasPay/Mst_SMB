package com.mfino.iframework.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.domain.BillPayments;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.iframework.builders.IntegrationBuilder;
import com.mfino.iframework.builders.RequestConstructor;
import com.mfino.iframework.domain.Integration;
import com.mfino.mce.core.MCEMessage;

public class RequestHandler {

	private static Logger	    logger	= LoggerFactory.getLogger(RequestHandler.class);

	private Map<String, String>	requestTypeTorequestFormatMap;

	public void setRequestTypeTorequestFormatMap(Map<String, String> requestTypeTorequestFormatMap) {
		this.requestTypeTorequestFormatMap = requestTypeTorequestFormatMap;
	}

	private BillPaymentsService	billPaymentsService;

	public BillPaymentsService getBillPaymentsService() {
		return billPaymentsService;
	}

	public void setBillPaymentsService(BillPaymentsService billPaymentsService) {
		this.billPaymentsService = billPaymentsService;
	}
	
	private AtomicInteger uniqueSuffix;
	
	public void setUniqueSuffix(AtomicInteger uniqueSuffix) {
		this.uniqueSuffix = uniqueSuffix;
	}

	/**
	 * constructs the request from the variant and request xml files. * For each
	 * element in the request file, a parameter is defined in the variant file
	 * that tells where to get that value from.
	 * 
	 * The required values are put in a Map that is a part of MCEMessage.
	 * 
	 * @param exchange
	 */
	@Handler
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void constructRequest(Exchange exchange, @Header("requestType") String requestType) {
		
		logger.info("constructing the request to send it to the integration");

		MCEMessage mceMessage = exchange.getIn().getBody(MCEMessage.class);
		CMBase requestFix = (CMBase) mceMessage.getRequest();
		Long sctlId = requestFix.getServiceChargeTransactionLogID();

		BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);

		HashMap<String, Object> dataHolder = new HashMap<String, Object>();
		dataHolder.put("txn.amount", billPayments.getAmount().toPlainString());
		dataHolder.put("txn.mdn", billPayments.getSourcemdn());
		dataHolder.put("txn.sctlid", billPayments.getServiceChargeTxnLog().getId());
		dataHolder.put("txn.transactiontime", billPayments.getLastupdatetime());
		dataHolder.put("dynamic.transactiontime", billPayments.getLastupdatetime());
		dataHolder.put("txn.itid", billPayments.getIntxnid());
		String uniqueID = billPayments.getServiceChargeTxnLog().getId().toString()+"00"+uniqueSuffix.incrementAndGet();
		dataHolder.put("txn.sctlasmessagegid", uniqueID);
		dataHolder.putAll(integrationDetails);

		mceMessage.getIntegrationDataHolder().putAll(dataHolder);
		exchange.getIn().setHeader("mceMessage", mceMessage);

		Integration integration = IntegrationBuilder.buildIntegration(variantFilepath);

		RequestConstructor constructor = new RequestConstructor();
		constructor.setIntegrationObject(integration);
		constructor.setRequestTypeTorequestFormatMap(requestTypeTorequestFormatMap);
		String request = constructor.constructRequest(mceMessage.getIntegrationDataHolder(), requestType);

		logger.info("request constructed for sctlid=" + billPayments.getServiceChargeTxnLog().getId() + "-->  " + request);

		exchange.getIn().setBody(request);

	}

	private String	variantFilepath;

	public void setVariantFilepath(String variantFilepath) {
		this.variantFilepath = variantFilepath;
	}

	private Map<String, String>	integrationDetails;

	public void setIntegrationDetails(Map<String, String> integrationDetails) {
		this.integrationDetails = integrationDetails;
	}

}