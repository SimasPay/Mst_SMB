package com.mfino.clickatell.iso8583;
import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.fix.CmFinoFIX.CMCommodityTransferReversalToOperator;
import com.mfino.mce.core.MCEMessage;

public class CompletionTimeoutExpression implements Expression {

	Log	log	= LogFactory.getLog(CompletionTimeoutExpression.class);
	private Long timeout;
	
	public Long getTimeout() {
		return timeout;
	}
	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}
	@Override
	public <T> T evaluate(Exchange exchange, Class<T> type) {

		log.info("evaluating completiontimeout expression for aggregration strategy");

		Long l = timeout;

		log.info("aggregration exchange class=" + exchange.getIn().getBody().getClass());

		if (exchange.getIn().getBody() instanceof MCEMessage) {
			MCEMessage msg = exchange.getIn().getBody(MCEMessage.class);
	      }

		log.info("Calculated completiontimeout =" + l);

		return type.cast(l);
	}
}
