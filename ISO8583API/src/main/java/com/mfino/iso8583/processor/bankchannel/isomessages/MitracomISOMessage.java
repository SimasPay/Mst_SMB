package com.mfino.iso8583.processor.bankchannel.isomessages;

import com.mfino.fix.CmFinoFIX;
import com.solab.iso8583.IsoMessage;


//FIXME mitracom and artajasa have the same ISO spec
public class MitracomISOMessage extends ArtajasaISOMessage {

	public MitracomISOMessage(IsoMessage isoMsg) throws Exception{
		super(isoMsg);
	}
	private static final String ISOVariant = CmFinoFIX.ISO8583_Variant_Mitracom_Gateway_Interface;
	public String getISOVariant() {
		return ISOVariant;
	}
}
