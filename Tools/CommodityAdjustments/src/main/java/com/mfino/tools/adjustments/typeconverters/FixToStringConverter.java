package com.mfino.tools.adjustments.typeconverters;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.camel.Converter;

import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;

@Converter
public class FixToStringConverter {

	@Converter
	public static InputStream toString(MCEMessage msg) {

		String str;
		BackendResponse response = (BackendResponse)msg.getResponse();

		str = "NotificationCode=" + response.getInternalErrorCode() + ",srcbalannce=" + response.getSourceMDNBalance() + "," + "destbalance="
		        + response.getDestinationMDNBalance() + ",ct/pct id=" + response.getTransferID()+", sctlid="+response.getServiceChargeTransactionLogID();

		InputStream is = new ByteArrayInputStream(str.getBytes());
		
		return is;

	}

}
