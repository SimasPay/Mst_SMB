package com.mfino.mce.frontend.test;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMSignOnToBank;

public class CMSignOnTest 
{
	public CFIXMsg getMessage()
	{
		CMSignOnToBank fixMesg = new CMSignOnToBank();
		fixMesg.setTransactionID(100000L);
		return fixMesg;
	}
}
