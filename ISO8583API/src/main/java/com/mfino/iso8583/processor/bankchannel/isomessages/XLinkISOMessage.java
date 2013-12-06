package com.mfino.iso8583.processor.bankchannel.isomessages;

import java.io.OutputStream;

import com.mfino.fix.CmFinoFIX;
import com.mfino.iso8583.WrapperISOMessage;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;

public class XLinkISOMessage extends WrapperISOMessage {

	public XLinkISOMessage(IsoMessage isoMsg) {
		super(isoMsg);
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
	 * 25
	 *
	 */
	public void setPOSEntryMode(int n) {
		originalIsoMessage.setValue(25, n, IsoType.NUMERIC, 2);
	}
	/**
	 * 25
	 *
	 */
	public Integer getPOSEntryMode() {
		return Integer.parseInt(originalIsoMessage.getObjectValue(25).toString());
	}
	
	/**
	 * 26
	 *
	 */
	public void setPOSPinCaptureCode(int n) {
		originalIsoMessage.setValue(26, n, IsoType.NUMERIC, 2);
	}
	/**
	 * 26
	 *
	 */
	public Integer getPOSCaptureCode() {
		return Integer.parseInt(originalIsoMessage.getObjectValue(26).toString());
	}
	
	/**
	 * 40
	 *
	 */
	public void setServiceRestrictionCode(String code) {
		originalIsoMessage.setValue(40, code, IsoType.ALPHA, 3);
	}
	/**
	 * 40
	 *
	 */
	public Integer getServiceRestrictionCode() {
		return Integer.parseInt(originalIsoMessage.getObjectValue(40).toString());
	}
	
	/**
	 * 
	 * 48
	 */
	public void setBillingProvidertData(String data) throws Exception{
		originalIsoMessage.setValue(48, data, IsoType.LLLVAR, 32);
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
	 * 52
	 */
	public void setEncryptedPin(String data) {
		originalIsoMessage.setValue(52, data, IsoType.ALPHA, 16);
	}
	/**
	 * 
	 * 52
	 */
	public String getEncryptedPin() {
		return originalIsoMessage.getObjectValue(52).toString();
	}
	/**
	 * 
	 * 60
	 */
	public void setReservedF60Data(String data) {
		originalIsoMessage.setValue(60, data, IsoType.LLLVAR, 0);
	}
	/**
	 * 
	 * 60
	 */
	public String getReservedF60Data() {
		return originalIsoMessage.getObjectValue(60).toString();
	}

	/**
	 * 
	 * 61
	 */
	public void setXLinkPrivatRequestData(String data) {
		originalIsoMessage.setValue(61, data, IsoType.LLLVAR, 0);
	}
	/**
	 * 
	 * 61
	 */
	public String getXLinkPrivatRequestData() {
		return originalIsoMessage.getObjectValue(61).toString();
	}

	/**
	 * 
	 * 62
	 */
	public void setXLinkPrivatResponseData(String data) {
		originalIsoMessage.setValue(62, data, IsoType.LLLVAR, 0);
	}
	/**
	 * 
	 * 62
	 */
	public String getXLinkPrivatResponseData() {
		return originalIsoMessage.getObjectValue(62).toString();
	}
	
	/**
	 * 
	 * 62
	 */
	public void setTransactionResponseData(String data) {
		originalIsoMessage.setValue(62, data, IsoType.LLLVAR, 0);
	}
	/**
	 * 
	 * 62
	 */
	public String getTransactionResponseData() {
		return originalIsoMessage.getObjectValue(62).toString();
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
	public String getOriginalPaymentTransactionData() {
		return originalIsoMessage.getObjectValue(90).toString();
	}
	/**
	 * 
	 * 90
	 */
	public void setOriginalPaymentTransactionData(String data) {
		originalIsoMessage.setValue(90, data, IsoType.ALPHA, 26);
	}
	
	/**
	 * 98
	 */
	public void setPayee(String code) {
		originalIsoMessage.setValue(98, code, IsoType.ALPHA, 25);
	}
	/**
	 * 98
	 */
	public String getPayee() {
		return originalIsoMessage.getObjectValue(98).toString();
	}
	
	@Override
	public void write(OutputStream outs, int lengthBytes) throws Exception {
		// TODO Auto-generated method stub

	}
	private static final String ISOVariant = CmFinoFIX.ISO8583_Variant_XLink_Gateway_Interface;
	public String getISOVariant() {
		return ISOVariant;
	}
}
