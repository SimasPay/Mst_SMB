package com.mfino.tools.adjustments;

import org.apache.camel.Exchange;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccount;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccountConfirmation;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;

public class FilterBean {

	public boolean isRequestObjectOfBankToBank(Exchange exchange) {
		boolean b = exchange.getIn().getBody(MCEMessage.class).getRequest().getClass().equals(CMBankAccountToBankAccount.class);
		return b;
	}

	public boolean isRequestObjectOfMoneyTransferReversalFromBank(Exchange exchange) {
		boolean b = exchange.getIn().getBody(MCEMessage.class).getRequest().getClass().equals(CMMoneyTransferReversalFromBank.class);
		return b;
	}

	public boolean isRequestObjectOfBankToBankConfirm(Exchange exchange) {
		boolean b = exchange.getIn().getBody(MCEMessage.class).getRequest().getClass().equals(CMBankAccountToBankAccountConfirmation.class);
		return b;
	}

	public boolean isRequestObjectOfTransferInquiryFromBank(Exchange exchange) {
		boolean b = exchange.getIn().getBody(MCEMessage.class).getRequest().getClass().equals(CMTransferInquiryFromBank.class);
		return b;
	}

	public boolean isRequestObjectOfMoneyTransferFromBank(Exchange exchange) {
		boolean b = exchange.getIn().getBody(MCEMessage.class).getRequest().getClass().equals(CMMoneyTransferFromBank.class);
		return b;
	}

	public boolean isRequestObjectOfBackendResponse(Exchange exchange) {
		boolean b = exchange.getIn().getBody(MCEMessage.class).getRequest().getClass().equals(BackendResponse.class);
		return b;
	}
	
	public boolean isResponseObjectOfBackendResponse(Exchange exchange) {
		boolean b = exchange.getIn().getBody(MCEMessage.class).getResponse().getClass().equals(BackendResponse.class);
		return b;
	}

	public boolean isResponseObjectOfMoneyTransferReversalToBank(Exchange exchange) {
		boolean b = exchange.getIn().getBody(MCEMessage.class).getResponse().getClass().equals(CMMoneyTransferReversalToBank.class);
		return b;
	}

	public boolean isResponseObjectOfTransferInquiryToBank(Exchange exchange) {
		boolean b = exchange.getIn().getBody(MCEMessage.class).getResponse().getClass().equals(CMTransferInquiryToBank.class);
		return b;
	}

	public boolean isResponseObjectOfMoneyTransferToBank(Exchange exchange) {
		boolean b = exchange.getIn().getBody(MCEMessage.class).getResponse().getClass().equals(CMMoneyTransferToBank.class);
		return b;
	}

	public boolean isInquirySuccessful(Exchange exchange) {
		boolean b = isSuccessful(exchange, 10072);
		return b;
	}

	public boolean isInquiryNotSuccessful(Exchange exchange) {
		boolean b = !(isInquirySuccessful(exchange));
		return b;
	}

	public boolean isEtoEConfirmationSuccessful(Exchange exchange) {
		boolean b = isSuccessful(exchange, 10293);
		return b;
	}

	public boolean isBtoEConfirmationSuccessful(Exchange exchange) {
		boolean b = isSuccessful(exchange, 10305);
		return b;
	}

	public boolean isEtoBConfirmationSuccessful(Exchange exchange) {
		boolean b = isSuccessful(exchange, 10307);
		return b;
	}

	public boolean isBtoBConfirmationSuccessful(Exchange exchange) {
		boolean b = isSuccessful(exchange, 10081);
		return b;
	}

	private boolean isSuccessful(final Exchange exchange, final Integer code) {
		CFIXMsg msg = exchange.getIn().getBody(MCEMessage.class).getResponse();
		if (msg == null || !(msg.getClass().equals(BackendResponse.class)))
			return false;
		BackendResponse br = (BackendResponse) msg;
		boolean b = br.getInternalErrorCode().equals(code);
		return b;
	}

	public boolean isConfirmationNotSuccessful(Exchange exchange) {
		boolean b = (isEtoEConfirmationNotSuccessful(exchange) && isEtoBConfirmationNotSuccessful(exchange)
		        && isBtoEConfirmationNotSuccessful(exchange) && isBtoBConfirmationNotSuccessful(exchange));
		return b;
	}

	private boolean isEtoEConfirmationNotSuccessful(Exchange exchange) {
		boolean b = !(isEtoEConfirmationSuccessful(exchange));
		return b;
	}

	private boolean isBtoEConfirmationNotSuccessful(Exchange exchange) {
		boolean b = !(isBtoEConfirmationSuccessful(exchange));
		return b;
	}

	private boolean isEtoBConfirmationNotSuccessful(Exchange exchange) {
		boolean b = !(isEtoBConfirmationSuccessful(exchange));
		return b;
	}

	private boolean isBtoBConfirmationNotSuccessful(Exchange exchange) {
		boolean b = !(isBtoBConfirmationSuccessful(exchange));
		return b;
	}

}
