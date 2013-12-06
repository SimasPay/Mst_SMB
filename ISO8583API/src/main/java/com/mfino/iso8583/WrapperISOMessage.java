package com.mfino.iso8583;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Date;

import org.apache.commons.lang.ArrayUtils;

import com.mfino.hibernate.Timestamp;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.IsoValue;
import com.solab.iso8583.MessageFactory;

//FIXME if we declare fields and assign iso values to them, we don't need to compute values each time one is accessed.
//FIXME add throws exceptions to all get methods
//FIXME check for the lengths when setvalue, add spaces as required
public abstract class WrapperISOMessage implements Serializable 
{

	public static MessageFactory	       msgFactory;
	protected com.solab.iso8583.IsoMessage	originalIsoMessage;
	protected int	                           etx	= -1;
	private int	                           MsgType;
	protected com.solab.iso8583.IsoMessage	element127Msg;
	
	protected WrapperISOMessage(IsoMessage isoMsg) {
		originalIsoMessage = isoMsg;
	}

	/**
	 * 2
	 * 
	 * @return
	 */
	public void setPAN(String str) {
		originalIsoMessage.setValue(2, str, IsoType.LLVAR, 0);
	}

	/**
	 * 2
	 * 
	 * @return
	 */
	public String getPAN() {
		Object panObj = originalIsoMessage.getObjectValue(2);
		return panObj.toString();
	}

	/**
	 * 3
	 * 
	 * @return
	 */
	public void setProcessingCode(Integer pCode) throws Exception{
		if(pCode>999999)
			throw new Exception("pCode cannot greater than 999999");
		originalIsoMessage.setValue(3, pCode, IsoType.NUMERIC, 6);
	}

	/**
	 * 3
	 * 
	 * @return
	 */
	public Integer getProcessingCode() {
		String str = originalIsoMessage.getObjectValue(3).toString();
		return Integer.parseInt(str);
	}

	/**
	 * 4
	 * 
	 * @return
	 */
	public void setTransactionAmount(String transactAmount) throws Exception{
		if(transactAmount.length()>12)
			throw new Exception("transactionAmount length is great than 12");
		transactAmount = padOnLeft(transactAmount, '0', 12);
		originalIsoMessage.setValue(4, transactAmount, IsoType.ALPHA, 12);
	}

	/**
	 * 
	 * 4
	 * 
	 * @return
	 */
	public String getTransactionAmount() {
		return originalIsoMessage.getObjectValue(4).toString();
	}

	/**
	 * 7
	 * 
	 * @return
	 */
	public void setTransmissionTime(Timestamp ts) {
		originalIsoMessage.setValue(7, ts, IsoType.DATE10, 0);
	}

	/**
	 * 7
	 * 
	 * @return
	 */
	public Timestamp getTransmissionTime() {
		Date d = (Date) originalIsoMessage.getObjectValue(7);
		return new Timestamp(d);
	}

	/**
	 * 11
	 * 
	 * @return
	 */
	public void setSTAN(long stan) {
		if (stan >= 1000000)
			stan = stan % 1000000;
		originalIsoMessage.setValue(11, stan, IsoType.NUMERIC, 6);
	}

	/**
	 * 11
	 * 
	 * @return
	 */
	public Long getSTAN() {
		return Long.parseLong(originalIsoMessage.getObjectValue(11).toString());
	}

	/**
	 * 12
	 * 
	 * @return
	 */
	public void setLocalTransactionTime(Timestamp ts) {
		originalIsoMessage.setValue(12, ts, IsoType.TIME, 0);
	}

	/**
	 * 12
	 * 
	 * @return
	 */
	public Timestamp getLocalTransactionTime() {
		Date d = (Date) originalIsoMessage.getObjectValue(12);
		return new Timestamp(d);
	}

	/**
	 * 13
	 * 
	 * @return
	 */
	public void setLocalTransactionDate(Timestamp ts) {
		originalIsoMessage.setValue(13, ts, IsoType.DATE4, 0);
	}

