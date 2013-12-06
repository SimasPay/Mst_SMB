package com.mfino.gt.iso8583.processor;

import org.jpos.iso.ISOMsg;

import com.mfino.fix.CmFinoFIX;

public class GTBankISOMessage {

	public GTBankISOMessage(ISOMsg isoMsg) {
		//super(isoMsg);
	}
	private static final String ISOVariant = CmFinoFIX.ISO8583_Variant_GT_Bank_Interface;
	public String getISOVariant() {
		return ISOVariant;
	}	

}
