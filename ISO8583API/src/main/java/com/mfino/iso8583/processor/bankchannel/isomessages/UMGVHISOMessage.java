package com.mfino.iso8583.processor.bankchannel.isomessages;

import java.io.OutputStream;

import com.mfino.fix.CmFinoFIX;
import com.mfino.iso8583.WrapperISOMessage;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;

public class UMGVHISOMessage extends WrapperISOMessage{

	public UMGVHISOMessage(IsoMessage isoMsg) {
		super(isoMsg);
	}
	
	/**
	 * 
	 * 48
	 */
	public void setBillingProvidertData(String data) throws Exception {
		originalIsoMessage.setValue(48, data, IsoType.LLLVAR, 0);
	}

	/**
	 * 
	 * 48
	 */
	public String getBillingProvidertData() {
		return originalIsoMessage.getObjectValue(48).toString();
	}

	
	
	/**
	 * 
	 * 90
	 */
	public void setOriginalPaymentTransactionData(String data) {
		originalIsoMessage.setValue(90, data, IsoType.ALPHA, 42);
	}

	/**
	 * 
	 * 90
	 */
	public String getOriginalPaymentTransactionData() {
		return originalIsoMessage.getObjectValue(90).toString();
	}

	private static final String ISOVariant = CmFinoFIX.ISO8583_Variant_Sinarmas_VirtualHost_Gateway_Interface;
	
	@Override
    public void write(OutputStream outs, int lengthBytes) throws Exception {
	    
    }
	
	public String getISOVariant() {
		return ISOVariant;
	}

}
