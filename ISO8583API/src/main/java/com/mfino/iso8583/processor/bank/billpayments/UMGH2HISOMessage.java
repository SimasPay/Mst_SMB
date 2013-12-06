package com.mfino.iso8583.processor.bank.billpayments;

import java.io.OutputStream;
import java.util.Date;

import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.WrapperISOMessage;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;

public class UMGH2HISOMessage extends WrapperISOMessage {

	public UMGH2HISOMessage(IsoMessage msg) {
		super(msg);
	}
	
	
	/**
	 * 14
	 * @param ts
	 */
	public void setExpirationDate(Timestamp ts) {
		originalIsoMessage.setValue(14, ts, IsoType.DATE4, 0);
	}
	/**
	 * 14
	 * @param ts
	 */
	public Timestamp getExpirationDate() {
		Date d = (Date) originalIsoMessage.getObjectValue(14);
		return new Timestamp(d);
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
	 * 24
	 * 
	 * @return
	 */
	public void setNetworkIdentificationId(Integer fc) {
		originalIsoMessage.setValue(24, fc, IsoType.NUMERIC, 3);
	}

	/**
	 * 24
	 * 
	 * @return
	 */
	public Integer getNetworkIdentificationID() {
		return Integer.parseInt(originalIsoMessage.getObjectValue(24).toString());
	}
	
	/**
	 * 25
	 */
	public void setPointOfServiceConditionCode(int code) {
		originalIsoMessage.setValue(25, code, IsoType.NUMERIC, 2);
	}
	/**
	 * 25
	 */
	public Integer getPointOfSericeConditionCode() {
		return Integer.parseInt(originalIsoMessage.getObjectValue(25).toString());
	}
	
	/**
	 * 26
	 */
	public void setPOSPinCaptureCode(int code) {
		originalIsoMessage.setValue(26, code, IsoType.NUMERIC, 2);
	}
	/**
	 * 26
	 */
	public Integer getPOSPinCaptureCode() {
		return Integer.parseInt(originalIsoMessage.getObjectValue(26).toString());
	}
	
	/**
	 * 27
	 */
	public void setAutherizationInstitutionIdentificationCode(int code) {
		originalIsoMessage.setValue(27, code, IsoType.NUMERIC, 1);
	}
	/**
	 * 27
	 */
	public Integer getAutherizationInstitutionIdentificationCode() {
		return Integer.parseInt(originalIsoMessage.getObjectValue(27).toString());
	}

	/**
	 * 34
	 */
	public void setPANExtendedCountryCode(String code) {
		originalIsoMessage.setValue(34, code, IsoType.LLVAR, 0);
	}
	/**
	 * 34
	 */
	public String getPANExtendedCountryCode() {
		return originalIsoMessage.getObjectValue(34).toString();
	}
	
	/**
	 * 36
	 */
	public void setTract3Data(String code) {
		originalIsoMessage.setValue(36, code, IsoType.LLLVAR, 0);
	}
	/**
	 * 36
	 */
	public String getTract3Data() {
		return originalIsoMessage.getObjectValue(36).toString();
	}
	
	/**
	 * 40
	 */
	public void setServiceRestrictionCode(String code) {
		originalIsoMessage.setValue(40, code, IsoType.ALPHA, 3);
	}
	/**
	 * 40
	 */
	public String getServiceRestrictionCode() {
		return originalIsoMessage.getObjectValue(40).toString();
	}
	
	/**
	 * 
	 * 48
	 */
	public String getEncryptedWorkingKey() {
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
	public void setTransactionRequestData(String data) {
		originalIsoMessage.setValue(61, data, IsoType.LLLVAR, 0);
	}
	/**
	 * 
	 * 61
	 */
	public String getTransactionRequestData() {
		return originalIsoMessage.getObjectValue(61).toString();
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
	 * 62
	 */
	public void setTransactionResponseData(String data) {
		originalIsoMessage.setValue(62, data, IsoType.LLLVAR, 0);
	}
	
	/**
	 * 
	 * 63
	 */
	public void setTransactionFee(String data) {
		originalIsoMessage.setValue(63, data, IsoType.LLLVAR, 0);
	}
	/**
	 * 
	 * 63
	 */
	public String getTransactionFee() {
		return originalIsoMessage.getObjectValue(63).toString();
	}
	
	/**
	 * 
	 * 63
	 */
	public void setOriginalPaymentTransactionData(String data) {
		originalIsoMessage.setValue(90, data, IsoType.ALPHA, 42);
	}
	/**
	 * 
	 * 63
	 */
	public String getOriginalPaymentTransactionData() {
		return originalIsoMessage.getObjectValue(90).toString();
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
	/**
	 * 98
	 */
	public void setBillPayee(String code) {
		setPayee(code);
	}
	/**
	 * 98
	 */
	public String getBillPayee() {
		return getPayee();
	}
	
	@Override
	public void write(OutputStream outs, int lengthBytes) throws Exception {
		// TODO Auto-generated method stub

	}
	private static final String ISOVariant = CmFinoFIX.ISO8583_Variant_Bank_BillPayments_Gateway_Interface;
	public String getISOVariant() {
		return ISOVariant;
	}
}	
