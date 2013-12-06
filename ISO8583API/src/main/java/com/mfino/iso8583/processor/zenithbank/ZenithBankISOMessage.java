package com.mfino.iso8583.processor.zenithbank;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Date;

import org.apache.commons.lang.ArrayUtils;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.WrapperISOMessage;
import com.solab.iso8583.IsoType;

public class ZenithBankISOMessage extends WrapperISOMessage {

	public ZenithBankISOMessage(com.solab.iso8583.IsoMessage isoMsg) {
		super(isoMsg);
	}
	
	/**
	 * 5
	 * @return
	 */
	public void setSettlementAmount(String settlementAmount) throws Exception {
		if(settlementAmount.length()>12)
			throw new Exception("SettlementAmount length is great than 12");
		settlementAmount = padOnLeft(settlementAmount, '0', 12);
		originalIsoMessage.setValue(5, settlementAmount, IsoType.ALPHA, 12);
	}
	
	/**
	 * 
	 * 5
	 * 
	 * @return
	 */
	public String getSettlementAmount() {
		return originalIsoMessage.getObjectValue(5).toString();
	}
	
	/**
	 * 9
	 * @return
	 */
	public void setSettlementConversionRate(String SettlementConversionRate) throws Exception {
		if(SettlementConversionRate.length()>8)
			throw new Exception("SettlementConversionRate length is great than 8");
		SettlementConversionRate = padOnLeft(SettlementConversionRate, '0', 8);
		originalIsoMessage.setValue(9, SettlementConversionRate, IsoType.ALPHA, 8);
	}
	
	/**
	 * 
	 * 9
	 * 
	 * @return
	 */
	public String getSettlementConversionRate() {
		return originalIsoMessage.getObjectValue(9).toString();
	}
	
	/**
	 * 14
	 * 
	 * @return
	 */
	public void setExpirationDate(Timestamp ts) {
		originalIsoMessage.setValue(14, ts, IsoType.DATE4, 0);
	}

	/**
	 * 14
	 * 
	 * @return
	 */
	public Timestamp getExpirationDate() {
		Date d = (Date) originalIsoMessage.getObjectValue(14);
		return new Timestamp(d);
	}

	/**
	 * 15
	 * 
	 * @return
	 */
	public void setSettlementDate(Timestamp ts) {
		originalIsoMessage.setValue(15, ts, IsoType.DATE4, 0);
	}

	/**
	 * 15
	 * 
	 * @return
	 */
	public Timestamp getSettlementDate() {
		Date d = (Date) originalIsoMessage.getObjectValue(15);
		return new Timestamp(d);
	}

	/**
	 * 16
	 * 
	 * @return
	 */
	public void setConversionDate(Timestamp ts) {
		originalIsoMessage.setValue(16, ts, IsoType.DATE4, 0);
	}

	/**
	 * 16
	 * 
	 * @return
	 */
	public Timestamp getConversionDate() {
		Date d = (Date) originalIsoMessage.getObjectValue(16);
		return new Timestamp(d);
	}
	
	
	/**
	 * 22
	 * 
	 * @return
	 */
	public void setPOSEntryMode(Integer posEntryMode)  throws Exception{
		if(posEntryMode>999)
			throw new Exception("POS Entry Mode should be a 3 digit integer");
		originalIsoMessage.setValue(22, posEntryMode, IsoType.NUMERIC, 3);
	}

	/**
	 * 22
	 * 
	 * @return
	 */
	public Integer getPOSEntryMode() {
		return Integer.parseInt(originalIsoMessage.getObjectValue(22).toString());
	}
	
	/**
	 * 23
	 * 
	 * @return
	 */
	public void setCardSequenceNumber(Integer cardSequenceNumber)  throws Exception{
		if(cardSequenceNumber>999)
			throw new Exception("Card Sequence Number should be a 3 digit integer");
		originalIsoMessage.setValue(23, cardSequenceNumber, IsoType.NUMERIC, 3);
	}

	/**
	 * 23
	 * 
	 * @return
	 */
	public Integer getCardSequenceNumber() {
		return Integer.parseInt(originalIsoMessage.getObjectValue(23).toString());
	}
	
	/**
	 * 25
	 * 
	 * @return
	 */
	public void setPOSConditionCode(Integer posConditionCode)  throws Exception{
		if(posConditionCode>99)
			throw new Exception("POS Condition Code should be a 2 digit integer");
		originalIsoMessage.setValue(25, posConditionCode, IsoType.NUMERIC, 2);
	}

