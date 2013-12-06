package com.mfino.iso8583.processor.bankchannel.fixtoiso;

import com.mfino.fix.CFIXMsg;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;

public class Register implements IFIXtoISOProcessor{

	@Override
    public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {

		//	ForwardResponse(*pISOMsg,Response.GetISO8583_ResponseCodeValue());
		return null;
    }

}
