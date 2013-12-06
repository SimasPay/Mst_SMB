package com.mfino.iso8583.processor.zenithbank.fixtoiso.networkmanagement;

import java.io.IOException;

import com.mfino.crypto.CryptographyService;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMMACKeyExchangeToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.IFIXtoISOProcessor;
import com.mfino.iso8583.WrapperISOMessage;
import com.mfino.iso8583.WrapperISOMessageFactory;
import com.mfino.iso8583.processor.zenithbank.ZenithBankISOMessage;
import com.mfino.util.DateTimeUtil;

public class MACKeyExchange implements IFIXtoISOProcessor {

	protected ZenithBankISOMessage isoMsg ;
	
	public MACKeyExchange() throws IOException {
		isoMsg = (ZenithBankISOMessage) WrapperISOMessageFactory.newWrapperISOMessage(0x800,CmFinoFIX.ISO8583_Variant_Zenith_Bank_Interface);
	}
	
	@Override
	public WrapperISOMessage process(CFIXMsg fixmsg) throws Exception {
		
		CMMACKeyExchangeToBank request = (CMMACKeyExchangeToBank)fixmsg;
		
		Timestamp ts = DateTimeUtil.getLocalTime();
		isoMsg.setTransmissionTime(ts);//7
		isoMsg.setSTAN(123456);//11
		isoMsg.setLocalTransactionTime(ts);//12
		isoMsg.setLocalTransactionDate(ts);//13
		isoMsg.setNetworkManagementInformationCode(160); //70
		byte[] macExt = {' ',' ',' ',' ',' ',' ',' ',' '};
		macExt[4] = request.getKeyToExchange().byteValue();
		//HexCodec.hexEncode(macExt) can be used instead of this
		String hexVal = new String(CryptographyService.binToHex(macExt));//FIXME is this fine?
		//FIXME look in their system
		isoMsg.setMACExtended(hexVal);//128
		return isoMsg;
	}

}