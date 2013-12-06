package com.mfino.scheduler.settlement;

import com.mfino.exceptions.MfinoRuntimeException;

public interface SettlementHandler {
	public void doSettlement(Long partnerServiceId) throws MfinoRuntimeException;
}
