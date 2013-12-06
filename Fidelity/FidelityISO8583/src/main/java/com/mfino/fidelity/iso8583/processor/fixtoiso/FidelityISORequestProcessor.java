package com.mfino.fidelity.iso8583.processor.fixtoiso;

import java.util.Map;

import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;
import com.mfino.iso8583.definitions.fixtoiso.IFixToIsoProcessor;

public abstract class FidelityISORequestProcessor implements IFixToIsoProcessor {

	protected ISOMsg	          isoMsg	= new ISOMsg();

	protected Map<String, String>	constantFieldsMap;
	
	private FixToISOService fixToISOService;
	
	public void setConstantFieldsMap(Map<String, String> map) {
		this.constantFieldsMap = map;
	}
	
	public void setFixToISOService(FixToISOService fixToISOService) {
		this.fixToISOService = fixToISOService;
	}

	
	public abstract ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException;
	
	public String getTransactionDiscription(CMBase msg,boolean setReconcilationID) {
		return fixToISOService.getTransactionDiscription(msg, setReconcilationID);
	}

}
