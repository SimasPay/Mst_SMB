package com.mfino.integration.cashout;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Map;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.ws.Holder;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.interswitchng.techquest.Iso8583PostXml;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMThirdPartyCashOut;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.core.security.PinBlockCipher;
import com.mfino.mce.core.security.PinBlockCipher.FORMAT;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.service.TransactionIdentifierService;

public class FrontendCashoutProcessor implements Processor {
	
	public static final String WITHDRAWAL_REQUEST_MSG_TYPE = "0200";
	public static final String WITHDRAWAL_RESPONSE_MSG_TYPE = "0210";
	public static final String REVERSAL_REQUEST_MSG_TYPE = "0420";
	public static final String REVERSAL_RESPONSE_MSG_TYPE = "0430";
	public static final String REVERSAL_ADDLN_REQUEST_MSG_TYPE = "0421";
	
	Logger	       log	= LoggerFactory.getLogger(FrontendCashoutProcessor.class);

	private String	cashOutQueue;

	private TransactionIdentifierService transactionIdentifierService ;

	public TransactionIdentifierService getTransactionIdentifierService() {
		return transactionIdentifierService;
	}

	public void setTransactionIdentifierService(
			TransactionIdentifierService transactionIdentifierService) {
		this.transactionIdentifierService = transactionIdentifierService;
	}

	private String KWP;
	
	private Integer timeout;

	public void setCashOutQueue(String cashOutQueue) {
		this.cashOutQueue = cashOutQueue;
	}

	private String	FrontendID;

	public void setFrontendID(String FrontendID) {
		this.FrontendID = FrontendID;
	}

	private static final String	EXCHANGE_HEADER_SYNCHRONOUS_REQUEST_ID	= "synchronous_request_id";

	@Override
	public void process(Exchange exchange) throws Exception {

		log.info("received intagration cashout request --> ");

		Holder<Iso8583PostXml> holder = exchange.getIn().getBody(Holder.class);

		Iso8583PostXml request = holder.value;

		final String requestID = UUID.randomUUID().toString();
		Map<String, Object> headers = exchange.getIn().getHeaders();
		log.info("synchronous request id " + requestID);
		headers.put(EXCHANGE_HEADER_SYNCHRONOUS_REQUEST_ID, requestID);
		headers.put("FrontendID", FrontendID);

		try {
			CashoutRequestValidator validator = new CashoutRequestValidator(request);
			if (validator.validate().equals(ValidationResult.InvalidRequest)) {
				exchange.getOut().setBody(constructResponse(request, "06", validator));
				return;
			}
			//getting the sourceMDN from the request to create the transactionIdentifier at start
			String uniqueIdMDN = validator.getCustomerID();
			String trxnIdentifier = transactionIdentifierService.generateTransactionIdentifier(uniqueIdMDN);
			MCEUtil.setBreadCrumbId(headers, trxnIdentifier);
			MDC.put(MCEUtil.BREADCRUMB_ID,trxnIdentifier);
			log.info("Transaction Identifier created in FrontendCashoutProcessor with ID -->"+trxnIdentifier);
			log.info("constructing cashoutrequest message");
			CMThirdPartyCashOut cashoutRequest = constructRequest(request, validator);

			CamelContext context = exchange.getContext();
			ProducerTemplate template = context.createProducerTemplate();

			template.start();
			template.sendBodyAndHeaders(cashOutQueue, cashoutRequest, headers);
			template.stop();

			ConsumerTemplate consumerTemplate = context.createConsumerTemplate();
			consumerTemplate.start();
			Exchange resultFromQueuingSystem = consumerTemplate.receive("seda:" + requestID, timeout);
			consumerTemplate.stop();
			log.info("result from queing system " + resultFromQueuingSystem);

			if (resultFromQueuingSystem == null) {
				log.error("did not return anything from the cashout system.");
				exchange.getOut().setBody(constructResponse(request, "06", validator));
				return;
			}

			String result = resultFromQueuingSystem.getIn().getBody(String.class);

			log.info("comparing "+result + " to "+CmFinoFIX.NotificationCode_SuccessfulCashOutFromATM.toString());
			
			log.info("comparing "+result + " to "+CmFinoFIX.NotificationCode_SuccessfulCashOutFromATM.toString());
			
			if (result.equals(CmFinoFIX.NotificationCode_SuccessfulCashOutFromATM.toString()) ||
					result.equals(CmFinoFIX.NotificationCode_SuccessfulReversalFromATM.toString())){
				exchange.getOut().setBody(constructResponse(request, "00", validator));
			}
			else{
				exchange.getOut().setBody(constructResponse(request, result, validator));
			}
			return;

		}
		catch (Exception ex) {
			
			log.error("exception occured.", ex);
			exchange.getOut().setBody(constructResponse(request, "06", null));
		}
		finally{
			MDC.remove(MCEUtil.BREADCRUMB_ID);

		}

	}