	/**
	 * 13
	 * 
	 * @return
	 */
	public Timestamp getLocalTransactionDate() {
		Date d = (Date) originalIsoMessage.getObjectValue(13);
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
	 * 18
	 * 
	 * @return
	 */
	public void setMerchantType(Integer merchantType)  throws Exception{
		if(merchantType>9999)
			throw new Exception("Merchanttype should be a 4 digit integer");
		originalIsoMessage.setValue(18, merchantType, IsoType.NUMERIC, 4);
	}

	/**
	 * 18
	 * 
	 * @return
	 */
	public Integer getMerchantType() {
		return Integer.parseInt(originalIsoMessage.getObjectValue(18).toString());
	}

	/**
	 * 24
	 * 
	 * @return
	 */
	public void setFunctionCode(Integer fc)throws Exception {
		if(fc>999)
			throw new Exception("fc should be a 3 digit integer");
		originalIsoMessage.setValue(24, fc, IsoType.NUMERIC, 3);
	}

	/**
	 * 24
	 * 
	 * @return
	 */
	public Integer getFunctionCode() {
		return Integer.parseInt(originalIsoMessage.getObjectValue(24).toString());
	}

	/**
	 * 24
	 * 
	 * @return
	 */
	public void setNetworkInternationalIdentifier(Integer fc) throws Exception{
		if(fc>999)
			throw new Exception("fc should be a 3 digit integer");
		originalIsoMessage.setValue(24, fc, IsoType.NUMERIC, 3);
	}

	/**
	 * 24
	 * 
	 * @return
	 */
	public Integer getNetworkInternationalIdentifier() {
		return Integer.parseInt(originalIsoMessage.getObjectValue(24).toString());
	}

	/**
	 * 27
	 * 
	 * @return
	 */
	public void setAuthorizingIdentificationResponseLength(Integer i)throws Exception {
		if(i>9)
			throw new Exception("responselength should be a 1 digit integer");
		originalIsoMessage.setValue(27, i, IsoType.NUMERIC, 1);
	}

	/**
	 * 27
	 * 
	 * @return
	 */
	public Integer getAuthorizingIdentificationResponseLength() {
		return Integer.parseInt(originalIsoMessage.getObjectValue(27).toString());
	}

	/**
	 * 32
	 * 
	 * @return
	 */
	public void setAcquiringInstitutionIdentificationCode(String code) {
		originalIsoMessage.setValue(32, code, IsoType.LLVAR, 0);
	}

	/**
	 * 32
	 * 
	 * @return
	 */
	public String getAcquiringInstitutionIdentificationCode() {
		return originalIsoMessage.getObjectValue(32).toString();
	}

	/**
	 * 33
	 * 
	 * @return
	 */
	public void setForwardInstitutionIdentificationCode(String code) {
		originalIsoMessage.setValue(33, code, IsoType.LLVAR, 0);
	}

	/**
	 * 33
	 * 
	 * @return
	 */
	public void setForwardInstitutionIdentificationCode(Integer code) {
		setForwardInstitutionIdentificationCode(code.toString());
	}

	/**
	 * 33
	 * 
	 * @return
	 */
	public String getForwardInstitutionIdentificationCode() {
		return originalIsoMessage.getObjectValue(33).toString();
	}

	/**
	 * 35
	 * 
	 * @return
	 */
	public void setTrack2Data(String data) {
		originalIsoMessage.setValue(35, data, IsoType.LLVAR, 0);
	}

	/**
	 * 35
	 * 
	 * @return
	 */
	public String getTrack2Data() {
		return originalIsoMessage.getObjectValue(35).toString();
	}

	/**
	 * 37
	 * 
	 * @return
	 */
	public void setRRN(String rrn) throws Exception{
		if(rrn.length()>12)
			throw new Exception("rrn length should be <=12");
		rrn = padOnLeft(rrn, '0', 12);
		originalIsoMessage.setValue(37, rrn, IsoType.ALPHA, 12);
	}

	/**
	 * 37
	 * 
	 * @return
	 */
	public String getRRN() {
		return originalIsoMessage.getObjectValue(37).toString();
	}

	/**
	 * 38
	 * 
	 * @return
	 */
	public void setAuthorizationIdentificationResponse(String response) throws Exception{
		if(response.length()!=6)
			throw new Exception("response should be a string of length 6");
		originalIsoMessage.setValue(38, response, IsoType.ALPHA, 6);
	}

	/**
	 * 38
	 * 
	 * @return
	 */
	public String getAuthorizationIdentificationResponse() {
		return originalIsoMessage.getObjectValue(38).toString();
	}

	/**
	 * 39
	 * 
	 * @return
	 */
	public void setResponseCode(String code) throws Exception{
		if(code.length()!=2)
			throw new Exception("code should be a string of length 2");
		originalIsoMessage.setValue(39, code, IsoType.ALPHA, 2);
	}

	/**
	 * 39
	 * 
	 * @return
	 */
	public String getResponseCode() {
		return originalIsoMessage.getObjectValue(39).toString();
	}

	/**
	 * 41
	 * 
	 * @return
	 */
	public void setCardAcceptorTerminalIdentification(String identifier) throws Exception{
		if(identifier.length()>8)
			throw new Exception("identifier length should be <=8");
		identifier = padOnRight(identifier, ' ', 8);
		originalIsoMessage.setValue(41, identifier, IsoType.ALPHA, 8);
	}

	/**
	 * 41
	 * 
	 * @return
	 */
	public String getCardAcceptorTerminalIdentification() {
		return originalIsoMessage.getObjectValue(41).toString();
	}

	/**
	 * 42
	 * 
	 * @return
	 */
	public void setCardAcceptorIdentificationCode(String code) throws Exception{
		if(code.length()>15)
			throw new Exception("code length should be <=15");
		code = padOnRight(code, ' ', 15);
		originalIsoMessage.setValue(42, code, IsoType.ALPHA, 15);
	}

	/**
	 * 42
	 * 
	 * @return
	 */
	public String getCardAcceptorIdentificationCode() {
		return originalIsoMessage.getObjectValue(42).toString();
	}

	/**
	 * 43
	 * 
	 * @return
	 */
	public void setCardAcceptorNameLocation(String code) throws Exception{
		code = code.trim();
		if(code.length()>40)
			throw new Exception("code length should be <=40");
		code = padOnRight(code, ' ', 40);
		originalIsoMessage.setValue(43, code, IsoType.ALPHA, 40);
	}

	/**
	 * 43
	 * 
	 * @return
	 */
	public String getCardAcceptorNameLocation() {
		return originalIsoMessage.getObjectValue(43).toString();
	}

	/**
	 * 49
	 * 
	 * @return
	 */
	public String getTransactionCurrencyCode() {
		return originalIsoMessage.getObjectValue(49).toString();
	}

	/**
	 * 49
	 * 
	 * @return
	 */
	public void setTransactionCurrencyCode(int CurrencyCode) throws Exception{
		if(CurrencyCode>999)
			throw new Exception("currencycode cannot be greater than 999");
		originalIsoMessage.setValue(49, CurrencyCode, IsoType.NUMERIC, 3);
	}

	/**
	 * 70
	 * 
	 * @return
	 */
	public void setNetworkManagementInformationCode(Integer code)throws Exception {
		if(code>999)
			throw new Exception("code cannot be greater than 999");
		originalIsoMessage.setValue(70, code, IsoType.NUMERIC, 3);
	}

	/**
	 * 70
	 * 
	 * @return
	 */
	public Integer getNetworkManagementInformationCode() {
		return Integer.parseInt(originalIsoMessage.getObjectValue(70).toString());
	}

	/**
	 * 102
	 * 
	 * @return
	 */
	public void setAccountIdentification1(String ide) {
		originalIsoMessage.setValue(102, ide, IsoType.LLVAR, 0);
	}

	/**
	 * 102
	 * 
	 * @return
	 */public String getAccountIdentification1() {
		return originalIsoMessage.getObjectValue(102).toString();
	}

	/**
	 * 103
	 * 
	 * @return
	 */
	public void setAccountIdentification2(String ide) {
		originalIsoMessage.setValue(103, ide, IsoType.LLVAR, 0);
	}

	/**
	 * 103
	 * 
	 * @return
	 */
	public String getAccountIdentification2() {
		return originalIsoMessage.getObjectValue(103).toString();
	}

	public int getMessageType() {
		return MsgType;
	}

	public void setForceSecondaryBitmap(boolean flag) {
		originalIsoMessage.setForceSecondaryBitmap(flag);
	}

	public boolean getForceSecondaryBitmap() {
		return originalIsoMessage.getForceSecondaryBitmap();
	}

	public void setCharacterEncoding(String value) {
		originalIsoMessage.setCharacterEncoding(value);
	}

	public String getCharacterEncoding() {
		return originalIsoMessage.getCharacterEncoding();
	}

	public void setIsoHeader(String value) {
		originalIsoMessage.setIsoHeader(value);
	}

	public String getIsoHeader() {
		return originalIsoMessage.getIsoHeader();
	}

	public void setType(int value) {
		originalIsoMessage.setType(value);
	}

	public int getType() {
		return originalIsoMessage.getType();
	}

	public void setBinary(boolean flag) {
		originalIsoMessage.setBinary(flag);
	}

	public boolean isBinary() {
		return originalIsoMessage.isBinary();
	}

	public void setEtx(int value) {
		etx = value;
		originalIsoMessage.setEtx(value);
	}

	public Object getObjectValue(int field) {
		return originalIsoMessage.getObjectValue(field);
	}

	public IsoValue<?> getField(int field) {
		return originalIsoMessage.getField(field);
	}

//	public void setField(int index, IsoValue<?> field) {
//		originalIsoMessage.setField(index, field);
//	}

	/*
	 * public void setValue(int index, Object value, IsoType t, int length) {
	 * OriginalIsoMessage.setValue(index, value, t, length); }
	 */
/*	public void setValue(int index, Object value, CustomField<?> encoder, IsoType t, int length) {
		originalIsoMessage.setValue(index, value, encoder, t, length);
	}
*/
	public boolean hasField(int idx) {
		return originalIsoMessage.hasField(idx);
	}

	public abstract void write(OutputStream outs, int lengthBytes) throws Exception ;

	public ByteBuffer writeToBuffer(int lengthBytes) {
		return originalIsoMessage.writeToBuffer(lengthBytes);
	}

	public byte[] writeData() {
		return originalIsoMessage.writeData();
	}

	public void putAt(int i, IsoValue<?> v) {
		originalIsoMessage.putAt(i, v);
	}

	public IsoValue<?> getAt(int i) {
		return originalIsoMessage.getAt(i);
	}

	public void copyFieldsFrom(com.solab.iso8583.IsoMessage src, int... idx) {
		originalIsoMessage.copyFieldsFrom(src, idx);
	}
	public static String padOnLeft(String str,char paddingChar,int finalLength)throws Exception {
		if(finalLength==str.length())
			return str;
		if(finalLength<str.length())
			throw new Exception("String length is already greater than the final length");
		String s = "";
		for(int i=0;i<finalLength-str.length();i++) 
			s = s +String.valueOf(paddingChar);
		str = s+str;
		return str;
	}
	public static String padOnRight(String str,char paddingChar,int finalLength) throws Exception{
		if(finalLength==str.length())
			return str;
		if(finalLength<str.length())
			throw new Exception("String length is already greater than the final length");
		String s = "";
		for(int i=0;i<finalLength-str.length();i++) 
			s = s+String.valueOf(paddingChar);
		str = str+s;
		return str;
	}
	
	public String toString() {
		byte[] byteData = writeData();
		String str = new String(byteData);
		return str;
	}
	
	public abstract String getISOVariant();
	public byte[] write(int noOfBytesInLength) {
		if (noOfBytesInLength > 4) {
			throw new IllegalArgumentException("The length header can have at most 4 bytes");
		}
		byte[] data = originalIsoMessage.writeData();
		byte[] lenBytes = null;
		if (noOfBytesInLength > 0) {
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
			lenBytes=str.getBytes();

		}
		
		byte[] finalBytes = ArrayUtils.addAll(lenBytes, data);
		
		return finalBytes;
	}

}