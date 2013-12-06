package com.mfino.fep.processor;

import java.math.BigDecimal;

import javax.jms.JMSException;

import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.fep.messaging.ChannelCommunicationException;
import com.mfino.fep.messaging.QueueChannel;
import com.mfino.fep.validators.CashoutRequestValidator;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMThirdPartyCashOut;
import com.mfino.hibernate.Timestamp;

public class CashOutRequestProcessor extends ISORequestProcessor {
	private static Logger log = LoggerFactory.getLogger(CashOutRequestProcessor.class);

	public CashOutRequestProcessor() throws JMSException {
		channel = new QueueChannel("CashoutIQueue");
	}

	@Override
	public void process(ISOMsg msg) throws ChannelCommunicationException, ISOException {

		log.info("CashOutRequestProcessor:process()::Begin MTI:" + msg.getMTI());
		try {
			CMThirdPartyCashOut request = new CMThirdPartyCashOut();
			CashoutRequestValidator cashoutRequestValidator = (CashoutRequestValidator) validator;
			request.setSourceMDN(cashoutRequestValidator.getCustomerMDN());
			String amount = msg.getString(4);
			Double d = Double.parseDouble(amount);
			d = d / 100.0;
			request.setAmount(new BigDecimal(d));

			log.info("Cashout request from mdn:"+ cashoutRequestValidator.getCustomerMDN() + " For amount:"+ d);

			// request.setAcquiringBank(msg.getString(52));

			request.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_WITHDRAW_FROM_ATM);
			request.setSourceApplication(CmFinoFIX.SourceApplication_Interswitch);
			request.setIsSecure(false);
			request.setIsSystemIntiatedTransaction(true);
			request.setCATerminalId(msg.getString(41));
			request.setCAIDCode(msg.getString(42));
			request.setCANameLocation(msg.getString(43));
			request.setCurrencyCode(msg.getString(49));
			String str = msg.getString(13);
			if (StringUtils.isNotBlank(str))
				request.setLocalTxnDate(Timestamp.fromString(str, "MMdd"));
			str = msg.getString(12);
			if (StringUtils.isNotBlank(str))
				request.setLocalTxnTime(Timestamp.fromString(str, "HHmmss"));
			request.setMessageTypeIndicator(msg.getMTI());
			request.setOneTimePassCode(cashoutRequestValidator.getFAC());
			request.setProcessingCode((msg.getString(3)));
			request.setReceiveTime(new Timestamp());
			request.setSTAN((msg.getString(11)));
			str = msg.getString(28);
			if (StringUtils.isNotBlank(str)) {
				str = str.substring(1, str.length());
				request.setSurcharge(new BigDecimal(str));
			}
			str = msg.getString(7);
			if (StringUtils.isNotBlank(str))
				request.setTransmissionDateTime(Timestamp.fromString(str,"MMddHHmmss"));
			request.setInstitutionID(cashoutRequestValidator.getInstitutionID());
			msg.unset("127.22");// unset subfield 22
			msg.set(39, getResponse(request));
		}finally{
			try {
				log.info("Closing channel");
				channel.close();
			} catch (JMSException ex) {
				log.error("Error closing Queue Channel");
			}
		}

		log.info("CashOutRequestProcessor:process()::End ");

	}

}
