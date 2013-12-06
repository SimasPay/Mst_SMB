package com.mfino.iso8583.processor.bank;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import com.mfino.fix.CmFinoFIX;
import com.mfino.iso8583.WrapperISOMessage;
import com.solab.iso8583.IsoType;

public class SinarmasISOMessage extends WrapperISOMessage {

	public SinarmasISOMessage(com.solab.iso8583.IsoMessage isoMsg) {
		super(isoMsg);
	}

	/**
	 * 
	 * 47
	 */
	public void setPrivateTransactionID(String tranID) {
		originalIsoMessage.setValue(47, tranID, IsoType.LLLVAR, 0);
	}
	/**
	 * 
	 * 47
	 */
	public String getPrivateTransactionID() {
		return originalIsoMessage.getObjectValue(47).toString();
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
	 * 48
	 */
	public void setProductorIndicator(String data) throws Exception{
		originalIsoMessage.setValue(48, data, IsoType.LLLVAR, 0);
	}
	/**
	 * 
	 * 48
	 */
	public String getProductIndicator() {
		return originalIsoMessage.getObjectValue(48).toString();
	}
	/**
	 * 
	 * 48
	 */
	public void setEncryptedPinBlocks(String data) throws Exception{
		originalIsoMessage.setValue(48, data, IsoType.LLLVAR, 0);
	}
	/**
	 * 
	 * 48
	 */
	public String getEncryptedPinBlocks() {
		return originalIsoMessage.getObjectValue(48).toString();
	}
	/**
	 * 
	 * 48
	 */
	public String getSianrmasBankAccountName() {
		return originalIsoMessage.getObjectValue(48).toString();
	}
	/**
	 * 
	 * 52
	 */
	public void setEncryptedPin(String data) throws Exception{
		if(data.length()!=16)
			throw new Exception("data length should be 16");
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
	 * 54
	 */
	public void setAdditionalAmounts(String amounts) {
		originalIsoMessage.setValue(54, amounts, IsoType.LLLVAR, 0);
	}
	/**
	 * 
	 * 54
	 */
	public String getAdditionalAmounts() {
		return originalIsoMessage.getObjectValue(54).toString();
	}
	/**
	 * 
	 * 61
	 */
	public String getLastTransactions() {
		return originalIsoMessage.getObjectValue(61).toString();
	}
	
	/**
	 * Do not use this method unless for a bank mock/simulator.
	 * 61
	 */
	@Deprecated
	public void setLastTransactions(String str) {
		originalIsoMessage.setValue(61,str,IsoType.LLLVAR,0);
	}
	/**
	 * This value is taken as a String instead of a Number.
	 * 26 digits are too big to handle for long and if BigInteger/Decimal
	 * is used ,the code of com.sloab.iso8583.IsoValue needs to be changed.
	 * 90
	 */
	public void setSinarmasReversalInfo(String reversalInfo) throws Exception{
		if(reversalInfo.length()!=26)
			throw new Exception("data length should be 26");
		originalIsoMessage.setValue(90, reversalInfo, IsoType.ALPHA, 26);
	}
	/**
	 * 
	 * 90
	 */
	public String getSinarmasReversalInfo() {
		return originalIsoMessage.getObjectValue(90).toString();
	}
	public void write(OutputStream outs, int lengthBytes) throws Exception {

		if (lengthBytes > 4) {
			throw new IllegalArgumentException("The length header can have at most 4 bytes");
		}
		byte[] data = originalIsoMessage.writeData();
		if (lengthBytes > 0) {
			int l = data.length;
			if (etx > -1) {
				l++;
			}
			Integer i = l;
			String str = i.toString();
			if (str.length() == 1)
				str = "000" + str;
			if (str.length() == 2)
				str = "00" + str;
			if (str.length() == 3)
				str = "0" + str;
			outs.write(str.getBytes());

		}
		outs.write(data);
		// ETX
		if (etx > -1) {
			outs.write(etx);
		}
		outs.flush();
	}

	private static final String ISOVariant = CmFinoFIX.ISO8583_Variant_Sinarmas_Bank_Interface;
	public String getISOVariant() {
		return ISOVariant;
	}	
}
