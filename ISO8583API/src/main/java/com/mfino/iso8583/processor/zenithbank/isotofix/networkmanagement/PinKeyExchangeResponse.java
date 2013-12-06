package com.mfino.iso8583.processor.zenithbank.isotofix.networkmanagement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.crypto.CryptographyService;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMPinKeyExchangeResponseFromBank;
import com.mfino.iso8583.processor.zenithbank.IZenithBankISOtoFixProcessor;
import com.mfino.iso8583.processor.zenithbank.ZenithBankISOMessage;

public class PinKeyExchangeResponse implements IZenithBankISOtoFixProcessor {

	private Log	log	= LogFactory.getLog(PinKeyExchangeResponse.class);
	protected CFIXMsg	response;

	@Override
	public CFIXMsg process(ZenithBankISOMessage isoMsg, CFIXMsg request) throws Exception {

		CMPinKeyExchangeResponseFromBank fromBank = new CMPinKeyExchangeResponseFromBank();
		fromBank.setResponseCode(isoMsg.getResponseCode());
		if (!CmFinoFIX.ISO8583_ResponseCode_Success.equals(fromBank.getResponseCode()))
			return fromBank;
		int DESkeyCount = getKeyCount();
		String key1 = null, key2 = null, key3 = null, kcv = null, info = null;
		try {
			info = isoMsg.getSecurityControlInfo();
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
			log.error("Error occurred during key exchange process", ex);
		}

		try {
			info = isoMsg.getNetworkManagementInfo();
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
			log.error("Error occurred during key exchange process", ex);
		}

		return fromBank;
	}

	//FIXME
	private int getKeyCount() {
		return 2;
	}
}
