package com.mfino.iso8583.processor.mobileoperator;

import java.io.OutputStream;
import java.math.BigInteger;

import com.mfino.fix.CmFinoFIX;
import com.mfino.iso8583.WrapperISOMessage;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;

public class MobileOperatorISOMessage extends WrapperISOMessage {

	public MobileOperatorISOMessage(IsoMessage isoMsg) {
		super(isoMsg);
	}

	public void setCollectionAgentCode(String code) {
		originalIsoMessage.setValue(32, code, IsoType.LLVAR, 0);
	}

	public String getCollectionAgentCode() {
		return originalIsoMessage.getObjectValue(32).toString();
	}

	public void setProductIndicator(String data) {
		originalIsoMessage.setValue(48, data, IsoType.NUMERIC, 4);
	}

	public String getProductIndicator() {
		return originalIsoMessage.getObjectValue(48).toString();
	}

	public void setTransactionRequestData(String data) {
		originalIsoMessage.setValue(61, data, IsoType.LLLVAR, 0);
	}

	public String getTransactionRequestData() {
		return originalIsoMessage.getObjectValue(61).toString();
	}

	public void setTransactionResponseData(String data) {
		originalIsoMessage.setValue(62, data, IsoType.LLLVAR, 0);
	}

	public String getTransactionResponseData() {
		return originalIsoMessage.getObjectValue(62).toString();
	}

	public void setInstitutionCode(String code) {
		originalIsoMessage.setValue(63, code, IsoType.LLLVAR, 0);
	}

	public String getInstitutionCode() {
		return originalIsoMessage.getObjectValue(63).toString();
	}

	public void setOriginalPaymentTransactionData(String data) {
		originalIsoMessage.setValue(90, data, IsoType.ALPHA, 42);
	}

	public String getOriginalPaymentTransactionData() {
		return originalIsoMessage.getObjectValue(90).toString();
	}

	@Override
    public void write(OutputStream outs, int lengthBytes) throws Exception {
	    // TODO Auto-generated method stub
	    
    }
	private static final String ISOVariant = CmFinoFIX.ISO8583_Variant_Mobile_Operator_Gateway_Interface;
	public String getISOVariant() {
		return ISOVariant;
	}
}
