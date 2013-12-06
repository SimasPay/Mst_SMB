package com.mfino.mce.bankteller;

import com.mfino.mce.core.util.BackendResponse;

public class TellerBackendResponse extends BackendResponse 
{
	private Long sourcePocketID;
	private Long destPocketID;
	private Long endDestPocketID;
	private String  pin;
	
	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public Long getSourcePocketID() {
		return sourcePocketID;
	}

	public void setSourcePocketID(Long sorucePocketID) {
		this.sourcePocketID = sorucePocketID;
	}

	public Long getDestPocketID() {
		return destPocketID;
	}

	public void setDestPocketID(Long destPocketID) {
		this.destPocketID = destPocketID;
	}

	public Long getEndDestPocketID() {
		return endDestPocketID;
	}

	public void setEndDestPocketID(Long endDestPocketID) {
		this.endDestPocketID = endDestPocketID;
	}	
}
