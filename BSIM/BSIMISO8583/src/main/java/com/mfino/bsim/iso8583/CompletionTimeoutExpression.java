package com.mfino.bsim.iso8583;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.mce.core.MCEMessage;

public class CompletionTimeoutExpression implements Expression {

	Log	log	= LogFactory.getLog(CompletionTimeoutExpression.class);
	private Long timeout;
	private Long reversalTimeout;
	public Long getTimeout() {
		return timeout;
	}
	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}
	public Long getReversalTimeout() {
		return reversalTimeout;
	}
	public void setReversalTimeout(Long reversalTimeout) {
		this.reversalTimeout = reversalTimeout;
	}
	@Override
	public <T> T evaluate(Exchange exchange, Class<T> type) {

		log.info("evaluating completiontimeout expression for aggregration strategy");

		Long l = timeout;

		log.info("aggregration exchange class=" + exchange.getIn().getBody().getClass());

		if (exchange.getIn().getBody() instanceof MCEMessage) {
			MCEMessage msg = exchange.getIn().getBody(MCEMessage.class);
			if (msg.getResponse() instanceof CMMoneyTransferReversalToBank) {
				l = reversalTimeout;
				log.info("Exchange body is of type CMMoneyTransferReversalToBank");
			}
		}
//		if (exchange.getIn().getBody() instanceof ISOMsg) {
//			log.info("Exchange body is of type ISOMsg");
//			ISOMsg isoMsg = exchange.getIn().getBody(ISOMsg.class);
//			try {
//				if (isoMsg.getMTI().equals("0430"))
//					l = 200000l;
//			}
//			catch (ISOException ex) {
//				ex.printStackTrace();
//			}
//		}

		log.info("Calculated aggregration completiontimeout =" + l);

		return type.cast(l);
	}
}
