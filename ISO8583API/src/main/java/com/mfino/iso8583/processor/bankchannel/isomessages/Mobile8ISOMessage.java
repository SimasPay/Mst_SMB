package com.mfino.iso8583.processor.bankchannel.isomessages;

import java.io.OutputStream;

import com.mfino.fix.CmFinoFIX;
import com.mfino.iso8583.WrapperISOMessage;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;

public class Mobile8ISOMessage extends WrapperISOMessage {

	public Mobile8ISOMessage(IsoMessage isoMsg) {
		super(isoMsg);
	}

	
	/**
	 * 
	 * 48
	 */
	public void setBillingProvidertData(String data) throws Exception{
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
	 * 63
	 */
	public void setInstitutionCode(String code) {
		originalIsoMessage.setValue(63, code, IsoType.LLLVAR, 0);
	}
	/**
	 * 
	 * 63
	 */
	public String getInstitutionCode() {
		return originalIsoMessage.getObjectValue(63).toString();
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

	@Override
	public void write(OutputStream outs, int lengthBytes) throws Exception {
		// TODO Auto-generated method stub

	}

	private static final String ISOVariant = CmFinoFIX.ISO8583_Variant_Mobile8_Gateway_Interface;
	public String getISOVariant() {
		return ISOVariant;
	}
}
