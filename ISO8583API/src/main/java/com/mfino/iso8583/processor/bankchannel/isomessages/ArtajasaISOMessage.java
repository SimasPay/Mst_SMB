package com.mfino.iso8583.processor.bankchannel.isomessages;

import java.io.OutputStream;

import com.mfino.fix.CmFinoFIX;
import com.mfino.iso8583.WrapperISOMessage;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;

public class ArtajasaISOMessage extends WrapperISOMessage {

	public ArtajasaISOMessage(IsoMessage msg) {
		super(msg);
	}

	/**
	 * 22
	 * 
	 */
	public void setPointOfServiceEntryMode(int n) {
		originalIsoMessage.setValue(22, n, IsoType.NUMERIC, 3);
	}

	/**
	 * 22
	 * 
	 */
	public Integer getPointOfServiceEntryMode() {
		return Integer.parseInt(originalIsoMessage.getObjectValue(22).toString());
	}

	/**
	 * 32
	 * 
	 */
	public void setCollectionAgentCode(String code) {
		originalIsoMessage.setValue(32, code, IsoType.LLVAR, 0);
	}

	/**
	 * 32
	 * 
	 */
	public String getCollectionAgentCode() {
		return originalIsoMessage.getObjectValue(22).toString();
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

	}

	private static final String ISOVariant = CmFinoFIX.ISO8583_Variant_Artajasa_Gateway_Interface;
	public String getISOVariant() {
		return ISOVariant;
	}
}
