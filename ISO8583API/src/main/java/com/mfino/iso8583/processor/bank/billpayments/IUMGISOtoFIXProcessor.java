package com.mfino.iso8583.processor.bank.billpayments;

import com.mfino.fix.CFIXMsg;

public interface IUMGISOtoFIXProcessor {
	public CFIXMsg process(UMGH2HISOMessage isoMsg,CFIXMsg msg)throws Exception;
}