	/**
	 * 25
	 * 
	 * @return
	 */
	public Integer getPOSConditionCode() {
		return Integer.parseInt(originalIsoMessage.getObjectValue(25).toString());
	}
	
	
	/**
	 * 26
	 * 
	 * @return
	 */
	public void setPOSPINCaptureCode(Integer posPINCaptureCode)  throws Exception{
		if(posPINCaptureCode>99)
			throw new Exception("POS PIN Capture Code should be a 2 digit integer");
		originalIsoMessage.setValue(26, posPINCaptureCode, IsoType.NUMERIC, 2);
	}

	/**
	 * 26
	 * 
	 * @return
	 */
	public Integer getPOSPINCaptureCode() {
		return Integer.parseInt(originalIsoMessage.getObjectValue(26).toString());
	}
	
	
	/**
	 * 28,29,30,31 Types and lengths to be clarified
	 */
	
	/**
	 * 
	 */
	
	/**
	 * 28
	 * @return
	 */
	public void setTransactionAmountFee(String transactionAmountFee,Integer amountSign ) throws Exception {
		if(transactionAmountFee.length()>9)
			throw new Exception("TransactionAmountFee length is great than 9");
		if(amountSign == 1)
			transactionAmountFee = padOnLeft(transactionAmountFee, 'C', 1);
		else
			transactionAmountFee = padOnLeft(transactionAmountFee, 'D', 1);
		transactionAmountFee = padOnRight(transactionAmountFee, '0', 8);
		originalIsoMessage.setValue(28, transactionAmountFee, IsoType.ALPHA, 9);
	}
	
	/**
	 * 
	 * 28
	 * 
	 * @return
	 */
	public String getTransactionAmountFee() {
		return originalIsoMessage.getObjectValue(28).toString();
	}
	
	/**
	 * 29
	 * @return
	 */
	public void setSettlementAmountFee(String settlementAmountFee,Integer amountSign) throws Exception {
		if(settlementAmountFee.length()>9)
			throw new Exception("SettlementAmountFee length is great than 9");
		if(amountSign == 1)
			settlementAmountFee = padOnLeft(settlementAmountFee, 'C', 1);
		else
			settlementAmountFee = padOnLeft(settlementAmountFee, 'D', 1);
		settlementAmountFee = padOnRight(settlementAmountFee, '0', 8);
		originalIsoMessage.setValue(29, settlementAmountFee, IsoType.ALPHA, 9);
	}
	
	/**
	 * 
	 * 29
	 * 
	 * @return
	 */
	public String getSettlementAmountFee() {
		return originalIsoMessage.getObjectValue(29).toString();
	}
	
	/**
	 * 30
	 * @return
	 */
	public void setTransactionProcessingFee(String transactionProcessingFee,Integer amountSign) throws Exception {
		if(transactionProcessingFee.length()>9)
			throw new Exception("TransactionProcessingFee length is great than 9");
		if(amountSign == 1)
			transactionProcessingFee = padOnLeft(transactionProcessingFee, 'C', 1);
		else
			transactionProcessingFee = padOnLeft(transactionProcessingFee, 'D', 1);
		transactionProcessingFee = padOnRight(transactionProcessingFee, '0', 8);
		originalIsoMessage.setValue(30, transactionProcessingFee, IsoType.ALPHA, 9);
	}
	
	/**
	 * 
	 * 30
	 * 
	 * @return
	 */
	public String getTransactionProcessingFee() {
		return originalIsoMessage.getObjectValue(30).toString();
	}
	
	/**
	 * 31
	 * @return
	 */
	public void setSettlementProcessingFee(String settlementProcessingFee,Integer amountSign) throws Exception {
		if(settlementProcessingFee.length()>9)
			throw new Exception("SettlementProcessingFee length is great than 9");
		if(amountSign == 1)
			settlementProcessingFee = padOnLeft(settlementProcessingFee, 'C', 1);
		else
			settlementProcessingFee = padOnLeft(settlementProcessingFee, 'D', 1);
		settlementProcessingFee = padOnRight(settlementProcessingFee, '0', 8);
		originalIsoMessage.setValue(31, settlementProcessingFee, IsoType.ALPHA, 9);
	}
	
	/**
	 * 
	 * 31
	 * 
	 * @return
	 */
	public String getSettlementProcessingFee() {
		return originalIsoMessage.getObjectValue(31).toString();
	}
	
	/**
	 * 40
	 * 
	 * @return
	 */
	public void setServiceRestrictionCode(Integer serviceRestrictioncode)  throws Exception{
		if(serviceRestrictioncode>999)
			throw new Exception("service Restriction code should be a 3 digit integer");
		originalIsoMessage.setValue(40, serviceRestrictioncode, IsoType.NUMERIC, 3);
	}

