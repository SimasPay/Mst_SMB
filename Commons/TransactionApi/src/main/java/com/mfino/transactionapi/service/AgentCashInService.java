package com.mfino.transactionapi.service;

import com.mfino.domain.AgentCashinTxnLog;
import com.mfino.fix.CmFinoFIX.CMJSError;

public interface AgentCashInService {

	CMJSError processAgentCashIn(AgentCashinTxnLog actl);

}
