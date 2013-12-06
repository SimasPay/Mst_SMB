package com.mfino.integration.cashout;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import javax.xml.ws.Holder;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.interswitchng.techquest.Iso8583PostXml;
import com.mfino.fix.CmFinoFIX.CMThirdPartyCashOut;
import com.mfino.hibernate.Timestamp;
import com.mfino.result.Result;

public class IntegrationCashoutProcessor implements Processor {

	Logger	       log	= LoggerFactory.getLogger(IntegrationCashoutProcessor.class);

	private String	cashOutQueue;
	private Integer timeout;

	public void setCashOutQueue(String cashOutQueue) {
		this.cashOutQueue = cashOutQueue;
	}

	private String	channelNumber;

	public void setChannelNumber(String channelNumber) {
		this.channelNumber = channelNumber;
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
		headers.put("CashinChannel", channelNumber);

		try {
			CashoutRequestValidator validator = new CashoutRequestValidator(request);
			if (validator.validate().equals(ValidationResult.InvalidRequest)) {
				exchange.getOut().setBody(constructResponse(request, "06"));
				return;
			}
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
				exchange.getOut().setBody(constructResponse(request, "06"));
				return;
			}
			
			Result result = resultFromQueuingSystem.getIn().getBody(Result.class);

			if (result.getNotificationCode().equals(711))
				exchange.getOut().setBody(constructResponse(request, "00"));
			else
				exchange.getOut().setBody(constructResponse(request, "06"));

			return;

		}
		catch (Exception ex) {

		}

	}

	private Holder<Iso8583PostXml> constructResponse(Iso8583PostXml request, String responseCode) {
		Iso8583PostXml.Fields fields = request.getFields().get(0);
		fields.setField052(null);
		fields.setField039(responseCode);
		Holder<Iso8583PostXml> responseHolder = new Holder<Iso8583PostXml>(request);
		return responseHolder;
	}

	private CMThirdPartyCashOut constructRequest(Iso8583PostXml request, CashoutRequestValidator validator) {

		CMThirdPartyCashOut details = new CMThirdPartyCashOut();

		Iso8583PostXml.Fields fields = request.getFields().get(0);

		details.setAcquiringBank(fields.getField127014().getValue());

		String amount = fields.getField004();
		amount = amount.substring(0, amount.length() - 2);
		details.setAmount(new BigDecimal(amount));

		details.setIsSecure(false);
		details.setIsSystemIntiatedTransaction(false);
		details.setCAIDCode(fields.getField042().getValue());
		details.setCANameLocation(fields.getField043().getValue());
		details.setCATerminalId(fields.getField041().getValue());
		details.setCurrencyCode(fields.getField049());
		details.setLocalTxnDate(Timestamp.fromString(fields.getField013().getValue(), "MMdd"));
		details.setLocalTxnTime(Timestamp.fromString(fields.getField012().getValue(), "HHmmss"));
		details.setMessageTypeIndicator(request.getMsgType().getValue());
		details.setOneTimePassCode(validator.getWithdrawlCode());
		details.setPinData(fields.getField052().getValue());
		// FIXME
		// details.setPin
		details.setPOSData(fields.getField127013().getValue());
		details.setProcessingCode(request.getMsgType().getValue());
		details.setReceiveTime(new Timestamp());
		details.setSTAN(fields.getField011().getValue());
		details.setSurcharge(new BigDecimal(fields.getField028().getValue()));
		details.setTransmissionDateTime(Timestamp.fromString(fields.getField007().getValue()));
		details.setTxnReferenceId(fields.getField127002().getValue());

		return details;

	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

}
