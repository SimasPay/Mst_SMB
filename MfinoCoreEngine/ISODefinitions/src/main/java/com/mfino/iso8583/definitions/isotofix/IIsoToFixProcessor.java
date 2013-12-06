package com.mfino.iso8583.definitions.isotofix;

import org.jpos.iso.ISOMsg;

import com.mfino.fix.CFIXMsg;
import com.mfino.iso8583.definitions.exceptions.InvalidIsoElementException;


public interface IIsoToFixProcessor {
	
	public CFIXMsg process(ISOMsg isoMsg,CFIXMsg request) throws InvalidIsoElementException;

}
