package com.mfino.transactionapi.handlers.interswitch;

import com.mfino.domain.ChannelCode;
import com.mfino.fix.CmFinoFIX.CMInterswitchCashin;
import com.mfino.result.Result;

public interface CashinReversalHandler {

	Result handle(CMInterswitchCashin cashIn, ChannelCode cc, String header);

}
