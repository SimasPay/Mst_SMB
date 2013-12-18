package com.mfino.cashin;

import com.mfino.domain.ChannelCode;
import com.mfino.fix.CmFinoFIX.CMInterswitchCashin;
import com.mfino.result.Result;

public interface CashinHandler {


	public Result handle(CMInterswitchCashin details, ChannelCode cc,String transactionIdentifier);
	
}
