package com.mfino.smart.gateway.impl;

import java.util.Collection;
import java.util.List;
import com.mfino.billpayments.service.impl.BillPaymentsSuspenceClearanceServiceImpl;
import com.mfino.domain.BillPayments;
import com.mfino.fix.CmFinoFIX;
import com.mfino.mce.core.MCEMessage;

public class GatewaySuspenseClearanceServiceImpl extends BillPaymentsSuspenceClearanceServiceImpl {

	@Override
	public void setSuspenseToDestinationQueue(MCEMessage mceMessage) {
		mceMessage.setDestinationQueue("jms:gatewaySuspenseToDestinationInquiryQueue?disableReplyTo=true");
	}

	@Override
	public void setSuspenseAndChargesToReversalQueue(MCEMessage mceMessage) {
		mceMessage.setDestinationQueue("jms:gatewaySuspenseAndChargesToSourceReversalQueue?disableReplyTo=true");
	}

	@Override
	public void addValidForClearanceStates(Collection<Integer> suspenseToDestFailedStatuses) {
		super.addValidForClearanceStates(suspenseToDestFailedStatuses);
		suspenseToDestFailedStatuses.add(CmFinoFIX.BillPayStatus_BILLER_INQUIRY_FAILED);
	}
	
	@Override
	public void handleClearanceStates(List<MCEMessage> mceMessageList, BillPayments billPayment, MCEMessage mceMessage) {
		super.handleClearanceStates(mceMessageList, billPayment, mceMessage);
		if (billPayment.getBillPayStatus().equals(CmFinoFIX.BillPayStatus_BILLER_INQUIRY_FAILED)) {
			handleInquiryFailedState(mceMessageList, billPayment, mceMessage);
		}
	}

	public void handleInquiryFailedState(List<MCEMessage> mceMessageList, BillPayments billPayment, MCEMessage mceMessage) {
		handleConfirmationFailedState(mceMessageList, billPayment, mceMessage);
	}

}