	private Holder<Iso8583PostXml> constructResponse(Iso8583PostXml request, String responseCode, CashoutRequestValidator validator) {
		Holder<Iso8583PostXml> responseHolder = null;
		if(request!=null)
		{
			if(WITHDRAWAL_REQUEST_MSG_TYPE.equals(request.getMsgType().getValue()))
			{
				request.getMsgType().setValue(WITHDRAWAL_RESPONSE_MSG_TYPE);
			}
			else if (REVERSAL_REQUEST_MSG_TYPE.equals(request.getMsgType().getValue()))
			{
				request.getMsgType().setValue(REVERSAL_RESPONSE_MSG_TYPE);
			}
			else if (REVERSAL_ADDLN_REQUEST_MSG_TYPE.equals(request.getMsgType().getValue()))
			{
				request.getMsgType().setValue(REVERSAL_RESPONSE_MSG_TYPE);
			}
			Iso8583PostXml.Fields fields = request.getFields().get(0);
			fields.setField052(null);
			fields.setField039(responseCode);
			responseHolder = new Holder<Iso8583PostXml>(request);
			log.info("CashoutResponse:"+responseCode);
			if(validator!=null)
			{
				validator.dumpFields(fields);
			}
		}
		return responseHolder;
	}

	private CMThirdPartyCashOut constructRequest(Iso8583PostXml request, CashoutRequestValidator validator) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {

		CMThirdPartyCashOut details = new CMThirdPartyCashOut();

		Iso8583PostXml.Fields fields = request.getFields().get(0);

		details.setSourceMDN(validator.getCustomerID());
		
		details.setAcquiringBank(fields.getField127014().getValue());

		String amount = fields.getField004();
		Double d = Double.parseDouble(amount);
		d = d/100.0;
//		amount = amount.substring(0, amount.length() - 2);
		details.setAmount(new BigDecimal(d));
		details.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_WITHDRAW_FROM_ATM);
		details.setSourceApplication(CmFinoFIX.SourceApplication_Interswitch);
		details.setIsSecure(false);
		details.setIsSystemIntiatedTransaction(false);
		details.setCAIDCode(fields.getField042().getValue());
		details.setCANameLocation(fields.getField043().getValue());
		details.setCATerminalId(fields.getField041().getValue());
		details.setCurrencyCode(fields.getField049());
		String str = fields.getField013().getValue();
		if (StringUtils.isNotBlank(str))
			details.setLocalTxnDate(Timestamp.fromString(str, "MMdd"));
		str = fields.getField012().getValue();
		if (StringUtils.isNotBlank(str))
			details.setLocalTxnTime(Timestamp.fromString(str, "HHmmss"));
		details.setMessageTypeIndicator(request.getMsgType().getValue());
		details.setOneTimePassCode(validator.getWithdrawlCode());
		if ((fields.getField052() != null) && (fields.getField052().getValue() != null)) {
			details.setPinData(fields.getField052().getValue());
			details.setPin(decodePin(fields.getField052().getValue()));
		}
		details.setPOSData(fields.getField127013().getValue());
		details.setProcessingCode(request.getMsgType().getValue());
		details.setReceiveTime(new Timestamp());
		details.setSTAN(fields.getField011().getValue());
		str = fields.getField028().getValue();
		if (StringUtils.isNotBlank(str)) {
			str = str.substring(1, str.length());
			details.setSurcharge(new BigDecimal(str));
		}
		str = fields.getField007().getValue();
		if (StringUtils.isNotBlank(str))
			details.setTransmissionDateTime(Timestamp.fromString(str, "MMddHHmmss"));
		details.setTxnReferenceId(fields.getField127002().getValue());
		if(fields.getField127011()!=null)
		{
			details.setOriginalTxnReferenceId(fields.getField127011().getValue());
		}
		details.setInstitutionID(this.InstitutionID);
		return details;

	}
	
	private String decodePin(String pinBlock) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException
	{
		String pin = pinBlock;
		
		PinBlockCipher cipher = new PinBlockCipher(KWP);
		char[] decodedPinBlock = cipher.decode(pinBlock);
		pin = cipher.getPIN(FORMAT.ISO1, new String(decodedPinBlock)); 
		
		return pin;
	}
	
	private String InstitutionID;

	public void setInstitutionID(String institutionID) {
    	InstitutionID = institutionID;
    }

	public void setKWP(String kwp)
	{
		KWP = kwp;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
}
