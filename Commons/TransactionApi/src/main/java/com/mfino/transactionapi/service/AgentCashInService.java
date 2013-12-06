package com.mfino.transactionapi.service;

import com.mfino.domain.AgentCashInTransactions;
import com.mfino.fix.CmFinoFIX.CMJSError;

public interface AgentCashInService {

	CMJSError processAgentCashIn(AgentCashInTransactions actl);

}
