package com.mfino.mce.frontend;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.MCEUtil;

public class FrontendSetHeader implements Processor {

	Log	log	= LogFactory.getLog(FrontendSetHeader.class);

	@Override
	public void process(Exchange e) throws Exception {
		Object obj = e.getIn().getBody();
		String correlationId = "";
		if (obj instanceof ISOMsg) {
			String transactionID = ((ISOMsg) obj).getString(11).toString();
			Long tID = Long.parseLong(transactionID);
			correlationId = tID.toString();
		}
		else if (obj instanceof MCEMessage) {
			CFIXMsg fixMesg = ((MCEMessage) obj).getResponse();
			Long tID = ((CMBase)fixMesg).getTransactionID();
			tID = tID%1000000;
			
			correlationId = tID.toString();
			
//			if (fixMesg instanceof CMBalanceInquiryToBank) {
//				correlationId = ((CMBalanceInquiryToBank) fixMesg).getTransactionID().toString();
//			}
//			else if (fixMesg instanceof CMSignOnToBank) {
//				correlationId = ((CMSignOnToBank) fixMesg).getTransactionID().toString();
//			}
//			else if (fixMesg instanceof CMPinKeyExchangeToBank) {
//				correlationId = ((CMPinKeyExchangeToBank) fixMesg).getTransactionID().toString();
//			}
		}
		log.info("header setter:" + obj.getClass());
		if (correlationId == null || correlationId.trim().equals("")) {
			log.info("got null correlation id");
			correlationId = Long.toString(UUID.randomUUID().getMostSignificantBits()).substring(0, 6);
		}
		log.info("Correlation id constructed--" + correlationId + "--");
		Map<String,Object> headersMap = new HashMap<String, Object>();
		headersMap.putAll(e.getIn().getHeaders());
		headersMap.put("corr_id", correlationId);
		MCEUtil.setMandatoryHeaders(e.getIn().getHeaders(), headersMap);
		e.getIn().setHeaders(headersMap);
		//return correlationId;
	}
}