	/**
	 * 40
	 * 
	 * @return
	 */
	public Integer getServiceRestrictionCode() {
		return Integer.parseInt(originalIsoMessage.getObjectValue(40).toString());
	}
	
	/**
	 * 45
	 * 
	 * @return
	 */
	public void setTrack1Data(String track2Data) {
		originalIsoMessage.setValue(45, track2Data, IsoType.LLVAR, 0);
	}

	/**
	 * 45
	 * 
	 * @return
	 */
	public String getTrack1Data() {
		return originalIsoMessage.getObjectValue(45).toString();
	}
	
	/**
	 * 50
	 * 
	 * @return
	 */
	public void setCurrencyCodeSettlement(Integer currencyCodeSettlement)  throws Exception{
		if(currencyCodeSettlement>999)
			throw new Exception("service Restriction code should be a 3 digit integer");
		originalIsoMessage.setValue(50, currencyCodeSettlement, IsoType.NUMERIC, 3);
	}

	/**
	 * 50
	 * 
	 * @return
	 */
	public Integer getCurrencyCodeSettlement() {
		return Integer.parseInt(originalIsoMessage.getObjectValue(50).toString());
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
	 * 53
	 */
	public void setSecurityControlInfo(String data) throws Exception{
		if(data.length()!=48)
			throw new Exception("data length should be 48");
		originalIsoMessage.setValue(53, data, IsoType.ALPHA, 48);
	}
	/**
	 * 
	 * 53
	 */
	public String getSecurityControlInfo() {
		return originalIsoMessage.getObjectValue(53).toString();
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
	 * 56
	 */
	public void setMessageReasonCode(String reasonCode) {
		originalIsoMessage.setValue(56, reasonCode, IsoType.LLLVAR, 0);
	}
	/**
	 * 
	 * 56
	 */
	public String getMessageReasonCode() {
		return originalIsoMessage.getObjectValue(56).toString();
	}
	
	/**
	 * 
	 * 57
	 */
	public void setAuthorizationCode(String authorizationCode) {
		originalIsoMessage.setValue(57, authorizationCode, IsoType.LLLVAR, 0);
	}
	/**
	 * 
	 * 57
	 */
	public String getAuthorizationCodeCode() {
		return originalIsoMessage.getObjectValue(57).toString();
	}
	
	/**
	 * 
	 * 59
	 */
	public void setTEchoData(String echoData) {
		originalIsoMessage.setValue(59, echoData, IsoType.LLLVAR, 0);
	}
	/**
	 * 
	 * 59
	 */
	public String getTEchoData() {
		return originalIsoMessage.getObjectValue(59).toString();
	}
	
	/**
	 * 67
	 * 
	 * @return
	 */
	public void setExtendedPaymentCode(Integer extendedPaymentCode)  throws Exception{
		if(extendedPaymentCode>99)
			throw new Exception("POS PIN Capture Code should be a 2 digit integer");
		originalIsoMessage.setValue(67, extendedPaymentCode, IsoType.NUMERIC, 2);
	}

	/**
	 * 67
	 * 
	 * @return
	 */
	public Integer getExtendedPaymentCode() {
		return Integer.parseInt(originalIsoMessage.getObjectValue(67).toString());
	}

	
	/**
	 * This value is taken as a String instead of a Number.
	 * 42 digits are too big to handle for long and if BigInteger/Decimal
	 * is used ,the code of com.sloab.iso8583.IsoValue needs to be changed.
	 * 90
	 */
	public void setReversalInfo(String reversalInfo) throws Exception{
		if(reversalInfo.length()!=42)
			throw new Exception("data length should be 42");
		originalIsoMessage.setValue(90, reversalInfo, IsoType.ALPHA, 42);
	}
	/**
	 * 
	 * 90
	 */
	public String getReversalInfo() {
		return originalIsoMessage.getObjectValue(90).toString();
	}
	
	/**
	 * 
	 * 95
	 */
	public void setReplacementAmounts(String data) throws Exception{
		if(data.length()!=42)
			throw new Exception("data length should be 42");
		originalIsoMessage.setValue(95, data, IsoType.ALPHA, 42);
	}
	/**
	 * 
	 * 95
	 */
	public String getReplacementAmounts() {
		return originalIsoMessage.getObjectValue(95).toString();
	}
	
	/**
	 * 
	 * 98
	 */
	public void setPayee(String payee) throws Exception{
		if(payee.length()!=25)
			throw new Exception("data length should be 25");
		originalIsoMessage.setValue(98, payee, IsoType.ALPHA, 25);
	}
	/**
	 * 
	 * 98
	 */
	public String getPayee() {
		return originalIsoMessage.getObjectValue(98).toString();
	}
	
	/**
	 * 100
	 * 
	 * @return
	 */
	public void setReceivingInstitutionIDCode(String code) {
		originalIsoMessage.setValue(100, code, IsoType.LLVAR, 0);
	}

	/**
	 * 100
	 * 
	 * @return
	 */
	public String getReceivingInstitutionIDCode() {
		return originalIsoMessage.getObjectValue(100).toString();
	}
	
	/**
	 * 
	 * 123
	 */
	public void setPOSDataCode(String dataCode) {
		originalIsoMessage.setValue(123, dataCode, IsoType.LLLVAR, 0);
	}
	/**
	 * 
	 * 123
	 */
	public String getPOSDataCode() {
		return originalIsoMessage.getObjectValue(123).toString();
	}
	
	/**
	 * 
	 * 125
	 */
	public void setNetworkManagementInfo(String dataCode) {
		originalIsoMessage.setValue(125, dataCode, IsoType.LLLVAR, 0);
	}
	/**
	 * 
	 * 125
	 */
	public String getNetworkManagementInfo() {
		return originalIsoMessage.getObjectValue(125).toString();
	}
	
	/**
	 * 
	 * 127
	 */
	public void set127ElementData(String data127) {
		originalIsoMessage.setValue(127, data127, IsoType.LLLVAR, 0);
	}
	/**
	 * 
	 * 127
	 */
	public String get127ElementData() {
		return originalIsoMessage.getObjectValue(127).toString();
	}
	

	/**
	 * 
	 * 127.3
	 */
	public void set127Element_3(String data3) throws Exception{
		if(data3.length() != 48)
			throw new Exception("127.3 Routing info data length should be 48");
		element127Msg.setValue(3, data3, IsoType.ALPHA, 48);
	}
	/**
	 * 
	 * 127.3
	 */
	public String get127Element_3() {
		return element127Msg.getObjectValue(3).toString();
	}
	
	/**
	 * 
	 * 127.6
	 */
	public void set127Element_6(Integer data6) throws Exception {
		if(data6 > 99)
			throw new Exception("127.6 Authorization profile should be a 2 digit integer");
		element127Msg.setValue(6, data6, IsoType.NUMERIC, 2);
	}
	/**
	 * 
	 * 127.6
	 */
	public Integer get127Element_6() {
		return Integer.parseInt(element127Msg.getObjectValue(6).toString());
	}
	
	/**
	 * 
	 * 127.19
	 */
	public void set127Element_19(String data19) throws Exception {
		if(data19.length() != 31)
			throw new Exception("127.19 Bank Details data length should be 31");
		element127Msg.setValue(19, data19, IsoType.ALPHA, 31);
	}
	/**
	 * 
	 * 127.19
	 */
	public String get127Element_19() {
		return element127Msg.getObjectValue(19).toString();
	}
	
	/**
	 * 
	 * 127.20
	 */
	public void set127Element_20(String data20) throws Exception {
		if(data20.length()>12)
			throw new Exception("127.20 Authorizer date length is great than 8");
		element127Msg.setValue(20, data20, IsoType.ALPHA, 8);
	}
	/**
	 * 
	 * 127.20
	 */
	public String get127Element_20() {
		return element127Msg.getObjectValue(20).toString();
	}
	
	/**
	 * 
	 * 128
	 * @throws Exception 
	 */
	public void setMACExtended(String MACCode) throws Exception {
		if(MACCode.length()!=16)
			throw new Exception("data length should be 16");
		originalIsoMessage.setValue(128, MACCode, IsoType.ALPHA, 16);
	}
	/**
	 * 
	 * 128
	 */
	public String getMACExtended() {
		return originalIsoMessage.getObjectValue(128).toString();
	}
	
	@Override
	public void write(OutputStream outs, int lengthBytes) throws Exception {
		byte[] data = write(lengthBytes);
		outs.write(data);
		outs.flush();
	}
	
	private static final String ISOVariant = CmFinoFIX.ISO8583_Variant_Zenith_Bank_Interface;
	
	@Override
	public String getISOVariant() {
		return ISOVariant;
	}	
	
	@Override
	public byte[] write(int noOfBytesInLength) {
		if (noOfBytesInLength != 2) {
			throw new IllegalArgumentException("noOfBytesInLength should be 2");
		}
		byte[] data = originalIsoMessage.writeData();
		byte[] lenBytes = new byte[2];
		if (noOfBytesInLength > 0) {
			int l = data.length;
			lenBytes[0] = (byte)(l/256);
			lenBytes[1] = (byte)(l%256);
		}
		byte[] finalBytes = ArrayUtils.addAll(lenBytes, data);
		return finalBytes;
	}

}
