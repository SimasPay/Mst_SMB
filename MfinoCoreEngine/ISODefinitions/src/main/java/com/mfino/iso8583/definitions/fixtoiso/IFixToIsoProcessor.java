package com.mfino.iso8583.definitions.fixtoiso;

import java.util.Map;

import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.iso8583.definitions.exceptions.AllElementsNotAvailableException;


public interface IFixToIsoProcessor {
	
	public void setConstantFieldsMap(Map<String,String> map);
	
	public ISOMsg process(CFIXMsg fixmsg) throws AllElementsNotAvailableException;

}
