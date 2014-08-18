package com.mfino.bsm.uangku.iso8583.processor.isotofix.networkmanagement;

import org.jpos.iso.ISOMsg;

import com.mfino.bsm.uangku.iso8583.processor.BSMISOtoFixProcessor;
import com.mfino.crypto.CryptographyService;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMMACKeyExchangeResponseFromBank;
import com.mfino.fix.CmFinoFIX.CMMACKeyExchangeToBank;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;

public class MacKeyExchangeResponse implements BSMISOtoFixProcessor {

	protected CFIXMsg response;
	
	@Override
	public CFIXMsg process(ISOMsg isoMsg, CFIXMsg request) throws InvalidIsoElementException {
		
		
		CMMACKeyExchangeToBank msg = (CMMACKeyExchangeToBank)request;
		CMMACKeyExchangeResponseFromBank fromBank = new CMMACKeyExchangeResponseFromBank();
		fromBank.setResponseCode(isoMsg.getString(39));
		if (!CmFinoFIX.ISO8583_ResponseCode_Success.equals(fromBank.getResponseCode()))
			return fromBank;
		int DESkeyCount = getKeyCount();
		String key1 = null, key2 = null, key3 = null, kcv = null, info = null;
		try {
			info = isoMsg.getString(53);
			key1 = info.substring(0, 8);
			key2 = key1;
			key3 = key1;
			if (DESkeyCount == 1) {
				kcv = info.substring(8, 11);
			}
			else if (DESkeyCount == 2) {
				key2 = info.substring(8, 16);
				kcv = info.substring(16, 19);
			}
			else if (DESkeyCount == 3) {
				key2 = info.substring(8, 16);
				key3 = info.substring(16, 24);
				kcv = info.substring(24, 27);
			}
			fromBank.setDESKey1(new String(CryptographyService.binToHex(key1.getBytes())));
			fromBank.setDESKey2(new String(CryptographyService.binToHex(key2.getBytes())));
			fromBank.setDESKey3(new String(CryptographyService.binToHex(key3.getBytes())));
			fromBank.setKeyCheckValue(new String(CryptographyService.binToHex(kcv.getBytes())));
		}
		catch (Exception ex) {

		}

		try {
			info = isoMsg.getString(125);
			key1 = info.substring(0, 16);
			key2 = key1;
			key3 = key1;
			if (DESkeyCount == 2)
				key2 = info.substring(16, 32);
			fromBank.setDESKey1(key1);
			fromBank.setDESKey2(key2);
			fromBank.setDESKey3(key3);
			fromBank.setKeyCheckValue(info.substring(32, 38));
		}
		catch (Exception ex) {

		}

		fromBank.copy(msg);
		
		return fromBank;
	}

	//FIXME
	private int getKeyCount() {
		return 2;
	}
}
