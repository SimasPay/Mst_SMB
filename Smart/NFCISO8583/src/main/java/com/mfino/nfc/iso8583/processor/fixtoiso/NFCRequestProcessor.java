package com.mfino.nfc.iso8583.processor.fixtoiso;

import static com.mfino.nfc.iso8583.utils.DateTimeFormatter.getHHMMSS;
import static com.mfino.nfc.iso8583.utils.DateTimeFormatter.getMMDD;
import static com.mfino.nfc.iso8583.utils.DateTimeFormatter.getMMDDHHMMSS;

import java.util.Map;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.iso8583.definitions.fixtoiso.IFixToIsoProcessor;
import com.mfino.util.DateTimeUtil;

public abstract class NFCRequestProcessor implements IFixToIsoProcessor {

	protected ISOMsg	          isoMsg	= new ISOMsg();

	protected Map<String, String>	constantFieldsMap;
	
	protected Logger log = LoggerFactory.getLogger(this.getClass());
	
	public int	TPM_UseBankNewCodes;

	public void setConstantFieldsMap(Map<String, String> map) {
		this.constantFieldsMap = map;
		TPM_UseBankNewCodes = Integer.parseInt(constantFieldsMap.get("TPM_UseBankNewCodes"));
	}

	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException {

		try {
			
			isoMsg.set(2, constantFieldsMap.get("2"));// 2
			Timestamp ts = DateTimeUtil.getLocalTime();
			isoMsg.set(7, getMMDDHHMMSS(ts)); // 7
		}
		catch (ISOException ex) {

		}
		return isoMsg;
	}

	
	

}
