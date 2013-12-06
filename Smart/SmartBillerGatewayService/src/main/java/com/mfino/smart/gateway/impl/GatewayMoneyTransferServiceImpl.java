package com.mfino.smart.gateway.impl;

import com.mfino.billpayments.service.impl.BillPayMoneyTransferServiceImpl;

public class GatewayMoneyTransferServiceImpl extends BillPayMoneyTransferServiceImpl {

	@Override
	public void setReversalResponseQueue(String reversalResponseQueue) {
		this.reversalResponseQueue = reversalResponseQueue;
	}

	@Override
	public void setSourceToSuspenseBankResponseQueue(String sourceToSuspenseBankResponseQueue) {
		this.sourceToSuspenseBankResponseQueue = sourceToSuspenseBankResponseQueue;
	}

	@Override
	public void setSuspenseToDestBankResponseQueue(String suspenseToDestBankResponseQueue) {
		this.suspenseToDestBankResponseQueue = suspenseToDestBankResponseQueue;
	}

}